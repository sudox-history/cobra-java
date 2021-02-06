#ifndef COBRA_JAVA_JSERVER_HPP
#define COBRA_JAVA_JSERVER_HPP

typedef struct server_bindings_data srv_bind_data;

struct server_bindings_data {
    JNIEnv *env;
    jobject ref;
    jloader_data *loader_data;
};

#endif //COBRA_JAVA_JSERVER_HPP