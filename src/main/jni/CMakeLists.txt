cmake_minimum_required(VERSION 3.10)
project(cobra-java C)

set(CMAKE_C_STANDARD 11)

# libcobra
add_subdirectory(deps/libcobra)

# bindings
add_library(cobra_java SHARED src/socket.c)
target_link_libraries(cobra_java cobra_a)

if (NOT ANDROID)
    find_package(JNI REQUIRED)
    target_link_libraries(cobra_java ${JNI_LIBRARIES})
    include_directories(${JNI_INCLUDE_DIRS})
endif ()