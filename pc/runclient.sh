#!/bin/bash
rm -rf outClient
mkdir outClient
javac -d outClient ./src/it/unibo/sca/multiroomaudio/ClientMain.java ./src/it/unibo/sca/multiroomaudio/shared/messages/*.java ./src/it/unibo/sca/multiroomaudio/shared/dto/*.java ./src/it/unibo/sca/multiroomaudio/shared/IPFinder.java ./src/it/unibo/sca/multiroomaudio/shared/Couple.java

java -cp "./outClient:./outClient/*" it.unibo.sca.multiroomaudio.ClientMain "$@"