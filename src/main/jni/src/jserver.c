#include <jni.h>
#include <cobra.h>
#include <stdlib.h>

#include "jloader.h"
#include "jserver.h"
#include "jsocket.h"

void on_server_connection(cobra_server_t *server, cobra_socket_t *socket) {
    srv_bind_data *sv_bind_data = (srv_bind_data *) cobra_server_get_data(server);
    init_cobra_socket(socket, sv_bind_data->loader_data);

    sock_bind_data *cl_bind_data = (sock_bind_data *) cobra_socket_get_data(socket);
    jobject cl_object = (*sv_bind_data->env)->NewObject(
            sv_bind_data->env,
            sv_bind_data->loader_data->socket_class,
            sv_bind_data->loader_data->socket_serverside_constructor_id,
            (jlong) socket
    );

    link_cobra_socket(sv_bind_data->env, cl_object, cl_bind_data);

    (*sv_bind_data->env)->CallVoidMethod(
            sv_bind_data->env,
            sv_bind_data->ref,
            sv_bind_data->loader_data->on_server_connection_method_id,
            cl_object
    );
}

void on_server_close(cobra_server_t *server, int error) {
    srv_bind_data *bind_data = (srv_bind_data *) cobra_server_get_data(server);

    (*bind_data->env)->CallVoidMethod(
            bind_data->env,
            bind_data->ref,
            bind_data->loader_data->on_server_close_method_id,
            error
    );

    (*bind_data->env)->DeleteGlobalRef(bind_data->env, bind_data->ref);
}

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_server_CobraServer_create(JNIEnv *env, jclass class, jlong loader_pointer,
                                                      jint write_queue_size) {

    cobra_server_t *server = cobra_server_create(write_queue_size);
    srv_bind_data *bind_data = malloc(sizeof(srv_bind_data));
    bind_data->loader_data = (jloader_data *) loader_pointer;

    cobra_server_set_data(server, bind_data);
    cobra_server_set_callbacks(server, on_server_connection, on_server_close);

    return (jlong) server;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_server_CobraServer_listen(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                      jstring port) {

    cobra_server_t *server = (cobra_server_t *) pointer;
    srv_bind_data *bind_data = (srv_bind_data *) cobra_server_get_data(server);

    bind_data->env = env;
    bind_data->ref = (*env)->NewGlobalRef(env, object);

    char *host_chars = (char *) (*env)->GetStringUTFChars(env, host, NULL);
    char *port_chars = (char *) (*env)->GetStringUTFChars(env, port, NULL);

    int status = cobra_server_listen(server, host_chars, port_chars);

    if (status != COBRA_SERVER_OK) {
        (*env)->DeleteGlobalRef(env, bind_data->ref);
    }

    (*env)->ReleaseStringUTFChars(env, host, host_chars);
    (*env)->ReleaseStringUTFChars(env, port, port_chars);

    return status;
}

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_server_CobraServer_destroy(JNIEnv *env, jclass class, jlong pointer) {
    cobra_server_t *server = (cobra_server_t *) pointer;
    free(cobra_server_get_data(server));
    cobra_server_destroy(server);
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_server_CobraServer_close(JNIEnv *env, jlong pointer) {
    return cobra_server_close((cobra_server_t *) pointer);
}