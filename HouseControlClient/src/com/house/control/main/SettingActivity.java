package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingActivity extends Activity implements OnClickListener{
//	给caller的结果码
	public static final int CANCEL_SETTING=1;
	public static final int SETTING_SUCCESS=2;
	
//	设置 下拉列表中的内容及其对应内容
	private String[] time={"5天","10天","15天","20天",
	"25天","30天","35天","40天","45天","50天","55天","60天"
	};
	private long baseTime=432000000;//5天时间对应的毫秒数
	
	ArrayAdapter< String> adapter;
	
	private Button settingButton;
	
	private Spinner qualityAlarmSpinner;
	private Spinner maxStoreTimeSpinner;
	
	private  int qualityAlarmPosition=0;
	private  int maxStroeTimePosition=0;
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.setting,null);  
        setContentView(contentView);
        
        
        qualityAlarmSpinner=(Spinner)findViewById(R.id.qualityAlarmSpinner);
        maxStoreTimeSpinner=(Spinner)findViewById(R.id.maxStoreTimeSpinner);
        
        adapter= new ArrayAdapter< String> ( this ,android.R .layout .simple_spinner_item ,time) ;

        // 将可选内容与 ArrayAdapter 连接

        adapter.setDropDownViewResource ( android.R .layout .simple_spinner_dropdown_item ) ;

        // 设置下拉列表的风格

        qualityAlarmSpinner.setAdapter ( adapter) ;
        maxStoreTimeSpinner.setAdapter(adapter);
        Session session=new Session(this);
        int userId=session.getUserId();
         DataBaseHelper database=new DataBaseHelper(this);
         Long storeTime=Long.parseLong(database.getUserInfo1(userId, "maxTimeOfStore"))/(24*3600*1000);
         Long qualityTime=Long.parseLong(database.getUserInfo1(userId, "timeAlarmOfQuality"))/(24*3600*1000);
        Log.e("qualityTime",""+qualityTime);
         for(int i=0;i<time.length;i++){
        	if(time[i].equals(""+storeTime+"天")){
        		maxStoreTimeSpinner.setSelection(i,true);
        	}
        	if(time[i].equals(""+qualityTime+"天")){
        		qualityAlarmSpinner.setSelection(i,true);
        	}
        }
        settingButton=(Button)findViewById(R.id.settingButton);
        
        settingButton.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.settingButton:{
			DataBaseHelper database=new DataBaseHelper(this);
			Session session=new Session(this);
			ContentValues values=new ContentValues(2);
			long qualityTime=(qualityAlarmSpinner.getSelectedItemPosition()+1)*this.baseTime;
			long storeTime=(maxStoreTimeSpinner.getSelectedItemPosition()+1)*this.baseTime;
			values.put(DataBaseHelper.TIME_ALARM_OF_QUALITY, ""+qualityTime);
			values.put(DataBaseHelper.MAXTIME_OF_STORE, ""+storeTime);
			if(database.updateTable(DataBaseHelper.FAMILY_MEMBER_INFO, values,session.getUserId())){
				Toast.makeText(this, "设置成功！",Toast.LENGTH_LONG).show();
			}
			else {
				Toast.makeText(this, "设置失败，请重新设置！",Toast.LENGTH_LONG).show();
			}
			break;
		}
		
		}

	}


}
