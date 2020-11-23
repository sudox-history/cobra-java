#ifndef COBRA_JAVA_JSERVER_H
#define COBRA_JAVA_JSERVER_H

typedef struct server_bindings_data srv_bind_data;

struct server_bindings_data {
    JNIEnv *env;
    jobject ref;
    jloader_data *loader_data;
};

#endif //COBRA_JAVA_JSERVER_H