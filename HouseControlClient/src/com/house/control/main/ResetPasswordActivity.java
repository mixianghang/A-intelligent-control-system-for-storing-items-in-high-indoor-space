package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DEScode;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends Activity implements OnClickListener {
	public static int RESET_SUCCESS=1;
	public static int RESET_CANCEL=2;
	
	private Button editPasswordButton;
	
	private EditText newPasswordEdit;
	private EditText confirmPasswordEdit;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        
        newPasswordEdit=(EditText)findViewById(R.id.newPasswordEdit);
        confirmPasswordEdit=(EditText)findViewById(R.id.confirmPasswordEdit);
        
        editPasswordButton=(Button)findViewById(R.id.editPasswordButton);

        
        editPasswordButton.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
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
					Intent intent=new Intent();
					setResult(RESET_SUCCESS, intent);
					finish();
				}
		
			break;
		}
		
		}
	}


}
