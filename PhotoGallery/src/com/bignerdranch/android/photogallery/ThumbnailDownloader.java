package com.bignerdranch.android.photogallery;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ThumbnailDownloader<Token> extends HandlerThread {
	private static final String TAG = "ThumbnailDownloader";

    private static final int MESSAGE_DOWNLOAD = 0;
    private static final int MESSAGE_PRELOAD = 1;

    Handler mHandler;
    Handler mResponseHandler;
    Listener<Token> mListener;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    
    LruCache<String, Bitmap> mCache;
    
    public interface Listener<Token> {
    	void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }
    
    public void setListener(Listener<Token> listener) {
    	mListener = listener;
    }
	
	public ThumbnailDownloader(Handler handler) {
		super(TAG);
		mResponseHandler = handler;
	}

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            @SuppressWarnings("unchecked")
            public void handleMessage(Message msg) {
                if(msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
                else if (msg.what == MESSAGE_PRELOAD) {
                	String url = (String) msg.obj;
                	Log.i(TAG, "Got a preload request for url: " + url);
                	handleRequest(url);
                }
            }
        };
        
        mCache = new LruCache<String, Bitmap>(500);
    }
	
	public void queueThumbnail(Token token, String url) {
		Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
	}
	
	public void preload(String url) {
		mHandler.obtainMessage(MESSAGE_PRELOAD, url)
				.sendToTarget();
	}
	
	public void clearQueue() {
		mHandler.removeMessages(MESSAGE_DOWNLOAD);
		requestMap.clear();
	}

    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if(url == null) {
                return;
            }
            
            Bitmap bm = null;
            if(mCache.get(url) != null) {
            	bm = mCache.get(url);
            }
            else {
	            byte[] bitmapBytes = new FlickrFetchr().getURLBytes(url);
	            bm = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	            mCache.put(url, bm);
            }
            final Bitmap bitmap = bm;
            Log.i(TAG, "Bitmap created");
            
            mResponseHandler.post(new Runnable() {

				@Override
				public void run() {
					if(requestMap.get(token) != url) {
						return;
					}
					
					requestMap.remove(token);
					mListener.onThumbnailDownloaded(token, bitmap);
				}
            	
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
    
    /**
     * Handles requests for preloading urls
     */
    private void handleRequest(final String url) {
    	if(mCache.get(url) != null) {
			return;
		}
		
    	try {
			byte[] bitmapBytes = new FlickrFetchr().getURLBytes(url);
	        Bitmap bm = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	        mCache.put(url, bm);
		} catch(IOException ioe) {
			Log.e(TAG, "Error preloading image: " + url, ioe);
		}
    }
}
