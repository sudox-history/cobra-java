#ifndef COBRA_JAVA_JSOCKET_H
#define COBRA_JAVA_JSOCKET_H

typedef struct socket_bindings_data sock_bind_data;

struct socket_bindings_data {
    jobject ref;
    uv_sem_t *sem;
    JNIEnv *env;
};

void init_cobra_socket(cobra_socket_t *socket);

void link_cobra_socket(JNIEnv *env, jobject object, sock_bind_data *bind_data);

#endif //COBRA_JAVA_JSOCKET_H