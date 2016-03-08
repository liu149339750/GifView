package com.example.gifview;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.giffinal.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Base64;
import android.widget.ImageView;

public class XImageView extends ImageView implements GifDecodeInterface {

	public Handler handler;

	public Runnable handleGif;

	private GifHandler gifHandler;
	private Executor mExecutor;

	// init gif data return first frame
	public Bitmap initGif(InputStream data,String fn) throws OutOfMemoryError{

		if (null == gifHandler) {
			gifHandler = new GifHandler();
		}
		
		if (null == handleGif) {
			handleGif = new Runnable() {
				@Override
				public void run() {
					nextFrame();
				}
			};
		}
		return gifHandler.initGifData(getContext(), data,fn);
	}

	// set next frame from gif data
	public void nextFrame() {
		if (handler != null && gifHandler != null) {
			FrameEntity fe = gifHandler.nextFrameBitmap();
			if(fe != null)
			showBitmap(fe.getBitmap());
			System.out.println(fe.getDelay()+":"+gifHandler.getDelay());
			handler.postDelayed(handleGif, gifHandler.getDelay());
		}
	}

	public void recycleGif() {
		System.out.println("recycleGif");
		if (null != handler) {
			handler.removeCallbacks(handleGif);
			handler = null;
			handleGif = null;
		}
		if (null != gifHandler) {
			gifHandler.recycleDecode();
			gifHandler = null;
		}
	}

	public XImageView(Context context) {
		super(context);
		mExecutor = Executors.newSingleThreadExecutor();
	}

    public XImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mExecutor = Executors.newSingleThreadExecutor();
	}

	public void setGifImage(final Uri uri, final Bitmap bitmap) {
		if (null == handler) {
			handler = new Handler();
		}
        if (null != uri) {
            try {
                final InputStream data = getContext().getContentResolver().openInputStream(uri);
                mExecutor.execute(new Runnable() {
					
					@Override
					public void run() {
			           if (null != initGif(data,Base64.encodeToString(uri.toString().getBytes(), 0).trim())) {
//		                    nextFrame();
		                } else {
//		                    showDefaultImage(bitmap);
		                }
					}
				});
                Thread.sleep(1000);
                nextFrame();
            } catch (OutOfMemoryError error) {
                showDefaultImage(bitmap);
            } catch (Exception exception) {
            	exception.printStackTrace();
                showDefaultImage(bitmap);
            }
        }
    }
	

    private void showDefaultImage(Bitmap bitmap) {
        if (null == bitmap) {
            bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_launcher);
        }
        showBitmap(bitmap);
        recycleGif();
    }
    
    private void showBitmap(final Bitmap bm) {
        handler.post(new Runnable() {
			
				@Override
				public void run() {
					setImageBitmap(bm);
				}
			});
    }
}