package com.house.control.main;

import java.util.Date;

import com.house.control.R;
import com.house.control.bluetooth.BlueToothActivity;
import com.house.control.bluetooth.BluetoothChatService;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
/**
 * 
 * @author GV
 *
 */
public class HouseControlActivityGroup extends ActivityGroup implements Parcelable {

	public static GridView gvTopBar;
	private static int currentId=0;
	
	private ImageAdapter topImgAdapter;
	public static LinearLayout container;// 装载sub Activity的容器
	
	public static final String PUBLIC_USER="公共用户";
	
//	由于国内不支持google购物
	public final static String[] websiteNames=new String[]{
		"百度","谷歌","淘宝网","卓越亚马逊","当当"
	};
	
	public final static String[] searchURL=new String[]{
		"http://www.baidu.com/s?wd=",
		"https://www.google.com/search?q=",
		"http://s.taobao.com/search?q=",
		"http://www.amazon.cn/s//ref=nb_sb_noss?field-keywords=",
		"http://searchb.dangdang.com/?key="
		
	};
	
	public static int HOME=0;
	
	public static int  PERSONAL_INFO=1;
	public static final int  MYGOODS=2;
	public static int  SEARCH_GOODS=3;
	public static int  ALARM=4;
	public static int  SETTINGS=5;
	
//	用于与单片机进行通信的端口号与波特率
	private static int SERIAL_PORT=0;
	private static int BAUD_RATE=0;
	
//	以下为与单片机进行通信的通信协议
	private static final byte GET_Container_COMMAND='A';//取箱子的命令
	private static final byte RECIEVE_REPLY='E';//对方收到后的回复
	private static final byte Container_GETED_REPLY='C';
	private static final byte SAVE_Container_COMMAND='B';//用户已经放入物品，可以保存了
	private static final byte SAVE_SUCSESS_REPLY='D';//保存箱子成功的回复
	private static final byte FAILED_REPLY='F';//保存箱子失败或者取箱子失败
	
//	存储当前所进行的存取物品的相关参数
	private static int commandType=0;//0为存物品，1为取物品
	private static int storeLocation;//物品保存的箱子编号
	private static String goodsId=null;
	private static byte preCommand;
	
//	圆形进度条对话框
	ProgressDialog mpDialog;
	
//	各button的点 击结果
	public static final String QUIT_SUCCESS="退出成功！";
	public static final String OFFLINE="您并未登陆！";
	
//	从message的bundle里获取子activity应该显示的tab
	public static String TAB_NUM="tabNum";
	
