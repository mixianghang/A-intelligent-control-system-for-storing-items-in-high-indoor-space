package com.house.control.main;

import com.house.control.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
 
/**
 * 主页，以gallery的形式分类显示物品
 * @author mscott
 *
 */
public class HomeActivity extends TabActivity {
	public static final String GOODS_TYPE="goodsType";
	
//	用于向galleryActivity传递参数
	public static  int GOODS_TYPE_ID=0;
	
	public static final int ALL_LIST=0;
	public static final int FOODS_LIST=1;
	public static final int CLOTHES_LIST=2;
	public static final int BOOKS_LIST=3;
	public static final int OTHER_LIST=4;
	
	private String allGoods="全部";
	private String foods="食品";
	private String clothes="衣服";
	private String books="书籍";
	private String others="其他";
	
	private int[] Lists=new int[]{
			ALL_LIST,
			FOODS_LIST,
			CLOTHES_LIST,
			BOOKS_LIST,
			OTHER_LIST
	};
	private String[] tabNames=new String[]{
			allGoods,foods,clothes,books,others	
	};
 
 /** Called when the activity is first created. */
 @Override
 public void onCreate(Bundle savedInstanceState) {
	 setTheme(android.R.style.Theme_Translucent_NoTitleBar);
     super.onCreate(savedInstanceState);
     Window win = getWindow();
     win.requestFeature(Window.FEATURE_NO_TITLE);//注意顺序
     setContentView(R.layout.tab);//这里使用了上面创建的xml文件（Tab页面的布局）
     Resources res = getResources(); // Resource object to get Drawables
     final TabHost tabHost = getTabHost();  // The activity TabHost
     TabSpec spec;
     Intent intent;  // Reusable Intent for each tab
     for(int i=0;i<tabNames.length;i++){
    	//第一个TAB
    	 GOODS_TYPE_ID=Lists[i];
         intent = new Intent(this,GalleryActivity.class);//新建一个Intent用作Tab1显示的内容
         intent.putExtra(GOODS_TYPE, Lists[i]);
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
     tabHost.setOnTabChangedListener(new OnTabChangeListener(){

		@Override
		public void onTabChanged(String arg0) {
			// TODO Auto-generated method stub
			GOODS_TYPE_ID=tabHost.getCurrentTab();
		}
    	 
     });
//     tabHost.seton
    tabHost.setCurrentTab(0);
//     tabHost.setO
 }
 
}