package com.house.control.datastore;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {
	private static final String SESSION_NAME="session";
	private static final String USER_NAME="userName";
	private static final String USER_ID="userId";
	private Context context=null;
	public Session(Context context){
		this.context=context;
	}
	public boolean setUserName(String userName){
//		起到存储当前用户名及ID的作用
		SharedPreferences session=context.getSharedPreferences(SESSION_NAME, 0);
		SharedPreferences.Editor editor=session.edit();
		editor.putString(USER_NAME, userName);
		return editor.commit();
	}
	
	public boolean setUserId(int userId){
//		起到存储当前用户名及ID的作用
		SharedPreferences session=context.getSharedPreferences(SESSION_NAME, 0);
		SharedPreferences.Editor editor=session.edit();
		editor.putInt(USER_ID, userId);
		return editor.commit();
	}
	
	public String getUserName(){
		SharedPreferences session=context.getSharedPreferences(SESSION_NAME, 0);
		return session.getString(USER_NAME, "public");
	}
	 
	public int  getUserId(){
		SharedPreferences session=context.getSharedPreferences(SESSION_NAME, 0);
		return session.getInt(USER_ID, 1);
	}
}
