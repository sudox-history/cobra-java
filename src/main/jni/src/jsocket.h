#ifndef COBRA_JAVA_JSOCKET_H
#define COBRA_JAVA_JSOCKET_H

typedef struct socket_bindings_data sock_bind_data;

struct socket_bindings_data {
    jobject ref;
    jloader_data *loader_data;
    bool can_detach_from_thread;
    JNIEnv *env;
};

void init_cobra_socket(cobra_socket_t *socket, jloader_data *loader_data, JNIEnv *env);

void link_cobra_socket(JNIEnv *env, jobject object, sock_bind_data *bind_data, bool can_detach);

#endif //COBRA_JAVA_JSOCKET_H