javac -d outServer ./src/it/unibo/sca/multiroomaudio/ServerMain.java ^
./src/it/unibo/sca/multiroomaudio/server_pkg/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/messages/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/dto/*.java ^
./src/it/unibo/sca/multiroomaudio/shared/IPFinder.java

java -cp "./outServer;./outServer/*;" it.unibo.sca.multiroomaudio.ServerMain