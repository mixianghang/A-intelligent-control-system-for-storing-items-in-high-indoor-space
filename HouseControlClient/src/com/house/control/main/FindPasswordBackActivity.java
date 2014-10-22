package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DEScode;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class FindPasswordBackActivity extends Activity implements OnClickListener {
	
	
//	用于确认用户身份界面
	private Button confirmUserButton;
	private EditText userNameEdit ;
	private EditText securityAnswerEdit;
	private Spinner securityQuestionSpinner;
	

	
	
//	用于重置密码界面
	private Button editPasswordButton;
	private EditText newPasswordEdit;
	private EditText confirmPasswordEdit;
	
//	ViewFlipper
	ViewFlipper flipper;
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_password_back);
        
        newPasswordEdit=(EditText)findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit=(EditText)findViewById(R.id.confirmPasswordEdit);
        
        editPasswordButton=(Button)findViewById(R.id.editPasswordButton);

        
        editPasswordButton.setOnClickListener(this);
        userNameEdit=(EditText)findViewById(R.id.userNameEdit);
        securityAnswerEdit=(EditText)findViewById(R.id.securityAnswerEdit);
        
        securityQuestionSpinner=(Spinner)findViewById(R.id.securityQuestionSpinner);
       
    
        securityQuestionSpinner.setPrompt("选择安全问题"); 
       securityQuestionSpinner.setAdapter(RegisterUserActivity.getSecurityQuestionAdapter(this));
     
       confirmUserButton=(Button)findViewById(R.id.confirmUserButton);
      
       
       confirmUserButton.setOnClickListener(this);
       Intent intent=getIntent();
       if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 handler=((PersonalInfoActivity)bundle.get("handler")).getHandler();
	    	 
	     }
       
       flipper=(ViewFlipper)findViewById(R.id.flipper);
       
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.confirmUserButton:{
			String userName=userNameEdit.getText().toString();
			String securityQuestionAnswer=securityAnswerEdit.getText().toString();
			
			DataBaseHelper database=new DataBaseHelper(this);
			int result=database.checkUser("userName=? and securityQuestionId="+(securityQuestionSpinner.getSelectedItemPosition()+1)+" and securityAnswer=?", new String[]{userName,securityQuestionAnswer});
			if(result==-1){
				Toast.makeText(this, "您 输入的数据无法验证您的身份 ！",Toast.LENGTH_LONG).show();
				break;
			}
			Toast.makeText(this, "身份验证成功，请重置您的密码！",Toast.LENGTH_LONG).show();
			int userId=database.getUserId(userName);
			Session session=new Session(this);
			session.setUserId(userId);
			session.setUserName(userName);
			flipper.showNext();
			
			break;
		}
		case R.id.editPasswordButton:{
			String newPassword=newPasswordEdit.getText().toString();
			String confirmPassword=confirmPasswordEdit.getText().toString();
			if(!newPassword.equals(confirmPassword)){
				Toast.makeText(this, "您输入的新密码与确认密码不相符！",Toast.LENGTH_LONG).show();
				
				break;
			}
			DataBaseHelper database=new DataBaseHelper(this);
			Session session=new Session(this);
			
			
				ContentValues values=new ContentValues(1);
				values.put(DataBaseHelper.PASSWORD, DEScode.encrypt(newPassword));
				if(database.updateTable(DataBaseHelper.FAMILY_MEMBER_INFO, values,session.getUserId())){
					Toast.makeText(this, "修改个人密码成功！",Toast.LENGTH_LONG).show();
					Message message=Message.obtain(handler, PersonalInfoActivity.FIND_PASSWORD_BACK);
					message.sendToTarget();
				}
		
			break;
		}
		
		}
		
		
	}
}
