#include <jni.h>
#include <cobra.h>

#include "jloader.hpp"
#include "jserver.hpp"
#include "jsocket.hpp"

void server_attach_thread_if_need(srv_bind_data *bind_data) {
    if (bind_data->env == nullptr) {
        bind_data->loader_data->vm->AttachCurrentThread(reinterpret_cast<void **>(&bind_data->env), nullptr);
    }
}

void on_server_connection(cobra_server_t *server, cobra_socket_t *socket) {
    auto *sv_bind_data = static_cast<srv_bind_data *>(cobra_server_get_data(server));

    server_attach_thread_if_need(sv_bind_data);
    init_cobra_socket(socket, sv_bind_data->loader_data, sv_bind_data->env);

    auto *cl_bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    auto cl_object = sv_bind_data->env->NewObject(
            sv_bind_data->loader_data->socket_class,
            sv_bind_data->loader_data->socket_serverside_constructor_id,
            reinterpret_cast<jlong>(socket)
    );

    link_cobra_socket(sv_bind_data->env, cl_object, cl_bind_data, false);

    sv_bind_data->env->CallVoidMethod(
            sv_bind_data->ref,
            sv_bind_data->loader_data->on_server_connection_method_id,
            cl_object
    );
}

void on_server_close(cobra_server_t *server, cobra_server_err_t error) {
    auto *bind_data = static_cast<srv_bind_data *>(cobra_server_get_data(server));

    server_attach_thread_if_need(bind_data);
    bind_data->env->CallVoidMethod(
            bind_data->ref,
            bind_data->loader_data->on_server_close_method_id,
            static_cast<jint>(error)
    );

    bind_data->env->DeleteGlobalRef(bind_data->ref);
    bind_data->loader_data->vm->DetachCurrentThread();
    bind_data->env = nullptr;
}

extern "C"
JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_server_CobraServer_create(JNIEnv *env, jclass clazz, jlong loader_pointer,
                                                      jint write_queue_size) {

    auto *server = cobra_server_create(write_queue_size);
    auto *bind_data = new srv_bind_data;
    bind_data->loader_data = (jloader_data *) loader_pointer;
    bind_data->env = nullptr;

    cobra_server_set_data(server, bind_data);
    cobra_server_set_callbacks(server, on_server_connection, on_server_close);

    return reinterpret_cast<jlong>(server);
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_server_CobraServer_listen(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                      jstring port) {

    auto *server = (cobra_server_t *) pointer;
    auto *bind_data = static_cast<srv_bind_data *>(cobra_server_get_data(server));
    bind_data->ref = env->NewGlobalRef(object);

    auto *host_chars = const_cast<char *>(env->GetStringUTFChars(host, nullptr));
    auto *port_chars = const_cast<char *>(env->GetStringUTFChars(port, nullptr));
    int status = cobra_server_listen(server, host_chars, port_chars);

    if (status != COBRA_SERVER_OK) {
        env->DeleteGlobalRef(bind_data->ref);
    }

    env->ReleaseStringUTFChars(host, host_chars);
    env->ReleaseStringUTFChars(port, port_chars);

    return status;
}

extern "C"
JNIEXPORT void
JNICALL Java_ru_sudox_cobra_server_CobraServer_destroy(JNIEnv *env, jclass clazz, jlong pointer) {
    auto *server = reinterpret_cast<cobra_server_t *>(pointer);
    delete static_cast<srv_bind_data *>(cobra_server_get_data(server));

    cobra_server_destroy(server);
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_server_CobraServer_close(JNIEnv *env, jlong pointer) {
    return cobra_server_close(reinterpret_cast<cobra_server_t *>(pointer));
}