	private  Handler handler=new Handler(){
		@Override
		  public void handleMessage(Message message) {
			switch(message.what){
			case BlueToothActivity.MESSAGE_STATE_CHANGE:
				if(message.arg2==BluetoothChatService.STATE_CONNECTED&&message.arg1!=BluetoothChatService.STATE_CONNECTED)
					Toast.makeText(getApplicationContext(), "蓝牙连接已经断开,请重新连接！", Toast.LENGTH_LONG).show();
				break;
			case BlueToothActivity.MESSAGE_DEVICE_NAME:
				Bundle bundle=message.getData();
				Toast.makeText(HouseControlActivityGroup.this, "已经连接到设备："+bundle.getString(BlueToothActivity.DEVICE_NAME), Toast.LENGTH_LONG).show();
				break;
			case PersonalInfoActivity.LOGIN_SUCCESS:
				refreshTitleBar(HouseControlActivityGroup.this);
				SwitchActivity(HouseControlActivityGroup.HOME,0);
				break;
			case PersonalInfoActivity.REGISTER_SUCCESS:
				break;
			case PersonalInfoActivity.EDIT_PASSWORD_SUCCESS:
				break;
			case PersonalInfoActivity.EDIT_PERSONAL_INFO_SUCCESS:
				break;
			case PersonalInfoActivity.FIND_PASSWORD_BACK:
				SwitchActivity(HouseControlActivityGroup.HOME,0);
				break;
			case HouseControlActivityGroup.MYGOODS:
				Bundle bundle1=message.getData();
				int tabNumber=bundle1.getInt(TAB_NUM);
			    SwitchActivity(message.what,tabNumber);
			   
				break;
			 case BlueToothActivity.MESSAGE_READ:
				String result=new String((byte[]) message.obj);
				result=result.substring(0, message.arg1);
				Log.e("client recevie", result+result.length());
//				mpDialog.setMessage(result+result.length());
				if(result.equals(new String(new byte[]{HouseControlActivityGroup.RECIEVE_REPLY}))){
					Log.e("client1","E");
					switch(preCommand){
					case HouseControlActivityGroup.GET_Container_COMMAND:
						mpDialog.setMessage("命令已经送达，开始取下箱子！");
//						new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
						break;
					
					case HouseControlActivityGroup.SAVE_Container_COMMAND:
					if(commandType==0){
						mpDialog.setMessage("命令已经送达，开始保存箱子！");
//						new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
					}
					
						break;
					}
//				
				}
				else if(result.equals(new String(new byte[]{HouseControlActivityGroup.SAVE_SUCSESS_REPLY}))){
					
					if(commandType==0)
						mpDialog.setMessage("物品保存成功！");
					else
						mpDialog.setMessage("箱子已经放回");
					mpDialog.dismiss();
					if(commandType==0){
						AddGoodsActivity activity =(AddGoodsActivity)((MyGoodsActivity)(HouseControlActivityGroup.this.getCurrentActivity())).getCurrentActivity();
						activity.saveGoodsInfo();
					}
				}
				else if(result.equals(new String(new byte[]{HouseControlActivityGroup.Container_GETED_REPLY}))){
		
					if(commandType==0){
						mpDialog.setMessage("请您将要保存的物品放入箱子！");
					new AlertDialog.Builder(HouseControlActivityGroup.this) 
					
				        .setTitle("确认物品") 
				        .setMessage("是否已经将物品放在箱子里") 
				        .setPositiveButton("是", 
				                new DialogInterface.OnClickListener() { 
				                    public void onClick(DialogInterface dialog, 
				                            int whichButton) {
				                        /* User clicked OK so do some stuff */ 
//				                  		if(SerialPortListener.openSerialPort(SERIAL_PORT, BAUD_RATE)){
//				                    		Log.e("open SerialPort 0", "success!");
//				                    		byte command=(byte) HouseControlActivityGroup.SAVE_Container_COMMAND;
//				                    		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
//				                    		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
//				                    		mpDialog.setMessage("正在将箱子放入柜子中......");
//				                    		
//				                    	}
//				                    	else{
//				                    		Log.e("open SerialPort 0", "failed!");
//				                    	}
//				                  		已经连接服务器
				                    	if(BluetoothChatService.service!=null&&BluetoothChatService.service.getState()==3){
				                    		byte command=(byte) HouseControlActivityGroup.SAVE_Container_COMMAND;
				                    		preCommand=command;
				                    		BluetoothChatService.service.write(new byte[]{command,(byte) storeLocation});
				                    		mpDialog.setMessage("正在将箱子放入柜子中......");
				                    	}
				                    	else{
				                    		Toast.makeText(HouseControlActivityGroup.this, "并未连接蓝牙服务器", Toast.LENGTH_LONG).show();
				                    	}
				                    } 
				                }) 
				        .create()
				        .show();
					}
					else{
						mpDialog.setMessage("请您从箱子里取出您要取出的物品！");
						new AlertDialog.Builder(HouseControlActivityGroup.this) 
				        .setTitle("确认物品取出") 
				        .setMessage("是否已经将物品从箱子里取出？") 
				        
				        .setPositiveButton("是", 
				                new DialogInterface.OnClickListener() { 
				                    public void onClick(DialogInterface dialog, 
				                            int whichButton) {
				                        /* User clicked OK so do some stuff */ 
//				                  		if(SerialPortListener.openSerialPort(SERIAL_PORT, BAUD_RATE)){
//				                    		Log.e("open SerialPort 0", "success!");
//				                    		byte command=(byte) HouseControlActivityGroup.SAVE_Container_COMMAND;
//				                    		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
////				                    		此时不必进行监听了
////				                    		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
//				                    		HouseControlActivityGroup.deleteGoodsInfo(goodsId,HouseControlActivityGroup.this);
//				                    	
//				                    		SwitchActivity(HouseControlActivityGroup.currentId,0);
//				                
//				                    		mpDialog.dismiss();
//				                    		
//				                    		
//				                    		
//				                    
//				                    		
//				                    	}
//				                    	else{
//				                    		Log.e("open SerialPort 0", "failed!");
//				                    	}
				                  		
				                  		if(BluetoothChatService.service!=null&&BluetoothChatService.service.getState()==3){
				                    		byte command=(byte) HouseControlActivityGroup.SAVE_Container_COMMAND;
				                    		preCommand=command;
				                    		BluetoothChatService.service.write(new byte[]{command,(byte) storeLocation});
				                    		HouseControlActivityGroup.deleteGoodsInfo(goodsId,HouseControlActivityGroup.this);
					                    	
				                    		SwitchActivity(HouseControlActivityGroup.currentId,0);
				                
				                    		mpDialog.dismiss();
				                    		
				                  		}
				                  		else{
				                    		Toast.makeText(HouseControlActivityGroup.this, "并未连接蓝牙服务器", Toast.LENGTH_LONG).show();
				                    	}
				                  		
				                    } 
				                }) 
				        .create()
				   
				        .show();
					}
					
					
				}
				else if(result.equals(new String(new byte[]{HouseControlActivityGroup.FAILED_REPLY}))){
					
					if(commandType==0)
						mpDialog.setMessage("物品保存失败，请重新尝试！");
					else
						mpDialog.setMessage("物品取出失败，请重新尝试！");
					mpDialog.dismiss();
				}
				
				break;
			}
		}
	};
	
