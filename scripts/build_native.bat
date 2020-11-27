@echo off

cd src\main\jni
rmdir mkdir
mkdir output
mkdir cmake-build-release
cd cmake-build-release

cmake -DCMAKE_BUILD_TYPE=Release .. -A Win32
cmake --build . --config Release -j %NUMBER_OF_PROCESSORS%
move /Y Release\cobra_java.dll ..\output\cobra_win_x86.dll
del CMakeCache.txt

cmake -DCMAKE_BUILD_TYPE=Release .. -A x64
cmake --build . --config Release -j %NUMBER_OF_PROCESSORS%
move /Y Release\cobra_java.dll ..\output\cobra_win_x86_64.dll
del CMakeCache.txt

cmake -DCMAKE_BUILD_TYPE=Release .. -A ARM
cmake --build . --config Release -j %NUMBER_OF_PROCESSORS%
move /Y Release\cobra_java.dll ..\output\cobra_win_arm.dll
del CMakeCache.txt

cmake -DCMAKE_BUILD_TYPE=Release .. -A ARM64
cmake --build . --config Release -j %NUMBER_OF_PROCESSORS%
move /Y Release\cobra_java.dll ..\output\cobra_win_arm64.dll
del CMakeCache.txt