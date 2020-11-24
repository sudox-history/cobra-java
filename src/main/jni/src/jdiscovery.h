#ifndef COBRA_JAVA_JDISCOVERY_H
#define COBRA_JAVA_JDISCOVERY_H

typedef struct discovery_data_t disc_data;

struct discovery_data_t {
    jloader_data *loader_data;
    jobject ref;
    JNIEnv *env;
};

#endif
