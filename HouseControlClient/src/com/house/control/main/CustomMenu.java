package com.house.control.main;


import com.house.control.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;



/**
 * @author Administrator
 *         xsl  xushilin@kingtoneinfo.com
 * @version： 创建时间：2011-8-30 上午11:16:19
 * 说         明:
 * 修改历史：
 */
public class CustomMenu {
	private LinearLayout myLayout;
	private  Context mContext;
	private Activity mActivity;
	private Animation menuShowAnimation = null;
	private Animation menuHideAnimation = null;
	private Resources res;
	public int screen_height;
	private ImageButton imgIndex,imgSet,imgNews,imgAdd,imgQuit,imgLib;	
	public CustomMenu(Context context,Activity activity){
		
		mContext=context;	
		mActivity=activity;
		res = mContext.getResources();
		screen_height = res.getDisplayMetrics().heightPixels;
		myLayout=(LinearLayout)activity.findViewById(R.id.lines);
		myLayout.setBackgroundColor(R.drawable.background);
		
		imgIndex=(ImageButton)activity.findViewById(R.id.menu_btn_index);	
		imgSet=(ImageButton)activity.findViewById(R.id.menu_btn_set);
		imgNews=(ImageButton)activity.findViewById(R.id.menu_btn_news);
		imgAdd=(ImageButton)activity.findViewById(R.id.menu_btn_add);
		imgQuit=(ImageButton)activity.findViewById(R.id.menu_btn_quit);
		imgLib=(ImageButton)activity.findViewById(R.id.menu_btn_lib);

		//返回首页
		imgIndex.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
		//设置
		imgSet.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
		//查询
		imgNews.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
		//编辑
		imgAdd.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
		//退出系统
		imgQuit.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
		//素材库
		imgLib.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				//TODO do somthing
			}			
		});
	}
	public  void  CreateMenu(){		
		if(myLayout.getVisibility()==View.GONE)
			showAppMenu();
			//myLayout.setVisibility(View.VISIBLE);		
		else
			hideAppMenu();
			//myLayout.setVisibility(View.GONE);
	}
	  /**
     * 显示菜单栏, 重新实现的Option menu.
     * */
    private void showAppMenu() {
    	if (menuShowAnimation == null) {
    		menuShowAnimation = AnimationUtils
    				.loadAnimation(mContext, R.anim.show_menu);
    	}
    	myLayout.startAnimation(menuShowAnimation);
    	myLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏菜单栏, 重新实现的Option menu.
     * */
    private void hideAppMenu() {
    	myLayout.setVisibility(View.GONE);
    	if (menuHideAnimation == null)
    		menuHideAnimation =AnimationUtils
    				.loadAnimation(mContext, R.anim.hidden_menu);
    	
    	myLayout.startAnimation(menuHideAnimation);
    }
    
    
    public boolean dispatchTouchEvent(MotionEvent event,boolean b) {
		if (myLayout.getVisibility() == View.VISIBLE) {
			int y = (int) event.getRawY();
			if (y < screen_height - myLayout.getHeight()) {
				hideAppMenu();
				return true;
			}
		}    	
		return b;
    }
    
    public boolean dispatchKeyEvent(KeyEvent event,boolean b) {
    	int act = event.getAction();
    	int code = event.getKeyCode();        	
    	// app menu like option menu
    	if (code == KeyEvent.KEYCODE_MENU){
	    	if (act == KeyEvent.ACTION_DOWN){
	    		if (myLayout.getVisibility() == View.VISIBLE) {
	    			hideAppMenu();
	    		} else {
	    			showAppMenu();
	    		}
	    		return true;
	    	}
    	}else if (code == KeyEvent.KEYCODE_BACK){
    		if (myLayout.getVisibility() == View.VISIBLE) {
    			hideAppMenu();
    			return true;
    		}
    	}
    	return b;	
    }
}