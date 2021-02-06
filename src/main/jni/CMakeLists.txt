cmake_minimum_required(VERSION 3.10)
project(cobra-java)

# libcobra
add_subdirectory(deps/libcobra)

# bindings
add_library(cobra_java SHARED
        src/jsocket.cpp
        src/jsocket.hpp
        src/jserver.cpp
        src/jserver.hpp
        src/jloader.cpp
        src/jloader.hpp
        src/jdiscovery.cpp
        src/jdiscovery.hpp
)

target_link_libraries(cobra_java cobra_a)

if (NOT ANDROID)
    find_package(JNI REQUIRED)
    target_link_libraries(cobra_java ${JNI_LIBRARIES})
    include_directories(${JNI_INCLUDE_DIRS})
endif ()