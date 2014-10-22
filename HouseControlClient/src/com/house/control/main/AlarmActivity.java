package com.house.control.main;

import com.house.control.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;

public class AlarmActivity extends TabActivity {

	
	private static final String STORE_TIME_ALARM="存储超期提醒";
	private static final String QUALITY_TIME_ALARM="保质期提醒";
	
	
	private static final String tabNames[]=new String[]{
		STORE_TIME_ALARM,QUALITY_TIME_ALARM
	};
	
	/** Called when the activity is first created. */
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     
	     setContentView(R.layout.tab);//这里使用了上面创建的xml文件（Tab页面的布局）
	     
	     Resources res = getResources(); // Resource object to get Drawables
	     final TabHost tabHost = getTabHost();  // The activity TabHost
	     TabSpec spec;
	     Intent intent=null;  // Reusable Intent for each tab
	     intent=getIntent();
	     int tabNum=0;
	     HouseControlActivityGroup houseControl;
	     if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 tabNum=bundle.getInt(HouseControlActivityGroup.TAB_NUM);
	     }
	     for(int i=0;i<tabNames.length;i++){
	    	//第一个TAB
	    	 switch(i){
	    	 case 0:
	    		 intent = new Intent(this,GoodsListActivity.class);//新建一个Intent用作Tab显示的内容
	    		 break;
	    	 case 1:
	    		 intent = new Intent(this,GoodsListActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 
	    	 
	    	 }
	    	 Bundle bundle = new Bundle();
//	    	 此处定义的listType用于listView返回存储超期与保质超期两种情况
	         bundle.putInt("listType",i);
	         intent.putExtras(bundle);
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	         spec = tabHost.newTabSpec(tabNames[i])//新建一个 Tab
	         .setIndicator(tabNames[i])//设置名称以及图标
	         .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
	         tabHost.addTab(spec);//添加进tabHost
	     
	     }
	     int height =50;   
	//   int width =45;   
	     TabWidget tabWidget = tabHost.getTabWidget();   
	     PersonalInfoActivity.setTabStyle(tabWidget,this);
	  
//	     tabHost.seton
	    
	    tabHost.setCurrentTab(tabNum);
//	     tabHost.setO
	 }
}
