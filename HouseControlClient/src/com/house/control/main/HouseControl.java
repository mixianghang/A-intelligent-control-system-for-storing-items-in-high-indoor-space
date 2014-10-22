package com.house.control.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.house.control.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.house.control.datastore.*;
public class HouseControl extends Activity implements OnClickListener, OnItemClickListener {
	
	public static final int PUBLIC_USER_ID=1;
	public static final String USERNAME="public";
	public static final int GOODS_TYPE_NUM=4;
	public static final int LOGIN_REQUEST_CODE=1;
	public static final int FORGET_PASSWORD_REQUEST_CODE=2;
	public static final int REGISTER_REQUEST_CODE=3;
	public static final int EDIT_USERINFO_REQUEST_CODE=4;
	public static final int EDIT_PASSWORD_REQUEST_CODE=5;
	public static final int SETTING_REQUEST_CODE=6;
	public static final int SEARCH_GOODS_REQUEST_CODE=7;
	public static final int TAKE_PHOTO_REQUEST_CODE=8;
	public static final int ADD_GOODS_REQUEST_CODE=9;
	
//	各button的点 击结果
	public static final String QUIT_SUCCESS="退出成功！";
	public static final String OFFLINE="您并未登陆！";
	
	private TextView userNameText;
	private Button logInButton;
	private Button registerButton;
	private Button editUserInfoButton;
	private Button forgetPasswordButton;
	private Button quitButton;
	private Button editPasswordButton;
	private Button takeGoodsButton;
	private Button settingButton;
	private Button searchButton;
	private Button addGoodsButton;
	private DataBaseHelper database;
	private ListView goodsListView;
	private Cursor[] goodsCursorsByType;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);//注意顺序
        setContentView(R.layout.house_control);
        
        registerPublicUser(this);
        userNameText=(TextView)findViewById(R.id.userNameText);
        String text=getUserInfo(this);
        userNameText.setText(text);
        
        logInButton=(Button)findViewById(R.id.logInButton);
        registerButton=(Button)findViewById(R.id.registerButton);
        editUserInfoButton=(Button)findViewById(R.id.editUserInfoButton);
        forgetPasswordButton=(Button)findViewById(R.id.forgetPasswordButton);
        quitButton=(Button)findViewById(R.id.quitButton);
        editPasswordButton=(Button)findViewById(R.id.editPasswordButton);
        takeGoodsButton=(Button)findViewById(R.id.takeGoodsButton);
        settingButton=(Button)findViewById(R.id.settingButton);
        searchButton=(Button)findViewById(R.id.searchButton);
        addGoodsButton=(Button)findViewById(R.id.addGoodsButton);
        goodsListView=(ListView)findViewById(R.id.goodsListView);
        
//        设置监听的事件
        logInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        editUserInfoButton.setOnClickListener(this);
        forgetPasswordButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        editPasswordButton.setOnClickListener(this);
        takeGoodsButton.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        addGoodsButton.setOnClickListener(this);
        goodsListView.setOnItemClickListener(this);
        
        this.refreshTitleBar(this);
        refreshGoodsList();//更新当前用户的物品列表
//        takePhoto(TAKE_PHOTO_REQUEST_CODE);
        File file=new File(AddGoodsActivity.IMG_STORE_LOCATION);
        if(!file.exists()&&!file.isDirectory()){
        	file.mkdir();
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
	
	private void refreshGoodsList(){
		database=new DataBaseHelper(getApplicationContext());
        Session session=new Session(this);
        int userId=session.getUserId();
        goodsCursorsByType=new Cursor[]{
        		database.getGoodsList(1, userId),
        		database.getGoodsList(2, userId),
        		database.getGoodsList(3, userId),
        		database.getGoodsList(4, userId)
        		
        };
//        Toast.makeText(this, ""+goodsCursorsByType[0].getCount(), Toast.LENGTH_LONG).show();
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.goods_list_by_type,
                new String[]{"img","goodsNum"},
                new int[]{R.id.goodsTypeImage,R.id.goodsNumText});
        goodsListView.setAdapter(adapter);
	}
	
