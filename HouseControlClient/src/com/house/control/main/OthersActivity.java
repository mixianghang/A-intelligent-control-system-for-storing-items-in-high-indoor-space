package com.house.control.main;

import com.house.control.R;
import com.house.control.bluetooth.BlueToothActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.TabSpec;

public class OthersActivity extends TabActivity {
	private static final String BLUE_SETTING="设置蓝牙";
	private static final String USER_SETTING="用户设置";
	private static final String USER_HELP="用户帮助";
	private static final String ABOUT="关于";
	
	private static final String tabNames[]=new String[]{
		BLUE_SETTING,USER_SETTING,USER_HELP,ABOUT
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
	    	 	intent = new Intent(this,BlueToothActivity.class);//新建一个Intent用作Tab显示的内容
	    	 	break;
	    	 case 1:
	    		 intent = new Intent(this,SettingActivity.class);//新建一个Intent用作Tab显示的内容
	    		 break;
	    	 case 2:
	    		 intent = new Intent(this,UserHelpActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 case 3:
	    		 intent = new Intent(this,AboutActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 
	    	 
	    	 }
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