	public static void deleteGoodsInfo(String goodsId,Context context){
		ContentValues values=new ContentValues(9); 
		 values.put("userId", 0);
//		 如果此物品并非之前装入过的物品，则插入新的条目
		 DataBaseHelper base=new DataBaseHelper(context);
			 base.updateGoodsTable("goodsInfo", values, goodsId);
			 Toast.makeText(context, "存储物品成功取出！", Toast.LENGTH_LONG).show();
	}
	
	public Handler getHandler(){
		return handler;
	}
	/**
	 *  
	 * 
	 * 顶部按钮图片 
	 *
	 * 
	 * **/
	int[] topbar_image_array = { R.drawable.home,
			R.drawable.personal, R.drawable.mygoods,
			R.drawable.searchgoods,R.drawable.alarm,R.drawable.settings};
	String[] menu_name={
			"首页","个人信息","我的物品","物品搜索","提醒","其他"
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		 Window win = getWindow();
	     win.requestFeature(Window.FEATURE_CUSTOM_TITLE);//注意顺序
	     
		setContentView(R.layout.activity_group);
		
		win.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);//注意顺序
	
		gvTopBar = (GridView) this.findViewById(R.id.gvTopBar);
		gvTopBar.setNumColumns(topbar_image_array.length);// 设置每行列数
		gvTopBar.setSelector(new ColorDrawable(Color.TRANSPARENT));// 选中的时候为透明色
		gvTopBar.setGravity(Gravity.CENTER);// 位置居中
		gvTopBar.setVerticalSpacing(0);// 垂直间隔
		
