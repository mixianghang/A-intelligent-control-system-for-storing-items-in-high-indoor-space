package com.house.control.main;
/**
 * 这是物品管理主界面，有两个tab,一个为添加物品，一个是编辑物品信息
 */
import com.house.control.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MyGoodsActivity extends TabActivity implements Parcelable {
//	每个tab下的subActivity向主tabActivity传递的消息
	public static final int EDIT_GOODS_SUCCESS=12345;
	public static final int ADD_GOODS_SUCCESS=23456;

	
	private static final String ADD_GOODS="添加物品";
	private static final String EDIT_GOODS="编辑物品信息";
	
	
	private static final String tabNames[]=new String[]{
		ADD_GOODS,EDIT_GOODS
	};
	
	Handler handler=null;
	
	private  Handler myGoodsHandler=new Handler(){
		@Override
		  public void handleMessage(Message message) {
			Message message1=null;
			message1=Message.obtain(handler, message.what);
//			通过下面的switch向上一级传递一些数据，或者获取一下下一级的数据
			switch(message.what){
			case EDIT_GOODS_SUCCESS:{//登陆成功，此时应该修改标题栏内容
			
				break;
			}
			case ADD_GOODS_SUCCESS:{
				break;
			}
		
			}
		message1.sendToTarget();
		   
		}

	
	};
	
	public Handler getHandler(){
		return myGoodsHandler;
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
	    		 intent = new Intent(this,AddGoodsActivity.class);//新建一个Intent用作Tab显示的内容
	    		 break;
	    	 case 1:
	    		 intent = new Intent(this,EditGoodsActivity.class);//新建一个Intent用作Tab1显示的内容
	    		 break;
	    	 
	    	 
	    	 }
	    	 Bundle bundle = new Bundle();
	         bundle.putParcelable("handler",MyGoodsActivity.this);
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
	 
	 

	    public  void handleActivityResult(int requestCode, int resultCode, Intent data) {
//	            如果是addGoodsActivity发起的请求
	            if(requestCode==AddGoodsActivity.TAKE_PHOTO_REQUEST_CODE||requestCode==AddGoodsActivity.BARCODE_SCAN){
	                   AddGoodsActivity activity =(AddGoodsActivity)getLocalActivityManager().getCurrentActivity();
	                    activity.handleActivityResult(requestCode, resultCode, data);
	                    Log.e("message in houseControl", "get it");
	            }
	            else if(requestCode==EditGoodsActivity.TAKE_PHOTO_REQUEST_CODE){
	            	EditGoodsActivity activity =(EditGoodsActivity)getLocalActivityManager().getCurrentActivity();
                    activity.handleActivityResult(requestCode, resultCode, data);
                    Log.e("message in houseControl", "get it");
	            }
	            else if(requestCode==EditGoodsActivity.EDIT_GOODS_INFO){
	            	EditGoodsActivity activity =(EditGoodsActivity)getLocalActivityManager().getCurrentActivity();
                    activity.handleActivityResult(requestCode, resultCode, data);
	            	
	            }
	    }
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
