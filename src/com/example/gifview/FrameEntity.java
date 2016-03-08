package com.example.gifview;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class FrameEntity {
	private Bitmap bitmap;
	private int delay;
	private int width;
	private int height;
	private int colors[];
	
	public FrameEntity(){};
	public FrameEntity(Bitmap bm,int delay){
		bitmap = bm;
		this.delay = delay;
	}
	
	public FrameEntity(int colors[],int width,int height,int delay){
		this.colors = colors;
		this.width = width;
		this.height = height;
		this.delay = delay;
	}
	
	public int[] getColors() {
		return colors;
	}
	
	public int[] getBuffer(){
		return new int[width*height];
	}
	
	public void setColors(int[] colors) {
		this.colors = colors;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public Bitmap getBitmap() {
		if(bitmap == null && colors != null)
			return Bitmap.createBitmap(colors, width, height, Config.RGB_565);
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