		int width = this.getWindowManager().getDefaultDisplay().getWidth()
				/ topbar_image_array.length;
		int height=this.getWindowManager().getDefaultDisplay().getHeight()
				/5;
		topImgAdapter = new ImageAdapter(this, topbar_image_array,menu_name, width, height,
				R.color.Black);
		gvTopBar.setAdapter(topImgAdapter);// 设置菜单Adapter
		gvTopBar.setOnItemClickListener(new ItemClickEvent());// 项目点击事件
		gvTopBar.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				SwitchActivity(arg2,0);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		container = (LinearLayout) findViewById(R.id.Container);
		SwitchActivity(0,0);//默认打开第0页
		this.refreshTitleBar(this);
	}

	class ItemClickEvent implements OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			SwitchActivity(arg2,0);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode==AddGoodsActivity.BARCODE_SCAN||requestCode==AddGoodsActivity.TAKE_PHOTO_REQUEST_CODE){
//                   MyGoodsActivity activity =(MyGoodsActivity)getLocalActivityManager().getCurrentActivity();
//                    activity.handleActivityResult(requestCode, resultCode, data);
//                this.SwitchActivity(MYGOODS, 0); 
                MyGoodsActivity activity =(MyGoodsActivity)getLocalActivityManager().getActivity("subActivity");
                activity.handleActivityResult(requestCode, resultCode, data);
                
//                Toast.makeText(getApplicationContext(), "picture got", Toast.LENGTH_LONG).show();
//            	Log.e("message in houseControl", "get it");
            }
            else if(requestCode==EditGoodsActivity.TAKE_PHOTO_REQUEST_CODE){
            	MyGoodsActivity activity =(MyGoodsActivity)getLocalActivityManager().getActivity("subActivity");
                activity.handleActivityResult(requestCode, resultCode, data);
//            	Log.e("message in houseControl", "get it");
            }
            
            else if(requestCode==EditGoodsActivity.EDIT_GOODS_INFO){
            	this.SwitchActivity(MYGOODS, 1); 
            	MyGoodsActivity activity =(MyGoodsActivity)getLocalActivityManager().getActivity("subActivity");
                activity.handleActivityResult(requestCode, resultCode, data);
            	
            }
    }
	
	public void startActivityForResult1(Intent intent,int requestCode){
		this.startActivityForResult(intent, requestCode);
	}
	
	 public  void handleActivityResult(int requestCode, int resultCode, Intent data) {
		 this.onActivityResult(requestCode, resultCode, data);
	 }
	/**
	 * 根据ID打开指定的Activity
	 * @param id GridView选中项的序号
	 */
	void SwitchActivity(int id,int tabNum)
	{
		topImgAdapter.SetFocus(id);//选中项获得高亮
		container.removeAllViews();//必须先清除容器中所有的View
		Intent intent =null;
		currentId=id;
		switch(id){
		case 0:{
			intent=new Intent(this,HomeActivity.class);
			break;
		}
		case 1:{
			intent=new Intent(this,PersonalInfoActivity.class);
			break;
		}
		case 2:{
			intent=new Intent(this,MyGoodsActivity.class);
			break;
		}
		case 3:{
			intent=new Intent(this,SearchGoodsActivity.class);
			break;
		}
		case 4:{
			intent=new Intent(this,AlarmActivity.class);
			break;
		}
		case 5:{
			intent=new Intent(this,OthersActivity.class);
			break;
		}
		}
		Bundle bundle = new Bundle();
        bundle.putParcelable("handler",HouseControlActivityGroup.this);
        bundle.putInt(TAB_NUM, tabNum);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//Activity 转为 View
		Window subActivity = getLocalActivityManager().startActivity(
				"subActivity", intent);
		//容器添加View
		container.addView(subActivity.getDecorView(),
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
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
	private Button titleLogInButton;
	private TextView titleText;
	static final String TITLE_BUTTON_TEXT1="登陆";
	static final String TITLE_BUTTON_TEXT2="退出";
	
	private void refreshTitleBar(final Context context){
		if(titleLogInButton==null){
			titleLogInButton=(Button)findViewById(R.id.titleLogInButton);
			titleLogInButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Session session=new Session(context);
					int userId=session.getUserId();
					if(userId==1){
						SwitchActivity(HouseControlActivityGroup.PERSONAL_INFO,PersonalInfoActivity.LOGIN);	
					}
					else{
					
						if(userId!=1){
							HouseControl.registerPublicUser(context);
							Toast.makeText(context, QUIT_SUCCESS,Toast.LENGTH_LONG).show();
							refreshTitleBar(context);
						}
						else{
							Toast.makeText(context, OFFLINE, Toast.LENGTH_LONG).show();
						}
					}
				}
				
			});
			
		}
		if(titleText==null){
			titleText=(TextView)findViewById(R.id.titleText);
		}
		Session session=new Session(this);
		int userId=session.getUserId();
		String userName=session.getUserName();
		if(userId==1){
			titleLogInButton.setText(TITLE_BUTTON_TEXT1);
			titleText.setText("当前用户："+PUBLIC_USER);
		}
		else{
			titleLogInButton.setText(TITLE_BUTTON_TEXT2);
			titleText.setText("当前用户："+userName);
		}
		
	}
	public static String getUserInfo(Context context){
		Session session=new Session(context);
		int userId=session.getUserId();
		if(userId==1){
			return "公共存储区";
		}
		else {
			DataBaseHelper database=new DataBaseHelper(context);
			return database.getUserName(userId)+"的存储区";
		}
	}
	@Override
    protected void onDestroy() {
      Session session=new Session(this);
      session.setUserId(1);
      session.setUserName("public");
      super.onDestroy();
    }
	 public void storeGoods(final int storeLocation){
		 	commandType=0;//设置当前进行的为存物品操作
		 	HouseControlActivityGroup.storeLocation=storeLocation;
		 	
//		 	if(SerialPortListener.openSerialPort(SERIAL_PORT, BAUD_RATE)){
//        		Log.e("open SerialPort 0", "success!");
//        		byte command=HouseControlActivityGroup.GET_Container_COMMAND;
//        		preCommand=HouseControlActivityGroup.GET_Container_COMMAND;
//        		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
//        		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
//        		mpDialog = new ProgressDialog(HouseControlActivityGroup.this);  
//                mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
//                mpDialog.setTitle("提示");//设置标题  
//                mpDialog.setIcon(R.drawable.mygoods);//设置图标  
//                mpDialog.setMessage("发送保存物品命令......"); 
////通过设置不明确，进度条在0——100之间来回移动，形成动画效果
//                mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
//                mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消  
//                mpDialog.show(); 
//		 	}
//		 	else{
//        		Log.e("open SerialPort 0", "failed!");
//        	}
			if(BluetoothChatService.service!=null){
        		Log.e("open SerialPort 0", "success!");
        		byte command=HouseControlActivityGroup.GET_Container_COMMAND;
        		preCommand=HouseControlActivityGroup.GET_Container_COMMAND;
//        		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
//        		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
        		BluetoothChatService.service.write(new byte[]{command,(byte) storeLocation});
        		mpDialog = new ProgressDialog(HouseControlActivityGroup.this);  
                mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
                mpDialog.setTitle("提示");//设置标题  
                mpDialog.setIcon(R.drawable.mygoods);//设置图标  
                mpDialog.setMessage("发送保存物品命令......"); 
//通过设置不明确，进度条在0——100之间来回移动，形成动画效果
                mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
                mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消  
                mpDialog.show(); 
		 	}
		 	else{
        	Log.e("尚未与命令接收端建立蓝牙连接，现在转向蓝牙设置界面", "failed!");
        		
        		Toast.makeText(this,"尚未与命令接收端建立蓝牙连接，现在转向蓝牙设置界面", Toast.LENGTH_LONG).show();
        		SwitchActivity(SETTINGS,0);
        	}
	 }
	 
	 public void getGoodsOut(final int storeLocation,final String goodsId){
		 deleteGoodsInfo(goodsId, getApplicationContext());
	 }
