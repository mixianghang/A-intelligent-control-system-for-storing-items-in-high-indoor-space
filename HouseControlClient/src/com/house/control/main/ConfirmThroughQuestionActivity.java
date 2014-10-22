package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class ConfirmThroughQuestionActivity extends Activity implements OnClickListener{
	public static final int CONFIRM=1;
	public static final int EDIT_PASSWORD_REQUEST_CODE=1;
	

	private Button confirmUserButton;

	
	private EditText userNameEdit ;
	private EditText securityAnswerEdit;
	private Spinner securityQuestionSpinner;
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.login_through_question,null);  
        setContentView(contentView);
        
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
			int userId=database.getUserId(userName);
			Session session=new Session(this);
			session.setUserId(userId);
			session.setUserName(userName);
			Intent intent=new Intent(this,ResetPasswordActivity.class);
			startActivityForResult(intent,EDIT_PASSWORD_REQUEST_CODE);
			break;
		}
		
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
	

	
}
