package com.example.project_s;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainGamePanel extends SurfaceView implements
		SurfaceHolder.Callback {

	private MainThread thread;
	private PirateIcon pirateIcon;
	private static final String TAG = MainGamePanel.class.getSimpleName();

	public MainGamePanel(Context context) {
		super(context);
		
		// create pirateIcon and load bitmap
		pirateIcon = new PirateIcon(BitmapFactory.decodeResource(getResources(), R.drawable.catchertiny), 50, 50);

		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// make the GamePanel focusable so it can handle events
		thread = new MainThread(getHolder(), this);

		setFocusable(true);
	}

	protected void onDraw(Canvas canvas) {
		// fills the canvas with black
		canvas.drawColor(Color.BLACK);
		pirateIcon.draw(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		boolean retry = true;
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				// keep trying until shutdown of the thread
			}
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// delegating event handling to the droid
			pirateIcon.handleActionDown((int)event.getX(), (int)event.getY());
			
			// check if in the lower part of the screen we exit
			if (event.getY() > getHeight() - 50) {
				thread.setRunning(false);
				((Activity) getContext()).finish();
			} else {
				Log.d(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
			}
		} if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// add gestures
			if (pirateIcon.isTouched()) {
				// the droid was picked up and is being dragged
				pirateIcon.setX((int)event.getX());
				pirateIcon.setY((int)event.getY());
			}
		} if (event.getAction() == MotionEvent.ACTION_UP){
			// touch was released
			if (pirateIcon.isTouched()){
				pirateIcon.setTouched(false);
			}
		}
		return true;
	}
}
