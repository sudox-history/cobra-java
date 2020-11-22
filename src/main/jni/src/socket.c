#include <jni.h>
#include <cobra.h>
#include <stdlib.h>
#include <uv.h>

static jmethodID on_connect_method_id;
static jmethodID on_close_method_id;
static jmethodID on_data_method_id;

struct socket_bindings_data {
    jobject ref;
    uv_sem_t *sem;
    JNIEnv *env;
    int write_queue_size;
};

void on_socket_connect(cobra_socket_t *socket) {
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);
    (*bindings_data->env)->CallVoidMethod(bindings_data->env, bindings_data->ref, on_connect_method_id);
}

void on_socket_close(cobra_socket_t *socket, int error) {
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);
    (*bindings_data->env)->CallVoidMethod(bindings_data->env, bindings_data->ref, on_close_method_id, error);
    (*bindings_data->env)->DeleteGlobalRef(bindings_data->env, bindings_data->ref);
}

void on_socket_alloc(cobra_socket_t *socket, uint8_t **data, uint64_t length) {
    *data = malloc(length);
}

void on_socket_data(cobra_socket_t *socket, uint8_t *data, uint64_t length) {
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);
    jobject buffer_obj = (*bindings_data->env)->NewDirectByteBuffer(bindings_data->env, data, length);
    (*bindings_data->env)->CallVoidMethod(bindings_data->env, bindings_data->ref, on_data_method_id, buffer_obj);
}

void on_socket_drain(cobra_socket_t *socket) {
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);
    uv_sem_post(bindings_data->sem);
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "OCUnusedGlobalDeclarationInspection"

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_load(JNIEnv *env, jclass class) {
    on_connect_method_id = (*env)->GetMethodID(env, class, "onConnect", "()V");
    on_close_method_id = (*env)->GetMethodID(env, class, "onClose", "(I)V");
    on_data_method_id = (*env)->GetMethodID(env, class, "onData", "(Ljava/nio/ByteBuffer;)V");
}

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_create(JNIEnv *env, jclass class, jint write_queue_size) {
    cobra_socket_t *socket = cobra_socket_create(write_queue_size);
    struct socket_bindings_data *bindings_data = malloc(sizeof(struct socket_bindings_data));

    bindings_data->write_queue_size = write_queue_size;
    bindings_data->sem = malloc(sizeof(uv_sem_t));

    uv_sem_init(bindings_data->sem, 1);

    cobra_socket_set_data(socket, bindings_data);
    cobra_socket_set_callbacks(
            socket,
            on_socket_connect,
            on_socket_close,
            on_socket_alloc,
            on_socket_data,
            on_socket_drain
    );

    return (jlong) socket;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_connect(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                       jstring port) {

    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);

    bindings_data->ref = (*env)->NewGlobalRef(env, object);
    bindings_data->env = env;

    char *host_chars = (char *) (*env)->GetStringUTFChars(env, host, NULL);
    char *port_chars = (char *) (*env)->GetStringUTFChars(env, port, NULL);
    int status = cobra_socket_connect(socket, host_chars, port_chars);

    if (status == COBRA_SOCKET_ERR_ALREADY_CONNECTED) {
        (*env)->DeleteGlobalRef(env, bindings_data->ref);
    }

    (*env)->ReleaseStringUTFChars(env, host, host_chars);
    (*env)->ReleaseStringUTFChars(env, port, port_chars);

    return status;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_send(JNIEnv *env, jclass class, jlong pointer, jobject buffer) {
    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);

    int buffer_length = (*env)->GetDirectBufferCapacity(env, buffer);
    void *address = (*env)->GetDirectBufferAddress(env, buffer);

    while (1) {
        int status = cobra_socket_send(socket, address, buffer_length);

        if (status == COBRA_SOCKET_ERR_QUEUE_OVERFLOW) {
            uv_sem_wait(bindings_data->sem);
            continue;
        }

        return status;
    }
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_close(JNIEnv *env, jclass class, jlong pointer) {
    return cobra_socket_close((cobra_socket_t *) pointer);
}

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_destroy(JNIEnv *env, jclass class, jlong pointer) {
    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    struct socket_bindings_data *bindings_data = (struct socket_bindings_data *) cobra_socket_get_data(socket);

    uv_sem_destroy(bindings_data->sem);
    cobra_socket_destroy(socket);
    free(bindings_data);
}

#pragma clang diagnostic pop