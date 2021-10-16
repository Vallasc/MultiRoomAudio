#!/bin/bash

javac -d out -cp "./lib;./lib/*" ./src/main/*.java

cp ./lib/jcef.jar ./out

# Determine the absolute path to the library directory.
export LIB_PATH=$(readlink -f "./lib/linux64")

# Necessary for jcef_helper to find libcef.so.
if [ -n "$LD_LIBRARY_PATH" ]; then
  LD_LIBRARY_PATH=$LIB_PATH:${LD_LIBRARY_PATH}
else
  LD_LIBRARY_PATH=$LIB_PATH
fi
export LD_LIBRARY_PATH

# Preload libcef.so to avoid crashes.
LD_PRELOAD=$LIB_PATH/libcef.so java -cp "./out;./out/*;" -Djava.library.path=$LIB_PATH main.Main "$@"