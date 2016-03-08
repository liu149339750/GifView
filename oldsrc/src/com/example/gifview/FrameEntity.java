package com.example.gifview;

import android.graphics.Bitmap;

public class FrameEntity {
	private Bitmap bitmap;
	private int delay;
	
	public FrameEntity(){};
	public FrameEntity(Bitmap bm,int delay){
		bitmap = bm;
		this.delay = delay;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
}
