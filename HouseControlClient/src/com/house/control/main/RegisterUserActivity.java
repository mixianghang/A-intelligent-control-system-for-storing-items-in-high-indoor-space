package com.house.control.main;

import java.util.Iterator;
import java.util.List;

import com.house.control.R;
import com.house.control.datastore.DEScode;
import com.house.control.datastore.DataBaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterUserActivity extends Activity implements OnClickListener {
	//	给caller的结果码
	public static final int CANCEL_REGISTER=1;
	public static final int REGISTER_SUCCESS=2;
	
	private Button registerButton;
	
	private EditText userNameEdit ;
	private EditText passwordEdit;
	private EditText confirmPasswordEdit;
	private EditText securityAnswerEdit;
	private Spinner securityQuestionSpinner;
	
	

	
	private final String 	FAILED_LOGIN="您输入的用户名或密码有误！";
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.register_user,null);  
        setContentView(contentView);
        
        Intent intent=getIntent();
        if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 handler=((PersonalInfoActivity)bundle.get("handler")).getHandler();
	    	 
	     }
        userNameEdit=(EditText)findViewById(R.id.userNameEdit);
        passwordEdit=(EditText)findViewById(R.id.passwordEdit);
        confirmPasswordEdit=(EditText)findViewById(R.id.confirmPasswordEdit);
        securityAnswerEdit=(EditText)findViewById(R.id.securityAnswerEdit);
        
        securityQuestionSpinner=(Spinner)findViewById(R.id.securityQuestionSpinner);
        
        securityQuestionSpinner.setPrompt("选择安全问题"); 
        securityQuestionSpinner.setAdapter(getSecurityQuestionAdapter(this));
        registerButton=(Button)findViewById(R.id.registerButton);
       
        
        registerButton.setOnClickListener(this);
        
    

	}

	
	public static ArrayAdapter< String>  getSecurityQuestionAdapter(Context context){

		DataBaseHelper data=new DataBaseHelper(context);
		List<String> list=data.getSecurityQuestionContent();
		
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				context,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				Iterator<String> it=list.iterator();
				while(it.hasNext()){
					adapter.add(it.next());
				}
		return adapter;
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.registerButton:{
			DataBaseHelper database=new DataBaseHelper(this);
			String userName=userNameEdit.getText().toString();
			if(!database.checkUserNameExist(userName)){
				Toast.makeText(this, "用户名已存在！",Toast.LENGTH_LONG).show();
				break;
			}
			String password=passwordEdit.getText().toString();
			String confirmPassword=confirmPasswordEdit.getText().toString();

			if(password.equals(confirmPassword)){
				
				long questionPosition=securityQuestionSpinner.getSelectedItemPosition()+1;
				String questionAnswer=securityAnswerEdit.getText().toString();
				if(questionAnswer.length()==0||userName.length()==0||password.length()==0||confirmPassword.length()==0){
					Toast.makeText(this, "信息不完整", Toast.LENGTH_LONG).show();
					return ;
				}
				ContentValues values=new ContentValues(5);
				values.put(DataBaseHelper.USERNAME, userName);
				values.put(DataBaseHelper.SECURITY_QUESTION_ID, questionPosition);
				values.put(DataBaseHelper.SECURITY_ANSWER, questionAnswer);
				values.put(DataBaseHelper.PASSWORD, DEScode.encrypt(password));
				values.put(DataBaseHelper.MAXTIME_OF_STORE, "432000000");
				values.put(DataBaseHelper.TIME_ALARM_OF_QUALITY, "432000000");
				values.put(DataBaseHelper.USER_ID, database.getRowsCount(DataBaseHelper.FAMILY_MEMBER_INFO)+1);
				if(database.insertIntoTable(DataBaseHelper.FAMILY_MEMBER_INFO, values)){
					Toast.makeText(this, "注册成功！",Toast.LENGTH_LONG).show();
					Message message=Message.obtain(handler, PersonalInfoActivity.REGISTER_SUCCESS);
					message.sendToTarget();
				}
				else{
					Toast.makeText(this, "注册失败，请重新注册！",Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(this, "两次输入的密码不一样！",Toast.LENGTH_LONG).show();
			}
			break;
		}
		
		}
	}




	

}
