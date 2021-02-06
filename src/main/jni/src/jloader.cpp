#include <jni.h>
#include "jloader.hpp"

extern "C"
JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_CobraLoader_loadNative(JNIEnv *env, jclass clazz) {
    auto data = new jloader_data;
    auto socket_class = env->FindClass("ru/sudox/cobra/socket/CobraSocket");

    data->socket_class = reinterpret_cast<jclass>(env->NewGlobalRef(socket_class));
    data->socket_serverside_constructor_id = env->GetMethodID(socket_class, "<init>", "(J)V");
    data->on_connect_method_id = env->GetMethodID(socket_class, "onConnect", "()V");
    data->on_close_method_id = env->GetMethodID(socket_class, "onClose", "(I)V");
    data->on_data_method_id = env->GetMethodID(socket_class, "onData", "(Ljava/nio/ByteBuffer;)V");
    data->on_drain_method_id = env->GetMethodID(socket_class, "onDrain", "()V");

    auto server_class = env->FindClass("ru/sudox/cobra/server/CobraServer");
    data->on_server_close_method_id = env->GetMethodID(server_class, "onServerClose", "(I)V");
    data->on_server_connection_method_id = env->GetMethodID(server_class, "onConnectionOpen",
                                                            "(Lru/sudox/cobra/socket/CobraSocket;)V");

    auto discovery_class = env->FindClass("ru/sudox/cobra/discovery/CobraDiscovery");
    data->on_discovery_found_method_id = env->GetMethodID(discovery_class, "onFound", "(Ljava/lang/String;)V");
    data->on_discovery_close_method_id = env->GetMethodID(discovery_class, "onClose", "(I)V");

    auto discovery_finder_class = env->FindClass("ru/sudox/cobra/discovery/finder/CobraDiscoveryInterfaceFinder");
    data->on_discovery_address_found_method_id = env->GetMethodID(discovery_finder_class, "onFound",
                                                                  "(Ljava/lang/String;)V");

    env->GetJavaVM(&data->vm);

    return reinterpret_cast<jlong>(data);
}