#!/bin/sh
if [ -z "$1" ]; then
    echo "You must provide wich type of program you would run!"
    echo "Usage: $0 server|client|speaker"
    exit 1
fi

JAR="target/multi_room_audio_client-0.0.1-jar-with-dependencies.jar"

case "$1" in
    "server")
        java -cp "$JAR" it.unibo.sca.multiroomaudio.server.ServerMain
        ;;
    "client")
        sudo java -cp "$JAR" it.unibo.sca.multiroomaudio.client.ClientMain
        ;;
    "speaker")
        java -cp "$JAR" it.unibo.sca.multiroomaudio.speaker.SpeakerMain
        ;;
    *)
    echo "unknown program type $1"
    ;;
esac