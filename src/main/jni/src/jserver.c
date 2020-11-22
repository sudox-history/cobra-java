#include <jni.h>
#include <cobra.h>
#include <stdlib.h>

static jmethodID on_connection_open_method_id;
static jmethodID on_server_close_method_id;

struct server_bindings_data {
    JNIEnv *env;
    jobject ref;
};

void on_server_connection(cobra_server_t *server, cobra_socket_t *socket) {

}

void on_server_close(cobra_server_t *server, int error) {
    struct server_bindings_data *bindings_data = (struct server_bindings_data *) cobra_server_get_data(server);
    (*bindings_data->env)->CallVoidMethod(bindings_data->env, bindings_data->ref, on_server_close_method_id, error);
    (*bindings_data->env)->DeleteGlobalRef(bindings_data->env, bindings_data->ref);
}

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_server_CobraServer_init(JNIEnv *env, jclass class) {
    on_server_close_method_id = (*env)->GetMethodID(env, class, "onServerClose", "(I)V");
    on_connection_open_method_id = (*env)->GetMethodID(
            env,
            class,
            "onConnectionOpen",
            "(Lru/sudox/cobra/socket/CobraSocket)V"
    );
}

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_server_CobraServer_create(JNIEnv *env, jclass class, jint write_queue_size) {
    cobra_server_t *server = cobra_server_create(write_queue_size);
    cobra_server_set_data(server, malloc(sizeof(struct server_bindings_data)));
    cobra_server_set_callbacks(server, on_server_connection, on_server_close);

    return (jlong) server;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_server_CobraServer_listen(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                      jstring port) {
    cobra_server_t *server = (cobra_server_t *) pointer;
    struct server_bindings_data *bindings_data = (struct server_bindings_data *) cobra_server_get_data(server);

    bindings_data->env = env;
    bindings_data->ref = (*env)->NewGlobalRef(env, object);

    char *host_chars = (char *) (*env)->GetStringUTFChars(env, host, NULL);
    char *port_chars = (char *) (*env)->GetStringUTFChars(env, port, NULL);

    int res = cobra_server_listen(server, host_chars, port_chars);

    if (res == COBRA_SERVER_ERR_ALREADY_LISTENING) {
        (*bindings_data->env)->DeleteGlobalRef(bindings_data->env, bindings_data->ref);
    }

    (*env)->ReleaseStringUTFChars(env, host, host_chars);
    (*env)->ReleaseStringUTFChars(env, port, port_chars);

    return res;
}