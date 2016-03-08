package com.example.gifview;

public class NativeUtil {

	static {
		System.loadLibrary("gif");
	}
	
	public static native void resizePixels(int destW, int destH, int srcW, int srcH,
			int[] srcPixels,int dest[]);
	
//	public static native void getResizePixels(int destW, int destH, int srcW, int srcH,
//			int[] srcPixels);
	
}