//	各个buton的点击事件处理函数
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.logInButton:{
			Intent intent=new Intent(this,LogInActivity.class);
			startActivityForResult(intent,LOGIN_REQUEST_CODE);
			break;
		}
		case R.id.registerButton:{
			Intent intent=new Intent(this,RegisterUserActivity.class);
			startActivityForResult(intent,REGISTER_REQUEST_CODE);
			break;
		}
		case R.id.quitButton:{
			Session session=new Session(this);
			if(session.getUserId()!=1){
				registerPublicUser(this);
				this.refreshGoodsList();
				userNameText.setText(getUserInfo(this));
				Toast.makeText(this, QUIT_SUCCESS,Toast.LENGTH_LONG).show();
				this.refreshTitleBar(this);
			}
			else{
				Toast.makeText(this, OFFLINE, Toast.LENGTH_LONG).show();
			}
			break;
		}
		case R.id.editUserInfoButton:{
			Session session=new Session(this);
			int userId=session.getUserId();
			if(userId==1){
				Toast.makeText(this, OFFLINE, Toast.LENGTH_LONG).show();
				break;
			}
			Intent  intent=new Intent(this,EditUserInfoActivity.class);
			startActivityForResult(intent,EDIT_USERINFO_REQUEST_CODE);
			break;
		}
		case R.id.editPasswordButton:{
			Session session=new Session(this);
			int userId=session.getUserId();
			if(userId==1){
				Toast.makeText(this, OFFLINE, Toast.LENGTH_LONG).show();
				break;
			}
			Intent  intent=new Intent(this,EditPasswordActivity.class);
			startActivityForResult(intent,EDIT_PASSWORD_REQUEST_CODE);
			break;
		}
		case R.id.forgetPasswordButton:{
			Intent intent=new Intent(this,ConfirmThroughQuestionActivity.class);
			startActivityForResult(intent,FORGET_PASSWORD_REQUEST_CODE);
			break;
		}
		case R.id.settingButton:{
			Intent intent=new Intent(this,SettingActivity.class);
			startActivityForResult(intent,SETTING_REQUEST_CODE);
			break;
		}
		case R.id.searchButton:{
			Intent intent=new Intent(this,SearchGoodsActivity.class);
			startActivityForResult(intent,SEARCH_GOODS_REQUEST_CODE);
			break;
		}
		case R.id.addGoodsButton:{
			Intent intent=new Intent(this,AddGoodsActivity.class);
			startActivityForResult(intent,ADD_GOODS_REQUEST_CODE);
			break;
		}
		}
	}
	
//	分类物品列表的点击触发事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

