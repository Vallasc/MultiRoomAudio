javac -d out -cp "./lib;./lib/*" ./src/main/*.java
copy .\lib\*.jar .\out
java -cp "./out;./out/*;" -Djava.library.path=./lib/win64 main.Main