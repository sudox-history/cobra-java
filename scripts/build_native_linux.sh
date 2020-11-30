cd src/main/jni
mkdir output
mkdir cmake-build-release
cd cmake-build-release

cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_C_FLAGS="-m64 -fPIC" ..
cmake --build . --config Release -j
mv libcobra_java.so ../output/cobra_linux_x86_64.so
rm CMakeCache.txt
