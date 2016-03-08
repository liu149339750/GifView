package com.example.giffinalf;

import java.io.File;
import java.io.IOException;

import com.example.gifview.GifImageView;
import com.example.gifview.NativeUtil;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity{
	private GifImageView mImageView;
	private Handler mHandler;
	private int i = 5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mImageView = (GifImageView) findViewById(R.id.image);
	}


	boolean b;
	public void button(View v) throws IOException {
//		mImageView.setGifPath("/sdcard/1.gif");
//		mImageView.start();
//		mImageView.stop();
		mImageView.recycleGif();
		mImageView.setScaleType(ScaleType.CENTER);
		mImageView.setImageResource(R.drawable.ic_launcher);
//		mImageView.recycleGif();
//		if(!b)
//		mImageView.pause(); 
//		else
//			mImageView.resume(); 
//		
//		b = !b;
		int a[] = new int[10];
		a[0] = 9;
//		System.out.println(NativeUtil.getInt(a));
//		int b[] = NativeUtil.getInt(a);
//		System.out.println(b[0]);
//		NativeUtil.resizePixels(100, 100, 20, 20, new int[20*20]);
//		NativeUtil.resizePixels1();
	}
	
	public void next(View v) throws IOException{
//		mImageView.stop();
		mImageView.setScaleType(ScaleType.FIT_XY);
		if(i>5)
			i=1;
		String path = "/sdcard/Android/"+i+".gif";
//		mImageView.setGif(path);
//		mImageView.setLoop(true);
//		mImageView.start();
//		if(!mImageView.isPlay())
			mImageView.setCurrentGif(Uri.fromFile(new File(path)));
//		else
//			mImageView.next();
		i++;
//		path = "/sdcard/Android/"+i+".gif";
//		mImageView.prepareNextGif(Uri.fromFile(new File(path)));
//		i++;
	}


	
	
	@Override
	public void onBackPressed() {
		mImageView.recycleGif();
		super.onBackPressed();
	}
}
