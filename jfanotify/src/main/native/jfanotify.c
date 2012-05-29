#include <jni.h>
#include <stdio.h>
#include <fcntl.h>
#include <errno.h>

#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/fanotify.h>

#include "al_franzis_jfanotify_FanotifyCom.h"


JNIEXPORT jint JNICALL Java_al_franzis_FanotifyCom_init(JNIEnv *env, jobject _ignore) {
  int fanotify_fd;

  fanotify_fd = fanotify_init(FAN_CLASS_PRE_CONTENT, O_RDWR);
  
  if (fanotify_fd < 0) {
    // open returned an error. Throw an IOException with the error string
    
    printf("Error while trying to init fanotify\n");
   // char buf[1024];
    //sprintf(buf, "open: %s", strerror(errno));
    //(*env)->ThrowNew(env, class_ioex, buf);
    return -1;
  }

  printf("Successful init of fanotify\n");
  return fanotify_fd;
}

JNIEXPORT void JNICALL Java_al_franzis_FanotifyCom_add(JNIEnv *env, jobject _ignore, jint fanotify_fd, jstring filename) {
  char *fname;
  u_int64_t mask = FAN_OPEN_PERM | FAN_CLOSE_WRITE | FAN_EVENT_ON_CHILD;
  
  fname = (*env)->GetStringUTFChars(env, filename, NULL);
  if (fanotify_mark(fanotify_fd, FAN_MARK_ADD, mask, AT_FDCWD, fname) != 0)
  {
    printf("Error while trying to add mark for fanotify\n");
    perror("fanotify_mark()");
    return;
  }
  (*env)->ReleaseStringUTFChars(env, filename, fname);
  
  printf("Successful adding mark for fanotify\n");
  
}


JNIEXPORT jobject JNICALL Java_al_franzis_FanotifyCom_read(JNIEnv *env, jobject obj, jint fanotify_fd)
{
  struct fanotify_event_metadata event;
  struct fanotify_response response; 
  char fname[FILENAME_MAX];
  jmethodID const_fanevent;
  jclass class_fanevent;
  jobject obj_fanevent;
  int i;
  
  
  class_fanevent = (*env)->FindClass(env, "al/franzis/FanotifyEvent");
  if (class_fanevent == NULL) return NULL;
  
  i=0;
  if (read(fanotify_fd, &event, sizeof(event)) == sizeof(event))
  {
      i++;
      if (event.fd >= 0)
      {
	get_fname(event.fd, fname, 0);
	printf(">>> event #%d: vers %hu, mask 0x%02lX, fd %d (%s), pid %d\n", 
             i, event.vers, (unsigned long) event.mask, event.fd, 
    	     fname, event.pid);
	
	const_fanevent = (*env)->GetMethodID(env, class_fanevent, "<init>", "()V");
	if (const_fanevent == NULL) return NULL;
	obj_fanevent = (*env)->NewObject(env, class_fanevent, const_fanevent);
	
	
	
	if (event.mask & FAN_OPEN_PERM)        /* snyc local file from remote, */
	{                                      /* than allow open              */
	  response.fd = event.fd;                       /* value and always    */
	  response.response = FAN_ALLOW;                /* allow access        */
	  if (write(fanotify_fd, &response, sizeof(response)) < 0) 
	    perror("write(fanotify_fd)");
	}
	if (event.mask & FAN_CLOSE_WRITE)         /* sync local file to remote */
	{
	  
	}        
	
	close(event.fd);   /* else file descriptors are eaten up */
	return obj_fanevent;
    }
  }
  else
    return NULL;
}

JNIEXPORT void JNICALL Java_al_franzis_FanotifyCom_write(JNIEnv *env, jobject obj, jint fanotify_fd, jobject response)
{
   printf("fantotify write\n");
   return;
}


int get_fname(int fd, char *fname, int basename)
/* places name for file descriptor fd in fname. If basename is false, */
/* fname contains full path name; else only file name */
/* Enough memory must be allocated for fname! */
/* 0: error, 1: ok */
{
  int len;
  char buf[FILENAME_MAX];
  char *c;
  
  sprintf(fname, "/proc/self/fd/%d", fd); /* link to local path name */
  len = readlink(fname, buf, FILENAME_MAX-1);   /* real path name is placed in buf */
  if (len == 0)
  {
    fname[0] = '\0';
    return 0;
  }
  buf[len] = '\0';
  if (basename)
  {
    c = buf + len;
    while (*c != '/') c--;   /* find last / in buf */
    c++;                     /* points to file name without directory */
  }
  else c = buf;
  sprintf(fname, "%s", c);
  return 1;
}   /* get_fname() */
