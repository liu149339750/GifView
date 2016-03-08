package com.example.gifview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ImageCache extends LruCache<Integer, FrameEntity>{
	private static final String TAG = "ImageCache";
	private static final int DEFAULT_JPEG_QUALITY = 95;
	private RandomAccessFile mFile;
	private long size = 0;
	private long currentPosition = 0;
	private int lastIndex = 0;
	private int count = 0;
	private String mPath;
	private final int HEADER_SIZE = 21;
	private final int END_FLAG = 0XFF;
	private static HashMap<String, ImageCache> mMap = new HashMap<String, ImageCache>();
	
	private ImageCache(String path) throws FileNotFoundException {
		super(4);
		mPath = path;
		clearCache();
		mFile = new RandomAccessFile(path, "rw");
	}

	public static ImageCache getCache(Context context,String name) throws FileNotFoundException {
        File cacheDir = context.getCacheDir();
        String path = cacheDir.getAbsolutePath() + "/" + name.trim();
        ImageCache cache = mMap.get(path);
        if(cache == null){
        	cache = new ImageCache(path);
//        	mMap.put(path, cache);
        }
        return cache;
	}
	
	public boolean isCacheExsit(){
		try {
			if(mFile.length()>0) {
				mFile.seek(mFile.length() - 1);
				return mFile.read() == END_FLAG;
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private byte[] intArrayToByteArray(int source[]){
		int len = source.length;
		byte buf[] = new byte[len*4];
		for(int i=0;i<len;i++){
			buf[4*i] = (byte) (source[i] >> 24);
			buf[4*i+1] = (byte) (source[i] >> 16);
			buf[4*i+2] = (byte) (source[i] >> 8);
			buf[4*i+3] = (byte) (source[i]);
		}
		return buf;
	}
	
	private int[] byteArrayToIntArray(byte source[]){
		int len = source.length/4;
		int buf[] = new int[len];
		for(int i=0;i<len;i++){
			buf[i] = ((source[4*i] & 0xff) << 24) | ((source[4*i + 1] & 0xff) << 16) | ((source[4*i + 2] & 0xff) << 8) |((source[4*i + 3] & 0xff));
		}
		return buf;
	}
	
	public void addFrameEntity(int magic,FrameEntity fe) throws IOException {
		synchronized (mFile) {
		mFile.seek(size);
//		byte data[] = compressToByte(fe.getBitmap());
//		int len = data.length;
		int color[] = fe.getColors();
		int len = color.length * 4;
		mFile.write(0x01);  //flag
		mFile.writeInt(magic);	//key
		mFile.writeInt(fe.getDelay());  //delay
		mFile.writeInt(len);	//size 
		mFile.writeInt(fe.getWidth()); //width
		mFile.writeInt(fe.getHeight()); //height
//		mFile.write(data);		//block
		mFile.write(intArrayToByteArray(color));
		size += len + HEADER_SIZE;
		put(count, fe);
		count ++;
//		System.out.println("addFrameEntity count = "+count);
		}
	}
	
	@Override
	protected void entryRemoved(boolean evicted, Integer key,
			FrameEntity oldValue, FrameEntity newValue) {
//		oldValue.getBitmap().recycle();
	}
	
	public FrameEntity getFrameEntity(int magic) throws IOException {
		synchronized (mFile) {
		long p = 0;
		long size = this.size; 
		FrameEntity fe = get(magic);
		if(fe != null)
			return fe;
		if(lastIndex + 1 == magic && magic != 0)
			p = currentPosition;
//		System.out.println("p="+p+",magic="+magic+",count="+count);
		while(size > p){
			mFile.seek(p);
			int flag = mFile.read();
			if(flag == -1 || flag == END_FLAG)
				return null;
			if(flag != 0x01)
				Log.w(TAG, "cache file has error");
			int key = mFile.readInt();
			int delay = mFile.readInt();
			if(key == magic) {
//				int len = (int) mFile.readLong();
//				byte buffer[] = new byte[(int) len];
//				mFile.read(buffer);
//				Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, len);
				int len = mFile.readInt();
				int width = mFile.readInt();
				int height = mFile.readInt();
				byte buffer[] = new byte[len];
				mFile.read(buffer);
				int colors[] = byteArrayToIntArray(buffer);
				buffer = null;
				fe = new FrameEntity(colors,width,height, delay);
				lastIndex = magic;
				currentPosition = mFile.getFilePointer();
				return fe;
			} else {
				p += mFile.readInt() + HEADER_SIZE;
			}
		}
		lastIndex = 0;
		currentPosition = 0;
		}
		return new FrameEntity();
	}
	
	public void clearCache(){
		new File(mPath).delete();
		size = 0;
		lastIndex = 0;
		currentPosition = 0;
	}
	
	public int count(){
		return count;
	}
	
	public void close(){
		try {
			mFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			size = 0;
		}
	}

	public void writeEnd() {
		try {
			mFile.write(0xff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private byte[] compressToByte(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(65536);
        bm.compress(CompressFormat.JPEG, DEFAULT_JPEG_QUALITY, baos);
        return baos.toByteArray();
	}
	
}
