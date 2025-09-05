# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 4.1.0)

project(touchbubbles)
# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add.library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -O3 -ffast-math")
add_library( # Specifies the name of the library.
        bubblemover
             # Sets the library as a shared library.
             SHARED
             # Provides a relative path to your source file(s).
             jnibubbles.cpp )
target_link_libraries( bubblemover log)
