set type=%1

SHIFT
set after1=
:loop
if "%1" == "" goto end
set after1=%after1% %1
SHIFT
goto loop
:end

echo %after1%

set JAR="target\multi_room_audio_client-0.0.1-jar-with-dependencies.jar"
IF "%type%"=="server" (
    java -cp %JAR% it.unibo.sca.multiroomaudio.server.ServerMain %after1%
)
IF "%type%"=="client" (
    java -cp %JAR% it.unibo.sca.multiroomaudio.client.ClientMain
)
IF "%type%"=="speaker" (
    java -cp %JAR% it.unibo.sca.multiroomaudio.speaker.SpeakerMain
)