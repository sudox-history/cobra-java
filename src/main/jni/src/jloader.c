#include <jni.h>
#include <stdlib.h>
#include "jloader.h"

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_CobraLoader_loadNative(JNIEnv *env, jclass class) {
    struct jloader_data *data = malloc(sizeof(struct jloader_data));
    jclass socket_class = (*env)->FindClass(env, "ru/sudox/cobra/socket/CobraSocket");

    data->socket_class = socket_class;
    data->socket_serverside_constructor_id = (*env)->GetMethodID(env, socket_class, "<init>", "(J)V");
    data->on_connect_method_id = (*env)->GetMethodID(env, socket_class, "onConnect", "()V");
    data->on_close_method_id = (*env)->GetMethodID(env, socket_class, "onClose", "(I)V");
    data->on_data_method_id = (*env)->GetMethodID(env, socket_class, "onData", "(Ljava/nio/ByteBuffer;)V");

    jclass server_class = (*env)->FindClass(env, "ru/sudox/cobra/server/CobraServer");
    data->on_server_close_method_id = (*env)->GetMethodID(env, server_class, "onServerClose", "(I)V");
    data->on_server_connection_method_id = (*env)->GetMethodID(env, server_class, "onConnectionOpen",
                                                               "(Lru/sudox/cobra/socket/CobraSocket;)V");

    return (jlong) data;
}