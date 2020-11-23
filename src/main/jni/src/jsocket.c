#include <jni.h>
#include <cobra.h>
#include <stdlib.h>

#include "jloader.h"
#include "jsocket.h"

void on_socket_connect(cobra_socket_t *socket) {
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);

    (*bind_data->env)->CallVoidMethod(
            bind_data->env,
            bind_data->ref,
            bind_data->loader_data->on_connect_method_id
    );
}

void on_socket_close(cobra_socket_t *socket, int error) {
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);

    (*bind_data->env)->CallVoidMethod(
            bind_data->env,
            bind_data->ref,
            bind_data->loader_data->on_close_method_id,
            error
    );

    (*bind_data->env)->DeleteGlobalRef(bind_data->env, bind_data->ref);
}

void on_socket_alloc(cobra_socket_t *socket, uint8_t **data, uint64_t length) {
    *data = malloc(length);
}

void on_socket_data(cobra_socket_t *socket, uint8_t *data, uint64_t length) {
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);
    jobject buffer_obj = (*bind_data->env)->NewDirectByteBuffer(bind_data->env, data, length);

    (*bind_data->env)->CallVoidMethod(
            bind_data->env,
            bind_data->ref,
            bind_data->loader_data->on_data_method_id,
            buffer_obj
    );
}

void on_socket_drain(cobra_socket_t *socket) {
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);

    (*bind_data->env)->CallVoidMethod(
            bind_data->env,
            bind_data->ref,
            bind_data->loader_data->on_drain_method_id
    );
}

void init_cobra_socket(cobra_socket_t *socket, jloader_data *loader_data) {
    sock_bind_data *bind_data = malloc(sizeof(sock_bind_data));
    bind_data->loader_data = loader_data;

    cobra_socket_set_data(socket, bind_data);
    cobra_socket_set_callbacks(
            socket,
            on_socket_connect,
            on_socket_close,
            on_socket_alloc,
            on_socket_data,
            on_socket_drain
    );
}

void link_cobra_socket(JNIEnv *env, jobject object, sock_bind_data *bind_data) {
    bind_data->ref = (*env)->NewGlobalRef(env, object);
    bind_data->env = env;
}

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_create(JNIEnv *env, jclass class, jlong loader_pointer,
                                                      jint write_queue_size) {

    cobra_socket_t *socket = cobra_socket_create(write_queue_size);
    jloader_data *loader_data = (jloader_data *) loader_pointer;
    init_cobra_socket(socket, loader_data);

    return (jlong) socket;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_connect(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                       jstring port) {

    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);

    link_cobra_socket(env, object, bind_data);

    char *host_chars = (char *) (*env)->GetStringUTFChars(env, host, NULL);
    char *port_chars = (char *) (*env)->GetStringUTFChars(env, port, NULL);
    int status = cobra_socket_connect(socket, host_chars, port_chars);

    if (status != COBRA_SOCKET_OK) {
        (*env)->DeleteGlobalRef(env, bind_data->ref);
    }

    (*env)->ReleaseStringUTFChars(env, host, host_chars);
    (*env)->ReleaseStringUTFChars(env, port, port_chars);

    return status;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_send(JNIEnv *env, jclass class, jlong pointer, jobject buffer) {
    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    int buffer_length = (*env)->GetDirectBufferCapacity(env, buffer);
    void *address = (*env)->GetDirectBufferAddress(env, buffer);

    return cobra_socket_send(socket, address, buffer_length);
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_close(JNIEnv *env, jclass class, jlong pointer) {
    return cobra_socket_close((cobra_socket_t *) pointer);
}

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_destroy(JNIEnv *env, jclass class, jlong pointer) {
    cobra_socket_t *socket = (cobra_socket_t *) pointer;
    sock_bind_data *bind_data = (sock_bind_data *) cobra_socket_get_data(socket);

    cobra_socket_destroy(socket);
    free(bind_data);
}