//		 	commandType=1;//设置当前进行的为取物品操作
//		 	HouseControlActivityGroup.storeLocation=storeLocation;
//		 	HouseControlActivityGroup.goodsId=goodsId;
//		 	if(BluetoothChatService.service!=null){
//	     		Log.e("開啟藍牙，发送数据", "success!");
//	     		byte command=HouseControlActivityGroup.GET_Container_COMMAND;
//	     		preCommand=HouseControlActivityGroup.GET_Container_COMMAND;
//	     		BluetoothChatService.service.write(new byte[]{command,(byte) storeLocation});
//	     		mpDialog = new ProgressDialog(HouseControlActivityGroup.this);  
//	             mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
//	             mpDialog.setTitle("取出物品");//设置标题  
//	             mpDialog.setIcon(R.drawable.mygoods);//设置图标  
//	             mpDialog.setMessage("发送取出物品命令......"); 
//	//通过设置不明确，进度条在0——100之间来回移动，形成动画效果
//	             mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
//	             mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消  
//	             mpDialog.show(); 
//			 	}
//			 else{
//					Log.e("尚未与命令接收端建立蓝牙连接，现在转向蓝牙设置界面", "failed!");
//	        		
//	        		Toast.makeText(this,"尚未与命令接收端建立蓝牙连接，现在转向蓝牙设置界面", Toast.LENGTH_LONG).show();
//	        		SwitchActivity(SETTINGS,0);
//	     	}
//		 	if(SerialPortListener.openSerialPort(SERIAL_PORT, BAUD_RATE)){
//     		Log.e("open SerialPort 0", "success!");
//     		byte command=HouseControlActivityGroup.GET_Container_COMMAND;
//     		preCommand=HouseControlActivityGroup.GET_Container_COMMAND;
//     		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
//     		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
//     		mpDialog = new ProgressDialog(HouseControlActivityGroup.this);  
//             mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
//             mpDialog.setTitle("取出物品");//设置标题  
//             mpDialog.setIcon(R.drawable.mygoods);//设置图标  
//             mpDialog.setMessage("发送取出物品命令......"); 
////通过设置不明确，进度条在0——100之间来回移动，形成动画效果
//             mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
//             mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消  
//             mpDialog.show(); 
//		 	}
//		 	else{
//     		Log.e("open SerialPort 0", "failed!");
//     	}

