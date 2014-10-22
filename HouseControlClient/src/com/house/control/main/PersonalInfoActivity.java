package com.house.control.main;

import java.util.ArrayList;
import java.util.List;

import com.house.control.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
/**
 * 个人信息子系统，实现用户登陆、注册、个人信息编辑、密码找回、修改密码等操作
 * @author mscott
 *
 */
public class PersonalInfoActivity extends TabActivity implements Parcelable {
	public static final String GOODS_TYPE="goodsType";
	
//	每个tab下的subActivity向主tabActivity传递的消息
	public static final int LOGIN_SUCCESS=1234;
	public static final int REGISTER_SUCCESS=2345;
	public static final int EDIT_PERSONAL_INFO_SUCCESS=3456;
	public static final int EDIT_PASSWORD_SUCCESS=4567;
	public static final int FIND_PASSWORD_BACK=5678;
//	用于向galleryActivity传递参数
	
	private Handler handler=null;
	private String logIn="登陆";
	private String register="注册";
	private String editPersonalInfo="资料编辑";
	private String forgetPassword="忘记密码";
	private String changePassword="修改密码";
	
	
	public static final int LOGIN=0;
	public static final int REGISTER=1;
	public static final int DATA_EDIT=2;
	public static final int FORGET_PASSWORD=3;
	public static final int EDIT_PASSWORD=4;
	private String[] tabNames=new String[]{
			logIn,register,editPersonalInfo,forgetPassword,changePassword
	};
	
	
	private  Handler personalInfoHandler=new Handler(){
		@Override
		  public void handleMessage(Message message) {
			Message message1=null;
			message1=Message.obtain(handler, message.what);
//			通过下面的switch向上一级传递一些数据，或者获取一下下一级的数据
			switch(message.what){
			case LOGIN_SUCCESS:{//登陆成功，此时应该修改标题栏内容
			
				break;
			}
			case REGISTER_SUCCESS:{
				break;
			}
			case EDIT_PERSONAL_INFO_SUCCESS:{
				break;
			}
			case EDIT_PASSWORD_SUCCESS:{
				break;
			}
			case FIND_PASSWORD_BACK:{
				break;
			}
			}
		message1.sendToTarget();
		   
		}

	
	};
	
	public Handler getHandler(){
		return personalInfoHandler;
	}
	
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
	    	 houseControl=(HouseControlActivityGroup)bundle.get("handler");
	    	 handler=houseControl.getHandler();
	    	 tabNum=bundle.getInt(HouseControlActivityGroup.TAB_NUM);
	     }
	     for(int i=0;i<tabNames.length;i++){
	    	//第一个TAB
	    	 switch(i){
	    	 case 0:
	    		 intent = new Intent(this,LogInActivity.class);//新建一个Intent用作Tab显示的内容
	    		 break;
	    	 case 1:
	    		 intent = new Intent(this,RegisterUserActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 case 2:
	    		 intent = new Intent(this,EditUserInfoActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 case 3:
	    		 intent = new Intent(this,FindPasswordBackActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 case 4:
	    		 intent = new Intent(this,EditPasswordActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 
	    	 }
	    	 Bundle bundle = new Bundle();
	         bundle.putParcelable("handler",PersonalInfoActivity.this);
	         intent.putExtras(bundle);
	         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	         spec = tabHost.newTabSpec(tabNames[i])//新建一个 Tab
	     
	         .setIndicator(tabNames[i])//设置名称以及图标
	         .setContent(intent);//设置显示的intent，这里的参数也可以是R.id.xxx
	         
	         tabHost.addTab(spec);//添加进tabHost
	     
	     }
 
	     TabWidget tabWidget = tabHost.getTabWidget();   
	    setTabStyle(tabWidget,this);
	 
//	     tabHost.seton
	    
	    tabHost.setCurrentTab(tabNum);
//	     tabHost.setO
	 }
	 
	 //设置tabchild的字体颜色等
public static  void setTabStyle(TabWidget tabWidget,Context context){
	 int height =50;  
	  for (int i =0; i < tabWidget.getChildCount(); i++) {  
          
	         /**设置高度、宽度，由于宽度设置为fill_parent，在此对它没效果 */   
	         tabWidget.getChildAt(i).getLayoutParams().height = height;   
//	         tabWidget.getChildAt(i).getLayoutParams().width = width;   
	         /**设置tab中标题文字的颜色，不然默认为黑色 */   
	          final TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);   
	          tv.setTextColor(context.getResources().getColorStateList(R.color.white)); 
	          tv.setTextSize(20);
	     } 
 }
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	 
}
