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
import android.widget.RadioButton;
import android.widget.Toast;

public class EditPasswordActivity extends Activity implements OnClickListener {
	public static final int EDIT_PASSWORD=1;
	
	private Button editPasswordButton;

	
	private EditText passwordEdit ;
	private EditText newPasswordEdit;
	private EditText confirmPasswordEdit;
	
//	存储PersonalInfoActivity的handler
	Handler handler=null;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_password);
        
        newPasswordEdit=(EditText)findViewById(R.id.newPasswordEdit);
        passwordEdit=(EditText)findViewById(R.id.passwordEdit);
        confirmPasswordEdit=(EditText)findViewById(R.id.confirmPasswordEdit);
        
        editPasswordButton=(Button)findViewById(R.id.editPasswordButton);
        
        
        editPasswordButton.setOnClickListener(this);
        
        
        Intent intent=getIntent();
        if(intent!=null){
	    	 Bundle bundle=intent.getExtras();
	    	 handler=((PersonalInfoActivity)bundle.get("handler")).getHandler();
	    	 
	     }

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.editPasswordButton:{
			String password=passwordEdit.getText().toString();
			String newPassword=newPasswordEdit.getText().toString();
			String confirmPassword=confirmPasswordEdit.getText().toString();
			if(!newPassword.equals(confirmPassword)){
				Toast.makeText(this, "您输入的新密码与确认密码不相符！",Toast.LENGTH_LONG).show();
				
				break;
			}
			DataBaseHelper database=new DataBaseHelper(this);
			Session session=new Session(this);
			int userId=database.checkUser("userName=? and password=?", new String[]{session.getUserName(),DEScode.encrypt(password)});
			if(userId==-1){
				Toast.makeText(this, "您输入的密码有误！",Toast.LENGTH_LONG).show();
				break;
			}
			else {
				ContentValues values=new ContentValues(1);
				values.put(DataBaseHelper.PASSWORD, DEScode.encrypt(newPassword));
				
				if(database.updateTable(DataBaseHelper.FAMILY_MEMBER_INFO, values,session.getUserId())){
					Toast.makeText(this, "修改个人密码成功！",Toast.LENGTH_LONG).show();
					Message message=Message.obtain(handler, PersonalInfoActivity.EDIT_PASSWORD_SUCCESS);
					message.sendToTarget();
				}
			}
		
			break;
		}
	
		}
	}

}
