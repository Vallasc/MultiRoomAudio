javac -d out -cp "./lib;./lib/*" ./src/main/*.java
copy .\lib\jcef.jar .\out
java -cp "./out;./out/*;" -Djava.library.path=./lib/win64 main.Main