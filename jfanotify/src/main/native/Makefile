JDK = /usr/java/jdk1.6.0_26

CLASSES = FanotifyCom.class
OBJS = jfanotify.o
NATIVE_LIB = libjfanotify.so

MVN_BASE_DIR = ../../..
TARGET_DIR = $(MVN_BASE_DIR)/target

CFLAGS += -fPIC -I$(JDK)/include -I$(JDK)/include/linux
GCC = gcc -shared $(CFLAGS) jfanotify.c -o $(TARGET_DIR)/$(NATIVE_LIB)

jni : al_franzis_jfanotify_FanotifyCom.h
	$(GCC)
	

jfanotify.c : al_franzis_jfanotify_FanotifyCom.h


#
# Run javah.
#
al_franzis_jfanotify_FanotifyCom.h:
	$(JDK)/bin/javah -classpath $(TARGET_DIR)/classes/ al.franzis.jfanotify.FanotifyCom


clean:
	rm -f *.o *.h
	rm -f *.so


#
# Check that the user has a valid JDK install.
#
checkjdk:
	@if [ ! -x $(JDK)/bin/java ]; then				\
	    echo "ERROR: JDK not found!";				\
	    echo "";							\
	    echo "Please install JDK version 1.1 or higher, and";	\
	    echo "invoke make like this:";				\
	    echo "        % $(MAKE) JDK=/path/to/jdk";			\
	    echo "";							\
	    exit 1;							\
	fi


