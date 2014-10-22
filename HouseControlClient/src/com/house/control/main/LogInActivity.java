package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DEScode;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends Activity implements OnClickListener {
//	给caller的结果码
	public static final int CANCEL_LOGIN=1;
	public static final int LOGIN_SUCCESS=2;
	public static final int FORGET_PASSWORD=3;

	private Button logInButton;
	private EditText userNameEdit ;
	private EditText passwordEdit;
	
	private final String 	FAILED_LOGIN="您输入的用户名或密码有误！";
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        
        logInButton=(Button)findViewById(R.id.logInButton);
       
        userNameEdit=(EditText)findViewById(R.id.userNameEdit);
        passwordEdit=(EditText)findViewById(R.id.passwordEdit);
        
        logInButton.setOnClickListener(this);
        Intent intent=getIntent();
        if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 handler=((PersonalInfoActivity)bundle.get("handler")).getHandler();
	    	 
	     }

	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.logInButton:{
			String userName=userNameEdit.getText().toString();
			String password=passwordEdit.getText().toString();
			if(userName.length()==0||password.length()==0){
				Toast.makeText(this, "输入信息不完整！", Toast.LENGTH_LONG).show();
				return ;
			}
			DataBaseHelper database=new DataBaseHelper(this);
			int userId=database.checkUser("userName=? and password=?", new String[]{userName,DEScode.encrypt(password)});
			if(userId!=-1){
				Session session=new Session(this);
				session.setUserId(userId);
				session.setUserName(userName);
				Toast.makeText(this, "登陆成功！", Toast.LENGTH_LONG).show();
				Message message=Message.obtain(handler, PersonalInfoActivity.LOGIN_SUCCESS);
				message.sendToTarget();
			}
			else{
				Toast.makeText(this, FAILED_LOGIN, Toast.LENGTH_LONG).show();
			}
			break;
		}
		
		}
	}
}
