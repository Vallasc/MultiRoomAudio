javac -d out -cp "./lib;./lib/*" ./src/it/unibo/sca/multiroomaudio/Main.java ^
./src/it/unibo/sca/multiroomaudio/ui/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/messages/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/dto/*.java
copy .\lib\*.jar .\out
java -cp "./out;./out/*;" -Djava.library.path=./lib/win64 it.unibo.sca.multiroomaudio.Main