//        	}
//        	else{
//        		Log.e("open SerialPort 0", "failed!");
//        	}
//	    	new AlertDialog.Builder(this) 
//	        .setTitle("确认物品") 
//	        .setMessage("是否已经将物品放在起运平台？") 
//	        .setPositiveButton("是", 
//	                new DialogInterface.OnClickListener() { 
//	                    public void onClick(DialogInterface dialog, 
//	                            int whichButton) {
	                        /* User clicked OK so do some stuff */ 
//	                  		if(SerialPortListener.openSerialPort(SERIAL_PORT, BAUD_RATE)){
//	                    		Log.e("open SerialPort 0", "success!");
//	                    		byte command=(byte) HouseControlActivityGroup.GET_Container_COMMAND;
//	                    		SerialPortListener.writeDataToSerialPort(new byte[]{command,(byte) storeLocation});
//	                    		new SerialPortListener(HouseControlActivityGroup.this.getHandler()).start();
//	                    		mpDialog = new ProgressDialog(HouseControlActivityGroup.this);  
//	                            mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置风格为圆形进度条  
//	                            mpDialog.setTitle("提示");//设置标题  
//	                            mpDialog.setIcon(R.drawable.mygoods);//设置图标  
//	                            mpDialog.setMessage("发送保存物品命令......"); 
////通过设置不明确，进度条在0——100之间来回移动，形成动画效果
//	                            mpDialog.setIndeterminate(false);//设置进度条是否为不明确  
//	                            mpDialog.setCancelable(true);//设置进度条是否可以按退回键取消  
//	                            mpDialog.show();  
//	                    	}
//	                    	else{
//	                    		Log.e("open SerialPort 0", "failed!");
//	                    	}
//	                    } 
//	                }) 
//	        .setNegativeButton("否", 
//	                new DialogInterface.OnClickListener() { 
//	                    public void onClick(DialogInterface dialog, 
//	                            int whichButton) {
//	                        /* User clicked OK so do some stuff */ 
//	                    } 
//	                }) 
//	        .create()
//	        .show();
	private   ArrayAdapter< String>  getWebsiteAdapter(){
			
			ArrayAdapter< String> adapter = new ArrayAdapter< String>(
					this,
					android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(
					android.R.layout.simple_spinner_dropdown_item);
					for(int i=0;i<websiteNames.length;i++){
						adapter.add(websiteNames[i]);
						}
			return adapter;
		}    
	 
	 public void startSearchDialog(final String searchContent){
		 LinearLayout l=(LinearLayout)getLayoutInflater().inflate(R.layout.search_dialog, null);
		 final Spinner sp=(Spinner)l.findViewById(R.id.searchWebsiteSpinner);
		 sp.setAdapter(getWebsiteAdapter());
		 AlertDialog.Builder builder=new AlertDialog.Builder(this);
			builder.setTitle("选择搜索网站")
			.setView(l)
			.setPositiveButton("搜索", new DialogInterface.OnClickListener() { 
                public void onClick(DialogInterface dialog, 
                        int whichButton) {
                    /* User clicked OK so do some stuff */
                	int position=sp.getSelectedItemPosition();
                	String prefix=searchURL[position];
                	byte[] bytes=searchContent.getBytes();
                	for(int i=0;i<bytes.length;i++){
            			prefix+="%"+Integer.toHexString(bytes[i]&0xff);
            		}
                	Uri uri = Uri.parse(prefix);
                	Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                	startActivity(intent);
                } 
            })
			.setNegativeButton("取消", new DialogInterface.OnClickListener() { 
                public void onClick(DialogInterface dialog, 
                        int whichButton) {
                    /* User clicked OK so do some stuff */ 
              		
              		
                } 
            })
			.setCancelable(true)
			.create(); 
			builder.show();
	 }
	 
	    
}

