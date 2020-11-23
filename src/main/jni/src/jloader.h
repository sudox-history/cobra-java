#ifndef COBRA_JAVA_JLOADER_H
#define COBRA_JAVA_JLOADER_H

typedef struct jloader_data jloader_data;

struct jloader_data {
    jclass socket_class;
    jmethodID socket_serverside_constructor_id;
    jmethodID on_server_connection_method_id;
    jmethodID on_server_close_method_id;
    jmethodID on_connect_method_id;
    jmethodID on_close_method_id;
    jmethodID on_data_method_id;
};

#endif //COBRA_JAVA_JLOADER_H
