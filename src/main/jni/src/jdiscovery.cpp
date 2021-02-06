#include <jni.h>
#include <cobra.h>

#include "jloader.hpp"
#include "jdiscovery.hpp"

void discovery_attach_thread_if_need(disc_data *bind_data) {
    if (bind_data->env == nullptr) {
        bind_data->loader_data->vm->AttachCurrentThread(reinterpret_cast<void **>(&bind_data->env), nullptr);
    }
}

void discovery_on_found(cobra_discovery_t *discovery, char *host) {
    auto *dsc_data = static_cast<disc_data *>(cobra_discovery_get_data(discovery));
    discovery_attach_thread_if_need(dsc_data);

    dsc_data->env->CallVoidMethod(
            dsc_data->ref,
            dsc_data->loader_data->on_discovery_found_method_id,
            dsc_data->env->NewStringUTF(host)
    );
}

void discovery_on_close(cobra_discovery_t *discovery, cobra_discovery_err_t error) {
    auto *dsc_data = static_cast<disc_data *>(cobra_discovery_get_data(discovery));
    discovery_attach_thread_if_need(dsc_data);

    dsc_data->env->CallVoidMethod(
            dsc_data->ref,
            dsc_data->loader_data->on_discovery_close_method_id,
            static_cast<jlong>(error)
    );

    dsc_data->env->DeleteGlobalRef(dsc_data->ref);
    dsc_data->loader_data->vm->DetachCurrentThread();
    dsc_data->env = nullptr;
}

void discovery_on_address_found(cobra_discovery_t *discovery, char *host) {
    auto *data = static_cast<finder_data *>(cobra_discovery_get_data(discovery));

    data->env->CallVoidMethod(
            data->ref,
            data->loader_data->on_discovery_address_found_method_id,
            data->env->NewStringUTF(host)
    );
}

disc_data *prepare_discovery(JNIEnv *env, jobject object, cobra_discovery_t *discovery) {
    auto *dsc_data = static_cast<disc_data *>(cobra_discovery_get_data(discovery));
    dsc_data->ref = env->NewGlobalRef(object);
    dsc_data->env = nullptr;

    return dsc_data;
}

extern "C"
JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_create(JNIEnv *env, jclass clazz, jlong loader_pointer) {
    auto *discovery = cobra_discovery_create();
    auto *discovery_data = new disc_data;
    discovery_data->loader_data = reinterpret_cast<jloader_data *>(loader_pointer);

    cobra_discovery_set_data(discovery, discovery_data);
    cobra_discovery_set_callbacks(discovery, discovery_on_found, discovery_on_close);

    return reinterpret_cast<jlong>(discovery);
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_scan(JNIEnv *env, jobject object, jlong pointer) {
    auto *discovery = (cobra_discovery_t *) pointer;
    auto *dsc_data = prepare_discovery(env, object, discovery);
    int res = cobra_discovery_scan(discovery);

    if (res != COBRA_DISCOVERY_OK) {
        env->DeleteGlobalRef(dsc_data->ref);
    }

    return res;
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_listen(JNIEnv *env, jobject object, jlong pointer) {
    auto *discovery = reinterpret_cast<cobra_discovery_t *>(pointer);
    auto *dsc_data = prepare_discovery(env, object, discovery);
    int res = cobra_discovery_listen(discovery);

    if (res != COBRA_DISCOVERY_OK) {
        env->DeleteGlobalRef(dsc_data->ref);
    }

    return res;
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_close(JNIEnv *env, jclass clazz, jlong pointer) {
    return cobra_discovery_close(reinterpret_cast<cobra_discovery_t *>(pointer));
}

extern "C"
JNIEXPORT void
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_destroy(JNIEnv *env, jclass clazz, jlong pointer) {
    auto *discovery = reinterpret_cast<cobra_discovery_t *>(pointer);
    delete static_cast<disc_data *>(cobra_discovery_get_data(discovery));

    cobra_discovery_destroy(discovery);
}

extern "C"
JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_discovery_finder_CobraDiscoveryInterfaceFinder_create(JNIEnv *env, jclass clazz,
                                                                                  jlong loader_pointer) {

    auto *discovery = cobra_discovery_create();
    auto *data = new finder_data;

    data->loader_data = (jloader_data *) loader_pointer;
    cobra_discovery_set_data(discovery, data);

    return reinterpret_cast<jlong>(discovery);
}

extern "C"
JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_finder_CobraDiscoveryInterfaceFinder_find(JNIEnv *env, jobject object,
                                                                                jlong pointer) {
    auto *discovery = reinterpret_cast<cobra_discovery_t *>(pointer);
    auto *data = static_cast<finder_data *>(cobra_discovery_get_data(discovery));
    data->ref = env->NewLocalRef(object);
    data->env = env;

    return cobra_discovery_get_addresses(discovery, discovery_on_address_found);
}

extern "C"
JNIEXPORT void
JNICALL Java_ru_sudox_cobra_discovery_finder_CobraDiscoveryInterfaceFinder_destroy(JNIEnv *env, jclass clazz,
                                                                                   jlong pointer) {
    auto *discovery = reinterpret_cast<cobra_discovery_t *>(pointer);
    delete static_cast<finder_data *>(cobra_discovery_get_data(discovery));

    cobra_discovery_destroy(discovery);
}