class ImageAdapter extends BaseAdapter {
	private Context mContext; 
	private ImageView[] imgItems;
	private View[] items;
	private int selResId;
	private int[] imageId;
	private String[] menuName;
    public ImageAdapter(Context c,int[] picIds,String[] menuName,int width,int height,int selResId) { 
        mContext = c; 
        this.selResId=selResId;
        imageId=picIds;
        this.menuName=menuName;
        imgItems=new ImageView[picIds.length];
        items=new View[picIds.length];
        for(int i=0;i<picIds.length;i++)
        {
        	View convertView;
        	LayoutInflater mInflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       	  	convertView=mInflater.inflate(R.layout.menu_item, null);
       	  	TextView text=(TextView)convertView.findViewById(R.id.menu_item_text);
        	text.setText(this.menuName[i]);
       	  	ImageView img=(ImageView)convertView.findViewById(R.id.menu_item_image);
        	img.setImageResource(picIds[i]);
//        	如果此行设置为true,则表示不改变图片的长宽比
        	img.setAdjustViewBounds(true); 
        	img.setPadding(2, 2, 2, 0); 
        	  LayoutParams para;
              para = img.getLayoutParams();
              para.height = (int) (height*2.3);
              para.width = width;
              img.setLayoutParams(para);
              text.setTextSize((float) (height*0.14));
        	convertView.setLayoutParams(new GridView.LayoutParams(width, (int) (0.88*height)));
        	items[i]=convertView;
//       	  	imgItems[i] = new ImageView(mContext); 
//        	imgItems[i].setLayoutParams(new GridView.LayoutParams(width, height));//设置ImageView宽高 
//        	imgItems[i].setAdjustViewBounds(false); 
//        	//imgItems[i].setScaleType(ImageView.ScaleType.CENTER_CROP); 
//        	imgItems[i].setPadding(2, 2, 2, 2); 
//        	imgItems[i].setImageResource(picIds[i]); 
        }
    } 
 
    public int getCount() { 
        return items.length; 
    } 
 
    public Object getItem(int position) { 
        return position; 
    } 
 
    public long getItemId(int position) { 
        return position; 
    } 
 
    /** 
     * 设置选中的效果 
     */  
    public void SetFocus(int index)  
    {  
    	Log.e("index",""+index);
        for(int i=0;i<items.length;i++)  
        {  
            if(i!=index)  
            {  
            	items[i].setBackgroundResource(0);//恢复未选中的样式
            }  
        }  
        items[index].setBackgroundResource(selResId);//设置选中的样式
    }  
    
    public View getView(int position, View convertView, ViewGroup parent) { 
        View view; 
        if (convertView == null) { 
        	view=items[position];
        } else { 
            view = (View) convertView; 
        } 
        return view; 
    }
    
//    此函数被subActivity调用执行存储物品命令
//    参数为物品需要保存的箱子
   

} 

