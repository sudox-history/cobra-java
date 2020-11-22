#include <jni.h>
#include <stdlib.h>
#include "jloader.h"

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_CobraLoader_loadNative(JNIEnv *env, jclass class) {
    struct jloader_data* data = malloc(sizeof(struct jloader_data));

    jclass socket_class = (*env)->FindClass(env, "ru/sudox/cobra/socket/CobraSocket");
    data->on_data_method_id = (*env)->GetMethodID(env, socket_class, "onConnect", "()V");
    data->on_close_method_id = (*env)->GetMethodID(env, socket_class, "onClose", "(I)V");
    data->on_data_method_id = (*env)->GetMethodID(env, socket_class, "onData", "(Ljava/nio/ByteBuffer;)V");

    return (jlong) data;
}