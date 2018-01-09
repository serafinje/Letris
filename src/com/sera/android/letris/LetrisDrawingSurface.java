package com.sera.android.letris;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
 
public class LetrisDrawingSurface extends SurfaceView 
implements SurfaceHolder.Callback
{
	private static final String TAG = "LetrisThread";
	
    /** Handle to the application context, used to e.g. fetch Drawables. */
    Context mContext;
    private LetrisThread drawThread;
    public LetrisThread getThread() { return drawThread; }

	public LetrisDrawingSurface(Context context, AttributeSet attrs) {
		super(context,attrs);
		
		Log.v(TAG,"Constructor de la Surface");

		this.mContext = context;
		
        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.setFixedSize(320,480);
        holder.addCallback(this);
		//drawThread = new LetrisThread(holder,this.getContext());
		drawThread = new LetrisThread(holder,context);
        setFocusable(true); // make sure we get key events
	}

	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		drawThread.mSurfaceHeight=this.getBottom();
		drawThread.mSurfaceWidth=this.getRight();
		drawThread.setSurfaceSize(width, height);
	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
        drawThread.setRunning(true);
        drawThread.start();
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        drawThread.setRunning(false);
        while (retry) {
            try {
                drawThread.join();
                retry = false;
            } catch (InterruptedException e) {
            } 
        }
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		drawThread.touch(event);
		
		return true;
	}
}
