package com.example.gifview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.support.v4.util.LruCache;

public class ImageCache extends LruCache<Integer, FrameEntity>{
	private static final String TAG = "ImageCache";
	private static final int DEFAULT_JPEG_QUALITY = 95;
	private RandomAccessFile mFile;
	private long size = 0;
	private long currentPosition = 0;
	private int lastIndex = 0;
	private int count = 0;
	private String mPath;
	private final int HEADER_SIZE = 17;
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
	
	public void addFrameEntity(int magic,FrameEntity fe) throws IOException {
		synchronized (mFile) {
		mFile.seek(size);
		byte data[] = compressToByte(fe.getBitmap());
		int len = data.length;
		mFile.write(0x01);  //flag
		mFile.writeInt(magic);	//key
		mFile.writeInt(fe.getDelay());  //delay
		mFile.writeLong(len);	//size
		mFile.write(data);		//block
		size += len + HEADER_SIZE;
		put(count, fe);
		count ++;
		System.out.println("addFrameEntity count = "+count);
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
		System.out.println("p="+p+",magic="+magic+",count="+count);
		while(size > p){
			mFile.seek(p);
			int flag = mFile.read();
			if(flag == -1 || flag == END_FLAG)
				return null;
			int key = mFile.readInt();
			int delay = mFile.readInt();
			if(key == magic) {
				int len = (int) mFile.readLong();
				byte buffer[] = new byte[(int) len];
				mFile.read(buffer);
				Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, len);
				fe = new FrameEntity(bm, delay);
				lastIndex = magic;
				currentPosition = mFile.getFilePointer();
				return fe;
			} else {
				p += mFile.readLong() + HEADER_SIZE;
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
