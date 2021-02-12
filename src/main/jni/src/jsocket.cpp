#include <jni.h>
#include <cobra.h>
#include <unordered_map>

#include "jloader.hpp"
#include "jsocket.hpp"

void socket_attach_thread_if_need(sock_bind_data *bind_data) {
    if (bind_data->env == nullptr) {
        bind_data->loader_data->vm->AttachCurrentThread(reinterpret_cast<void **>(&bind_data->env), nullptr);
    }
}

void on_socket_connect(cobra_socket_t *socket) {
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    socket_attach_thread_if_need(bind_data);

    bind_data->env->CallVoidMethod(bind_data->ref, bind_data->loader_data->on_connect_method_id);
}

void on_socket_close(cobra_socket_t *socket, cobra_socket_err_t error) {
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    socket_attach_thread_if_need(bind_data);

    bind_data->env->CallVoidMethod(bind_data->ref, bind_data->loader_data->on_close_method_id, error);
    bind_data->env->DeleteGlobalRef(bind_data->ref);

    if (bind_data->can_detach_from_thread) {
        bind_data->loader_data->vm->DetachCurrentThread();
        bind_data->env = nullptr;
    }
}

void on_socket_alloc(cobra_socket_t *socket, uint8_t **data, uint64_t length) {
    *data = new uint8_t[length];
}

void on_socket_data(cobra_socket_t *socket, uint8_t *data, uint64_t length) {
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    socket_attach_thread_if_need(bind_data);

    bind_data->env->CallVoidMethod(
            bind_data->ref,
            bind_data->loader_data->on_data_method_id,
            bind_data->env->NewDirectByteBuffer(data, length)
    );
}

void on_socket_drain(cobra_socket_t *socket) {
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    socket_attach_thread_if_need(bind_data);

    bind_data->env->CallVoidMethod(bind_data->ref, bind_data->loader_data->on_drain_method_id);
}

void on_socket_write(cobra_socket_t *socket, uint8_t *data, uint64_t length, cobra_socket_err_t error) {
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    auto reference = bind_data->buffers_map.at(data);
    socket_attach_thread_if_need(bind_data);

    if (reference != nullptr) {
        bind_data->env->DeleteGlobalRef(reference);
        bind_data->buffers_map.erase(data);
    }
}

void init_cobra_socket(cobra_socket_t *socket, jloader_data *loader_data, JNIEnv *env) {
    auto *bind_data = new sock_bind_data;
    bind_data->loader_data = loader_data;
    bind_data->env = env;

    cobra_socket_set_data(socket, bind_data);
    cobra_socket_set_callbacks(
            socket,
            on_socket_connect,
            on_socket_close,
            on_socket_alloc,
            on_socket_data,
            on_socket_write,
            on_socket_drain
    );
}

void link_cobra_socket(JNIEnv *env, jobject object, sock_bind_data *bind_data, bool can_detach) {
    bind_data->ref = env->NewGlobalRef(object);
    bind_data->can_detach_from_thread = can_detach;
}

extern "C"
JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_create(JNIEnv *env, jclass clazz, jlong loader_pointer,
                                                      jint write_queue_size) {

    cobra_socket_t *socket = cobra_socket_create(write_queue_size);
    auto *loader_data = reinterpret_cast<jloader_data *>(loader_pointer);
    init_cobra_socket(socket, loader_data, nullptr);

    return (jlong) socket;
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_connect(JNIEnv *env, jobject object, jlong pointer, jstring host,
                                                       jstring port) {

    auto *socket = reinterpret_cast<cobra_socket_t *>(pointer);
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));

    link_cobra_socket(env, object, bind_data, true);

    char *host_chars = const_cast<char *>(env->GetStringUTFChars(host, nullptr));
    char *port_chars = const_cast<char *>(env->GetStringUTFChars(port, nullptr));
    cobra_socket_err_t status = cobra_socket_connect(socket, host_chars, port_chars);

    if (status != COBRA_SOCKET_OK) {
        env->DeleteGlobalRef(bind_data->ref);
    }

    env->ReleaseStringUTFChars(host, host_chars);
    env->ReleaseStringUTFChars(port, port_chars);

    return status;
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_write(JNIEnv *env, jclass clazz, jlong pointer, jobject buffer) {
    auto *socket = reinterpret_cast<cobra_socket_t *>(pointer);
    auto *bind_data = static_cast<sock_bind_data *>(cobra_socket_get_data(socket));
    auto *address = static_cast<uint8_t *>(env->GetDirectBufferAddress(buffer));
    int buffer_length = env->GetDirectBufferCapacity(buffer);

    if (buffer_length > 0) {
        bind_data->buffers_map.insert(std::pair<uint8_t *, jobject>(address, env->NewGlobalRef(buffer)));
    }

    return cobra_socket_write(socket, address, buffer_length);
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_close(JNIEnv *env, jclass clazz, jlong pointer) {
    return cobra_socket_close((cobra_socket_t *) pointer);
}

extern "C"
JNIEXPORT void
JNICALL Java_ru_sudox_cobra_socket_CobraSocket_destroy(JNIEnv *env, jclass clazz, jlong pointer) {
    auto *socket = reinterpret_cast<cobra_socket_t *>(pointer);
    delete static_cast<sock_bind_data *>(cobra_socket_get_data(socket));

    cobra_socket_destroy(socket);
}