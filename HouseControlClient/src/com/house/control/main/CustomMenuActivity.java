package com.house.control.main;

import com.house.control.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
public class CustomMenuActivity extends Activity {
	private CustomMenu mCustomMenu=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_menu_activity);		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(mCustomMenu==null)
			mCustomMenu=new CustomMenu(CustomMenuActivity.this,CustomMenuActivity.this);
		mCustomMenu.CreateMenu();
		return false;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(mCustomMenu!=null)
			return mCustomMenu.dispatchKeyEvent(event,super.dispatchKeyEvent(event));
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(mCustomMenu!=null)
			return mCustomMenu.dispatchTouchEvent(event,super.dispatchTouchEvent(event));
		return super.dispatchTouchEvent(event);
	}
	
	
}
