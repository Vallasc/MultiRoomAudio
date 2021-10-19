#!/bin/bash
rm -rf out
mkdir out
javac -d out -cp "./lib:./lib/*" ./src/it/unibo/sca/multiroomaudio/*.java ./src/it/unibo/sca/multiroomaudio/ui/*.java ./src/it/unibo/sca/multiroomaudio/shared/*.java
cp ./lib/*.jar ./out

# Determine the absolute path to the library directory.
export LIB_PATH=$(readlink -f "./lib/linux64")

# Necessary for jcef_helper to find libcef.so.
JWT_SO=$(dirname $(dirname $(readlink -f $(which java))))
LD_LIBRARY_PATH=$LIB_PATH:$JWT_SO/lib/
export LD_LIBRARY_PATH

# Preload libcef.so to avoid crashes.
LD_PRELOAD=$LIB_PATH/libcef.so java -cp "./out:./out/*" -Djava.library.path=$LIB_PATH it.unibo.sca.multiroomaudio.Main "$@"