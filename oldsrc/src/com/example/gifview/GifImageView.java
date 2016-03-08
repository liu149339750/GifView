package com.example.gifview;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.giffinal.R;
import com.example.gifview.GifDecodeInterface.GifHandler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;

public class GifImageView extends ImageView implements GifDecodeInterface,PrepareListener{
	
	private Executor mExecutor;
	private Handler mHandler = new Handler();
	private GifHandler mGifHandler;
	private GifHandler mNextHandler;
	private Runnable mGifRun;
	private boolean isPlay;
	
	public GifImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mExecutor = Executors.newCachedThreadPool();
	}
	
	/**��Ԥ���ص�gif��Ϊ��ǰ��gif*/
	public void next(){
		if(mNextHandler == null)
			return;
		GifHandler temp = mGifHandler;
		mGifHandler = mNextHandler;
		mNextHandler = temp;
//		mGifHandler.setListener(this);
		if(!isPlay)
			nextFrame(mGifHandler);
	}
	
	/**��ʾһ֡ͼƬ����ʼ����*/
	private void nextFrame(GifHandler handler) {
		if (mHandler != null && handler != null) {
			FrameEntity fe = handler.nextFrameBitmap();
			if(fe != null)
				showBitmap(fe.getBitmap());
			mHandler.postDelayed(mGifRun, handler.getDelay());
		}
	}
	
	/**������Դ������gif*/
	public void setCurrentGif(final Uri uri) throws FileNotFoundException{
        final InputStream data = getContext().getContentResolver().openInputStream(uri);
        mExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
	           
	   		if (null != mGifHandler) {
	   			recycleGif();
			}
   			mGifHandler = new GifHandler();
   			mGifHandler.setListener(GifImageView.this);
			if (null == mGifRun) {
				mGifRun = new Runnable() {
					@Override
					public void run() {
						nextFrame(mGifHandler);
					}
				};
			}
			mGifHandler.initGifData(getContext(), data,Base64.encodeToString(uri.toString().getBytes(), 0).trim());
			}
		});
	}
	
	/**��ǰ׼���¸�Ҫ���ŵ�gif*/
	public void prepareNextGif(final Uri uri) throws FileNotFoundException{
        final InputStream data = getContext().getContentResolver().openInputStream(uri);
        mExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
	           if(mNextHandler != null)
	        	   mNextHandler.destroy();
	   			mNextHandler = new GifHandler();
				mNextHandler.initGifData(getContext(), data,Base64.encodeToString(uri.toString().getBytes(), 0).trim());
			}
		});
	}
	
    /**�ͷ�������Դ������Ԥ���ص�*/
	public void recycleGif() {
		System.out.println("recycleGif");
		if (null != mHandler) {
			mHandler.removeCallbacks(mGifRun);
			mGifRun = null;
		}
		if (null != mGifHandler) {
			mGifHandler.destroy();
			mGifHandler = null;
		}
		if(mNextHandler != null)
			mNextHandler.destroy();
		isPlay = false;
	}
	
    private void showBitmap(final Bitmap bm) {
        mHandler.post(new Runnable() {
			
				@Override
				public void run() {
					setImageBitmap(bm);
				}
			});
    }
    
    /**��ͣ������*/
    public void pause(){
    	if(mHandler != null)
        	mHandler.removeCallbacks(mGifRun);
        mHandler = null;
    }
    
   /**�ָ����ţ���Ӧpause*/
    public void resume(){
    	mHandler = new Handler();
    	nextFrame(mGifHandler);
    }
    
    public boolean isPlay(){
    	return isPlay;
    }

    /**ÿ�����õ�һ֡�ص�һ��,��һ��ʱ��Ϊ������13֡���߽�����ɺ�ʼ����*/
	@Override
	public void onPrepare(int count) {
		if(count >= 13 && !isPlay){
			nextFrame(mGifHandler);
			isPlay = true;
		}
//		System.out.println("onPrepare cout="+count);
	}

	/**���������ص��Ľӿ�*/
	@Override
	public void onDecodeEnd(int status) {
		if(!isPlay){
			nextFrame(mGifHandler);
			isPlay = true;
		}
		
	}

}
