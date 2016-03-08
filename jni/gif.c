/*
 ============================================================================
 Name        : aa.c
 Author      : liuwei
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */
#include <jni.h>
#include <stdio.h>


  void Java_com_example_gifview_NativeUtil_resizePixels(JNIEnv* env, jclass clazz,int destW, int destH, int srcW, int srcH,
		  jintArray src,jintArray dest){
	  jint *srcPixels = (*env)->GetIntArrayElements(env,src,NULL);
	  jint *destPixels = (*env)->GetIntArrayElements(env,dest,NULL);
		 int len = destH*destW;
		 int destX,destY;
		 for(destY=0;destY<destH;destY++){
			 for(destX=0;destX<destW;destX++){
				 int offsetX = destX*srcW/destW;
				 int offsetY = destY*srcH/destH;
				 destPixels[destX + destY*destW] = srcPixels[offsetX + offsetY*srcW];
			 }
		 }
  }

//  jintArray Java_com_example_gifview_NativeUtil_getResizePixels(JNIEnv* env, jclass clazz,int destW, int destH, int srcW, int srcH,
//		  jintArray src){
//	  jint *srcPixels = (*env)->GetIntArrayElements(env,src,NULL);
//		 int len = destH*destW;
//		 jintArray dest = (*env)->NewObjectArray(env, len, (*env)->FindClass(env, "java/lang/Integer"), 0);
//		 int destX,destY;
//		 for(destY=0;destY<destH;destY++){
//			 for(destX=0;destX<destW;destX++){
//				 int offsetX = destX*srcW/destW;
//				 int offsetY = destY*srcH/destH;
//				 dest[destX + destY*destW] = srcPixels[offsetX + offsetY*srcW];
//			 }
//		 }
//		 return dest;
//  }


