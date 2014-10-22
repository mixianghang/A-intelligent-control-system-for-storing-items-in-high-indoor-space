package com.house.control.main;

import com.house.control.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     
	     setContentView(R.layout.text_content);
	     TextView text=(TextView)findViewById(R.id.contentText);
	     text.setText(R.string.about);
	 }
}
