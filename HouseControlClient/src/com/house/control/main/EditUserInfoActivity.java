package com.house.control.main;

import java.util.HashMap;

import com.house.control.R;
import com.house.control.datastore.DEScode;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class EditUserInfoActivity extends Activity implements OnClickListener {
	public static final int EDIT_USERINFO_SUCCESS=1;
	public static final int  CANCEL_EDIT_USERINFO=2;
	
	private Button cancelButton;
	private Button saveButton;
	
	private EditText userNameEdit ;
	private EditText passwordEdit;
	private EditText securityAnswerEdit;
	private Spinner securityQuestionSpinner;
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.edit_user_info,null);  
        setContentView(contentView);
        
        userNameEdit=(EditText)findViewById(R.id.userNameEdit);
        passwordEdit=(EditText)findViewById(R.id.passwordEdit);
        securityAnswerEdit=(EditText)findViewById(R.id.securityAnswerEdit);
        
        securityQuestionSpinner=(Spinner)findViewById(R.id.securityQuestionSpinner);

      
        securityQuestionSpinner.setPrompt("选择安全问题"); 
       securityQuestionSpinner.setAdapter(RegisterUserActivity.getSecurityQuestionAdapter(this));
       
       saveButton=(Button)findViewById(R.id.saveButton);
       cancelButton=(Button)findViewById(R.id.cancelButton);
       
       saveButton.setOnClickListener(this);
       cancelButton.setOnClickListener(this);
       
       initialUserInfo();
       Intent intent=getIntent();
       if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 handler=((PersonalInfoActivity)bundle.get("handler")).getHandler();
	    	 
	     }
	}
	
	private void initialUserInfo(){
		Session session=new Session(this);
		int userId=session.getUserId();
//		Log.e("userId", ""+userId);
		DataBaseHelper database= new DataBaseHelper(this);
		HashMap<String,String> map=database.getUserInfo(userId);
		
		String userName=map.get(DataBaseHelper.USERNAME);
		long securityQuestionId=Long.parseLong(map.get(DataBaseHelper.SECURITY_QUESTION_ID));
		String securityAnswer=map.get(DataBaseHelper.SECURITY_ANSWER);;
		userNameEdit.setText(userName);
		securityQuestionSpinner.setSelection((int) (securityQuestionId-1),true);
		securityAnswerEdit.setText(securityAnswer);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.saveButton:{
			DataBaseHelper database=new DataBaseHelper(this);
			String userName=userNameEdit.getText().toString();
			Session session=new Session(this);
			if(!database.checkUserNameExist(userName)&&!userName.equals(session.getUserName())){
				Toast.makeText(this, "用户名已被其他人注册！",Toast.LENGTH_LONG).show();
				break;
			}
			else{
				String password=passwordEdit.getText().toString();
				int userId=database.checkUser("userName=? and password=?", new String[]{session.getUserName(),DEScode.encrypt(password)});
				if(userId==-1){
					Toast.makeText(this, "您输入的密码有误！",Toast.LENGTH_LONG).show();
					break;
				}
				long questionPosition=securityQuestionSpinner.getSelectedItemPosition()+1;
				String questionAnswer=securityAnswerEdit.getText().toString();
				ContentValues values=new ContentValues(3);
				values.put(DataBaseHelper.USERNAME, userName);
				values.put(DataBaseHelper.SECURITY_QUESTION_ID, questionPosition);
				values.put(DataBaseHelper.SECURITY_ANSWER, questionAnswer);
				if(database.updateTable(DataBaseHelper.FAMILY_MEMBER_INFO, values,session.getUserId())){
					Toast.makeText(this, "修改个人信息成功！",Toast.LENGTH_LONG).show();
					Message message=Message.obtain(handler, PersonalInfoActivity.EDIT_PERSONAL_INFO_SUCCESS);
					message.sendToTarget();
				}
				
			}
			break;
		}
		case R.id.cancelButton:{
			initialUserInfo();
			break;
		}
		}
		
	}
}