//	为simpleAdapter准备数据
	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("img", R.drawable.clothes);
        map.put("goodsNum", ""+ goodsCursorsByType[0].getCount()+"件");
        list.add(map);
       
 
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.food);
        map.put("goodsNum", ""+goodsCursorsByType[1].getCount()+"份");
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.drink);
        map.put("goodsNum", ""+goodsCursorsByType[2].getCount()+"件");
       
        list.add(map);
        map = new HashMap<String, Object>();
        map.put("img", R.drawable.book);
        map.put("goodsNum", ""+goodsCursorsByType[3].getCount()+"本");
       
        list.add(map);
        return list;
    }
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case LOGIN_REQUEST_CODE:{
				switch(resultCode){
				case LogInActivity.CANCEL_LOGIN:{
					break;
				}
				case LogInActivity.FORGET_PASSWORD:{
					Intent intent=new Intent(this,ConfirmThroughQuestionActivity.class);
					startActivityForResult(intent,FORGET_PASSWORD_REQUEST_CODE);
					break;
				}
				case LogInActivity.LOGIN_SUCCESS:{
					refreshGoodsList();
					userNameText.setText(getUserInfo(this));
					this.refreshTitleBar(this);
					break;
				}
				}
		}
		case REGISTER_REQUEST_CODE:{
			switch(resultCode){
			case RegisterUserActivity.CANCEL_REGISTER:{
				break;
			}
			case RegisterUserActivity.REGISTER_SUCCESS:{
////				注册成功可以立即转向登陆
//				Intent intent=new Intent(this,LogInActivity.class);
//				startActivityForResult(intent,LOGIN_REQUEST_CODE);
				break;
			}
			
			}
		}
		case EDIT_USERINFO_REQUEST_CODE:{
			switch(resultCode){
			case EditUserInfoActivity.CANCEL_EDIT_USERINFO:{
				break;
			}
			case EditUserInfoActivity.EDIT_USERINFO_SUCCESS:{
////				注册成功可以立即转向登陆
//				Intent intent=new Intent(this,LogInActivity.class);
//				startActivityForResult(intent,LOGIN_REQUEST_CODE);
				userNameText.setText(getUserInfo(this));
				break;
			}
		}
		}
		case SETTING_REQUEST_CODE:{
			switch(resultCode){
			case SettingActivity.CANCEL_SETTING:{
				break;
			}
			case SettingActivity.SETTING_SUCCESS:{
//此处用于更新提醒的数目
				break;
			}
		}
		}
		case TAKE_PHOTO_REQUEST_CODE:{
			switch(resultCode){
			case Activity.RESULT_OK:{
				Bitmap bit=(Bitmap)data.getParcelableExtra("data");
				if(bit!=null){
					Toast.makeText(this, "get it",Toast.LENGTH_LONG).show();
				}
				break;
			}
			case Activity.RESULT_CANCELED:{
//此处用于更新提醒的数目
				break;
			}
			}
		}
		case ADD_GOODS_REQUEST_CODE:{
			switch(resultCode){
			case Activity.RESULT_OK:{
				this.refreshGoodsList();
				break;
			}
			case Activity.RESULT_CANCELED:{
//此处用于更新提醒的数目
				break;
			}
			}
		}
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
//		在此处注销私有用户登陆
		registerPublicUser(this);
	}
	
//	将session状态改为公共用户登陆
	public static void registerPublicUser(Context context){
		Session session=new Session(context);
		session.setUserId(PUBLIC_USER_ID);
		session.setUserName(USERNAME);
	}
	void takePhoto(int requestCode){
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  startActivityForResult(intent, requestCode);
	  }
	
	private Button titleLogInButton;
	private TextView titleText;
	static final String TITLE_BUTTON_TEXT1="登陆";
	static final String TITLE_BUTTON_TEXT2="退出";
	
	private void refreshTitleBar(final Context context){
//		if(titleLogInButton==null){
//			titleLogInButton=(Button)findViewById(R.id.titleLogInButton);
//			titleLogInButton.setOnClickListener(new OnClickListener(){
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					Session session=new Session(context);
//					int userId=session.getUserId();
//					if(userId==1){
//						Intent intent=new Intent(context,LogInActivity.class);
//						startActivityForResult(intent,LOGIN_REQUEST_CODE);
//						
//					}
//					else{
//					
//						if(userId!=1){
//							registerPublicUser(context);
//							refreshGoodsList();
//							userNameText.setText(getUserInfo(context));
//							Toast.makeText(context, QUIT_SUCCESS,Toast.LENGTH_LONG).show();
//							refreshTitleBar(context);
//						}
//						else{
//							Toast.makeText(context, OFFLINE, Toast.LENGTH_LONG).show();
//						}
//					}
//				}
//				
//			});
//			
//		}
//		if(titleText==null){
//			titleText=(TextView)findViewById(R.id.titleText);
//		}
//		Session session=new Session(this);
//		int userId=session.getUserId();
//		String userName=session.getUserName();
//		if(userId==1){
//			titleLogInButton.setText(HouseControl.TITLE_BUTTON_TEXT1);
//		}
//		else{
//			titleLogInButton.setText(HouseControl.TITLE_BUTTON_TEXT2);
//		}
//		titleText.setText(userName);
	}
//	
}


