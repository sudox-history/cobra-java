#include <jni.h>
#include <cobra.h>
#include <stdlib.h>

#include "jloader.h"
#include "jdiscovery.h"

void discovery_on_found(cobra_discovery_t *discovery, char *host) {
    disc_data *dsc_data = (disc_data *) cobra_discovery_get_data(discovery);
    jstring host_str = (*dsc_data->env)->NewStringUTF(dsc_data->env, host);

    (*dsc_data->env)->CallVoidMethod(
            dsc_data->env,
            dsc_data->ref,
            dsc_data->loader_data->on_discovery_found_method_id,
            host_str
    );
}

void discovery_on_close(cobra_discovery_t *discovery, int error) {
}

disc_data *prepare_discovery(JNIEnv *env, jobject object, cobra_discovery_t *discovery) {
    disc_data *dsc_data = (disc_data *) cobra_discovery_get_data(discovery);

    dsc_data->ref = (*env)->NewGlobalRef(env, object);
    dsc_data->env = env;

    return dsc_data;
}

JNIEXPORT jlong
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_create(JNIEnv *env, jclass class, jlong loader_pointer) {
    cobra_discovery_t *discovery = cobra_discovery_create();
    disc_data *discovery_data = malloc(sizeof(disc_data));
    discovery_data->loader_data = (jloader_data *) loader_pointer;

    cobra_discovery_set_data(discovery, discovery_data);
    cobra_discovery_set_callbacks(discovery, discovery_on_found, discovery_on_close);

    return (jlong) discovery;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_scan(JNIEnv *env, jobject object, jlong pointer) {
    cobra_discovery_t *discovery = (cobra_discovery_t *) pointer;
    disc_data *dsc_data = prepare_discovery(env, object, discovery);
    int res = cobra_discovery_scan(discovery);

    if (res != COBRA_DISCOVERY_OK) {
        (*env)->DeleteGlobalRef(env, dsc_data->ref);
    }

    return res;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_listen(JNIEnv *env, jobject object, jlong pointer) {
    cobra_discovery_t *discovery = (cobra_discovery_t *) pointer;
    disc_data *dsc_data = prepare_discovery(env, object, discovery);
    int res = cobra_discovery_listen(discovery);

    if (res != COBRA_DISCOVERY_OK) {
        (*env)->DeleteGlobalRef(env, dsc_data->ref);
    }

    return res;
}

JNIEXPORT jint
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_close(JNIEnv *env, jclass class, jlong pointer) {
    return cobra_discovery_close((cobra_discovery_t *) pointer);
}

JNIEXPORT void
JNICALL Java_ru_sudox_cobra_discovery_CobraDiscovery_destroy(JNIEnv *env, jclass class, jlong pointer) {
    cobra_discovery_t *discovery = (cobra_discovery_t *) pointer;
    free(cobra_discovery_get_data(discovery));
    cobra_discovery_destroy(discovery);
}