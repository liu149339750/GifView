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
	
	/**把预加载的gif作为当前的gif*/
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
	
	/**显示一帧图片，开始播放*/
	private void nextFrame(GifHandler handler) {
		if (mHandler != null && handler != null) {
			FrameEntity fe = handler.nextFrameBitmap();
			if(fe != null)
				showBitmap(fe.getBitmap());
			mHandler.postDelayed(mGifRun, handler.getDelay());
		}
	}
	
	/**清理资源，播放gif*/
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
	
	/**提前准备下个要播放的gif*/
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
	
    /**释放所有资源，包括预加载的*/
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
    
    /**暂停不回收*/
    public void pause(){
    	if(mHandler != null)
        	mHandler.removeCallbacks(mGifRun);
        mHandler = null;
    }
    
   /**恢复播放，对应pause*/
    public void resume(){
    	mHandler = new Handler();
    	nextFrame(mGifHandler);
    }
    
    public boolean isPlay(){
    	return isPlay;
    }

    /**每解析得到一帧回调一次,第一次时设为解析到13帧或者解析完成后开始播放*/
	@Override
	public void onPrepare(int count) {
		if(count >= 13 && !isPlay){
			nextFrame(mGifHandler);
			isPlay = true;
		}
//		System.out.println("onPrepare cout="+count);
	}

	/**解码结束后回调的接口*/
	@Override
	public void onDecodeEnd(int status) {
		if(!isPlay){
			nextFrame(mGifHandler);
			isPlay = true;
		}
		
	}

}
