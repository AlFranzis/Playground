
javah -classpath bin/ -jni al.franzis.JNIHelloWorld

gcc -shared -fPIC -I/usr/java/jdk1.6.0_26/include -I/usr/java/jdk1.6.0_26/include/linux helloworld.c -o libhello.so

LD_LIBRARY_PATH=/home/alex/dev/ecworkspace_3.7_ee/JNIHelloWorld/
export LD_LIBRARY_PATH

if fanotify should work execute Java with root rights!
java -cp bin al.franzis.JNIHelloWorld