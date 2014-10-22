package com.house.control.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import java.util.List;
import java.util.Map;


import org.json.simple.JSONValue;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.common.BitMatrix;
import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class EditGoodsActivity extends Activity implements OnItemSelectedListener, OnClickListener {
	private Spinner goodsTypeSpinner;
	private Spinner storeLocationSpinner;
	private Spinner yearOfProductTime;
	private Spinner monthOfProductTime;
	private Spinner dayOfProductTime;
	private Spinner qualityTimeSpinner;

	ArrayAdapter< String> daysAdapter1;//31天
	ArrayAdapter< String> daysAdapter2;//30天
	ArrayAdapter< String> daysAdapter3;//29天
	ArrayAdapter< String> daysAdapter4;//28天
	
	public static final int TAKE_PHOTO_REQUEST_CODE=3334;
	public static final int 	EDIT_GOODS_INFO=3335;
	  private static final int WHITE = 0xFFFFFFFF;
	  private static final int BLACK = 0xFF000000;

	public static final String IMG_STORE_LOCATION="/sdcard/HouseControl/";
	
//设立pregoodsId监测该物品是否是以前存储过的物品
	private static String preGoodsId=null;
	
	
//	json格式
	public static final String JSON_GOODS_NAME="goodsname";
	public static final String JSON_GOODS_Id="goodsId";
	public static final String JSON_IMG_PATH="imgpath";
	public static final String JSON_YEAR="year";
	public static final String JSON_MONTH="month";
	public static final String JSON_DAY="day";
	public static final String 	JSON_QUALITY="qualityTime";
	public static final String JSON_TYPE="goodstype";
	public static final String JSON_LOCATION="goodsLocationId";
//	创建菜单栏
	public  static final int SAVE_INFO=1234;
	public static final int SEARCH_NEW_GOODS=2345;

	private static final int CANCEL=3456;
	
	private ImageView goodsImageView;
	private EditText goodsNameEdit;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this.getParent()).inflate(R.layout.add_goods,null);  
        setContentView(contentView);
        goodsTypeSpinner=(Spinner)findViewById(R.id.goodsTypeSpinner);
       
       goodsTypeSpinner.setPrompt("选择物品类型"); 
       goodsTypeSpinner.setAdapter(this.getgoodsTypeAdapter());
       goodsTypeSpinner.setOnItemSelectedListener(this);
    	storeLocationSpinner=(Spinner)findViewById(R.id.storeLocationSpinner);
    	ArrayAdapter< String> adapter2=this.getContainerAdapter();
    	if(adapter2==null)
    		storeLocationSpinner.setPrompt("没有可用的储物柜");
    	else{
    		storeLocationSpinner.setPrompt("可用的储物柜");
    		storeLocationSpinner.setAdapter(adapter2);
    	}
    	yearOfProductTime=(Spinner)findViewById(R.id.yearOfProductTime);
    	yearOfProductTime.setPrompt("年份");
    	yearOfProductTime.setAdapter(this.createYearAdapter());
    	yearOfProductTime.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				refreshDayAdapter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	monthOfProductTime=(Spinner)findViewById(R.id.monthOfProductTime);
    	ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
                this, R.array.month, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthOfProductTime.setPrompt("月份");
        monthOfProductTime.setAdapter(adapter1);
        monthOfProductTime.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				refreshDayAdapter();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	dayOfProductTime=(Spinner)findViewById(R.id.dayOfProductTime);
    	dayOfProductTime.setPrompt("天数");
//    	dayOfProductTime.setOnItemSelectedListener(new OnItemSelectedListener(){
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// TODO Auto-generated method stub
//				refreshDayAdapter();
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//    		
//    	});
//    	dayOfProductTime.seto
//    	dayOfProductTime.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				refreshDayAdapter();
//			}
//    		
//    	});
    	daysAdapter1=this.getDaysAdapter(31);
    	daysAdapter2=this.getDaysAdapter(30);
    	daysAdapter3=this.getDaysAdapter(29);//29天
    	daysAdapter4=this.getDaysAdapter(28);
    	
    	refreshDayAdapter();
    	qualityTimeSpinner=(Spinner)findViewById(R.id.qualityTimeSpinner);
    	qualityTimeSpinner.setPrompt("保质天数");
    	qualityTimeSpinner.setAdapter(this.getQualityAdapter());
    	goodsImageView=(ImageView)findViewById(R.id.goodsImageView);
    	goodsImageView.setDrawingCacheEnabled(true);
    	goodsNameEdit=(EditText)findViewById(R.id.goodsNameEdit);
    	goodsImageView.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		this.takePhoto(TAKE_PHOTO_REQUEST_CODE);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case EditGoodsActivity.TAKE_PHOTO_REQUEST_CODE:{
			switch(resultCode){
			case Activity.RESULT_OK:{
				Bitmap bit=(Bitmap)data.getParcelableExtra("data");
				if(bit!=null){
					goodsImageView.setImageBitmap(bit);
				}
				break;
			}
			case Activity.RESULT_CANCELED:{
//此处用于更新提醒的数目
				break;
			}

		}
			break;
	
		
		}
		case EDIT_GOODS_INFO:{
//			此处应该使用getExtras 而不是getBundleExtra
			Bundle bundle=data.getExtras();
			preGoodsId=(String) bundle.get(JSON_GOODS_Id);
			goodsNameEdit.setText((String) bundle.get(JSON_GOODS_NAME));	
			String year=(String) bundle.get(JSON_YEAR);
			this.setItemPosition(year, yearOfProductTime);
			String month=(String) bundle.get(JSON_MONTH);
			this.setItemPosition(month, monthOfProductTime);
			String day=(String) bundle.get(JSON_DAY);
			this.setItemPosition(day, dayOfProductTime);
			
			int  goodsType=(Integer)bundle.get(JSON_TYPE)-1; 
			goodsTypeSpinner.setSelection(goodsType,true);
			
			String location=(String)bundle.get(JSON_LOCATION);
			setItemPosition(location, this.storeLocationSpinner);
			
			String qualityTime=(String) bundle.get(JSON_QUALITY);
			this.setItemPosition(qualityTime, qualityTimeSpinner);
			String imgPath=(String) bundle.get(JSON_IMG_PATH);
			Bitmap bit=BitmapFactory.decodeFile(imgPath);
			goodsImageView.setImageBitmap(bit);
			break;
		}
		}
	}
	
	public void handleActivityResult(int requestCode, int resultCode, Intent data){
		this.onActivityResult(requestCode, resultCode, data);
	}
	void takePhoto(int requestCode){
		HouseControlActivityGroup activity=(HouseControlActivityGroup)(this.getParent().getParent());
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  activity.startActivityForResult1(intent, requestCode);
	  }
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	ArrayAdapter< String> createYearAdapter(){
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				Date date=new Date();
				int year=date.getYear()+1900;
				for(int i=year;i>=year-20;i--){
					adapter.add(""+i);
				}
		return adapter;
	}
//	更新每月的天数
	void refreshDayAdapter(){
		int position=dayOfProductTime.getSelectedItemPosition();
//		Log.e("selectedItem", ""+position);
		if(dayOfProductTime.getAdapter()!=null){
			position=dayOfProductTime.getSelectedItemPosition();
		}
		int year=Integer.parseInt(yearOfProductTime.getSelectedItem().toString());
    	int month=Integer.parseInt(monthOfProductTime.getSelectedItem().toString());
		
				if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
					dayOfProductTime.setAdapter(daysAdapter1);
				}
				else if(month==2){
					if(year%400==0||year%4==0&&year%100!=0)
						dayOfProductTime.setAdapter(daysAdapter3);
					else
						dayOfProductTime.setAdapter(daysAdapter4);
				}
				else
					dayOfProductTime.setAdapter(daysAdapter2);
//				Log.e("item position",""+position);
//				dayOfProductTime.setSelection(position+1);
				dayOfProductTime.setSelection(position,true);
//				dayOfProductTime.
				
				return ;
				
	}
	
	ArrayAdapter< String>  getQualityAdapter(){
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				int days=48;
				for(int i=1;i<=days;i++){
					adapter.add(""+i*15);
				}
		return adapter;
	}
	ArrayAdapter< String>  getContainerAdapter(){
		Session session=new Session(this);
		int userId=session.getUserId();
		DataBaseHelper data=new DataBaseHelper(this);
		List<Integer> list=data.getContainers(userId);
		if(list.isEmpty()){
			Toast.makeText(getApplication(), "没有可用的储物柜！", Toast.LENGTH_LONG).show();
			finish();
			return null;
		}
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				Iterator<Integer> it=list.iterator();
				while(it.hasNext()){
					adapter.add(""+it.next());
				}
		return adapter;
	}
	ArrayAdapter< String>  getgoodsTypeAdapter(){

		DataBaseHelper data=new DataBaseHelper(this);
		List<String> list=data.getgoodsTypeList();
		
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				Iterator<String> it=list.iterator();
				while(it.hasNext()){
					adapter.add(it.next());
				}
		return adapter;
	}
	
//	获得天数的adapter
	
	ArrayAdapter< String>  getDaysAdapter(int days){
		
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				for(int i=1;i<=days;i++){
					adapter.add(""+i);
					}
		return adapter;
	}
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
		    super.onCreateOptionsMenu(menu);
		    menu.add(Menu.NONE, SAVE_INFO, Menu.NONE, R.string.save_goods_infor)
		        .setIcon(android.R.drawable.ic_menu_share);
		   
		    menu.add(Menu.NONE, SEARCH_NEW_GOODS, Menu.NONE, R.string.find_new_goods)
		        .setIcon(android.R.drawable.ic_menu_preferences);
		    
		    
		    return true;
		  }

		  // Don't display the share menu item if the result overlay is showing.
		//  在扫描结果出来的时候，不要显示分享菜单
		  @Override
		  public boolean onPrepareOptionsMenu(Menu menu) {
		    super.onPrepareOptionsMenu(menu);
		    
		    return true;
		  }

		  @Override
		  public boolean onOptionsItemSelected(MenuItem item) {
		    Intent intent = new Intent(Intent.ACTION_VIEW);
//		    intent.addFlags(Intent.);
		    switch (item.getItemId()) {
		      case SEARCH_NEW_GOODS:
//		    	  TextView text=(TextView)findViewById(R.id.contents_text_view);
////		    	  Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
//		        intent.setClassName(this, CaptureActivity.class.getName());
//		        startActivityForResult(intent,BARCODE_SCAN);
		        break;
//		        此处打印条形码
		      case SAVE_INFO:
	   
////		    	  检查并保存物品信息，并与机械处通信，控制物品存放
		    	  if(this.checkGoodsInfo()){
		    		  if(preGoodsId==null){
		    	  		Toast.makeText(this, "请先选择要编辑的物品！", Toast.LENGTH_LONG).show();
		    	  		return false;
		    		  }
		    	  		
		    	  		
		    		  String goodsName=goodsNameEdit.getText().toString();
		    		  String goodsType=goodsTypeSpinner.getSelectedItem().toString();
		    		  int goodsTypeId=this.getGoodsTypeId(goodsType, goodsTypeSpinner);
		    		  int storeLocation=Integer.parseInt(this.storeLocationSpinner.getSelectedItem().toString());
//		    		  int containerId=Integer.parseInt(this.containerSpinner.getSelectedItem().toString());
		    		  String year=this.yearOfProductTime.getSelectedItem().toString();
		    		  String month=this.monthOfProductTime.getSelectedItem().toString();
		    		  String day=this.dayOfProductTime.getSelectedItem().toString();
		    		  String qualityTime=this.qualityTimeSpinner.getSelectedItem().toString();
		    		  
		    		  
		    		  String productTime=this.stringToDate(year, month, day);
		    		  String qualityTimeInMscecond=this.qualityTimeChange(qualityTime);
		    		  String imgPath;
		    	
		    		  imgPath=this.storeImgToSdcard(preGoodsId);
		    		  
		    		  Map<String, Object> obj=new LinkedHashMap();
		    		  obj.put(JSON_GOODS_NAME, goodsName);
		    		  obj.put(JSON_YEAR,year);
		    		  obj.put(JSON_MONTH, month);
		    		  obj.put(JSON_DAY, day);
		    		  obj.put(JSON_GOODS_Id,preGoodsId);
		    		  obj.put(JSON_IMG_PATH, imgPath);
		    		  obj.put(JSON_TYPE, goodsType);
		    		  obj.put(JSON_QUALITY, qualityTime);
		    		  String jsonString=this.createJsonString(obj);
		    		  try {
						Bitmap bit=this.encodeAsBitmap(jsonString, BarcodeFormat.QR_CODE);
						
						this.storeBarCodeToSdcard(preGoodsId, bit);
						
					} catch (WriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
////		    		  goodsInfo(goodsId varchar(128) primary key," +
////		    					"goodsName varchar(128),goodsImagepath varchar(128),storeTime varchar(128),produceTime varchar(128),qualityTime varchar(128)," +
////		    					"goodsTypeId int," +"containerId int,userId int)
		    		  Session session=new Session(this);
		    		  int userId=session.getUserId();
		    		 ContentValues values=new ContentValues(9); 
		    		 if(preGoodsId==null)
		    			values.put("goodsId", preGoodsId); 
		    		 values.put("goodsName", goodsName);
		    		 values.put("goodsImagePath", imgPath);
		    		 values.put("storeTime", ""+new Date().getTime());
		    		 values.put("produceTime", productTime);
		    		 values.put("qualityTime", qualityTimeInMscecond);
		    		 values.put("goodsTypeId", goodsTypeId);
		    		 values.put("containerId", storeLocation);
		    		 values.put("userId", userId);
//		    		 如果此物品并非之前装入过的物品，则插入新的条目
		    		 DataBaseHelper base=new DataBaseHelper(this);
		    		 if(preGoodsId!=null){
		    			 if(base.updateGoodsTable("goodsInfo", values, preGoodsId)){
		    				 Toast.makeText(this, "存储物品信息成功！", Toast.LENGTH_LONG).show();
		    			 }
		    			 else{
		    				 Toast.makeText(this, "存储失败！", Toast.LENGTH_LONG).show();
		    			 }
		    		 }
		    		 
		    		 
		    	  }
		    	  else{
		    		  Toast.makeText(this, "您提供的物品信息不完整！", Toast.LENGTH_LONG).show();
		    	  }
		    	  break;
		      default:
		        return super.onOptionsItemSelected(item);
		    }
		    return true;
		  }
//		  检查输入的物品信息是否足够提交
		  private boolean checkGoodsInfo(){
			  String goodsName=goodsNameEdit.getText().toString();
			  if(goodsName==null||goodsName.isEmpty())
				  return false;
			  else{
//				  此处可以检测其他条件，如生产日期不能超过现在的日期等
			  }
			  return true;
		  }
		  
//		  根据年月日字符串生成对应日期相对于1970.1.1的毫秒数的字符串，用于作为goodsId
		  private String stringToDate(String year,String month,String day){
			  SimpleDateFormat bartDateFormat = new SimpleDateFormat("MM-dd-yyyy");
			// Create a string containing a text date to be parsed. 
			  String dateStringToParse = month+"-"+day+"-"+year;
			  Date date;
			 try { 
				 date = bartDateFormat.parse(dateStringToParse);
			 }
			 catch (Exception ex) { 
				 return null;
			 } 
			 return ""+date.getTime();
		  }
//		  保质期由天数字符串转为毫秒字符串
		  private String qualityTimeChange(String qualityTime){
			  long time=Integer.parseInt(qualityTime);
			  long mSecond=time*24*60*60*1000;
			  return ""+mSecond;
		  }
		  
//		  存储照片
		  private String storeImgToSdcard(String goodsId){
			  File dir1=new File(IMG_STORE_LOCATION);
			  if(!dir1.exists()&&!dir1.isDirectory()){
		        	dir1.mkdir();
		        }
			  File dir=new File(IMG_STORE_LOCATION+goodsId);
		      if(!dir.exists()&&!dir.isDirectory()){
		        	dir.mkdir();
		        }
			 Bitmap bit= goodsImageView.getDrawingCache();
		
		      File barcodeFile = new File(IMG_STORE_LOCATION+goodsId+"/", "image.png");
		      barcodeFile.delete();
		      FileOutputStream fos = null;
		      try {
		        fos = new FileOutputStream(barcodeFile);
		        bit.compress(Bitmap.CompressFormat.PNG, 0, fos);
		        fos.flush();
		        fos.close();
		      } catch (FileNotFoundException fnfe) {
		        
		        return null;
		      } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      return IMG_STORE_LOCATION+goodsId+"/image.png";
		  }
//		  存储二维码
		  private boolean storeBarCodeToSdcard(String goodsId,Bitmap bit){
			  File dir1=new File(IMG_STORE_LOCATION);
			  if(!dir1.exists()&&!dir1.isDirectory()){
		        	dir1.mkdir();
		        }    
			  File dir=new File(IMG_STORE_LOCATION+goodsId);
			      if(!dir.exists()&&!dir.isDirectory()){
			        	dir.mkdir();
			        }
			        
			      File barcodeFile = new File(IMG_STORE_LOCATION+goodsId+"/", "barcode.png");
			      barcodeFile.delete();
			      FileOutputStream fos = null;
			      try {
			        fos = new FileOutputStream(barcodeFile);
			        bit.compress(Bitmap.CompressFormat.PNG, 0, fos);
			        fos.flush();
			        fos.close();
			      } catch (FileNotFoundException fnfe) {
			        
			        return false;
			      } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			      return true;
			  }
		  
		  
//		  生成jason字符串，根据输入的hashmap
		  private String createJsonString(Map<String, Object> obj){
			  StringWriter out = new StringWriter();
			   try {
				JSONValue.writeJSONString(obj, out);
				return out.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			  
		  }
		  
//		  生成二维码图片
		  private  Bitmap encodeAsBitmap(String contents, BarcodeFormat format) throws WriterException {
			  WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
			    Display display = manager.getDefaultDisplay();
			    int width1 = display.getWidth();
			    int height1 = display.getHeight();
			    int smallersmallerDimension = width1 < height1 ? width1 : height1;
			    smallersmallerDimension = smallersmallerDimension * 7 / 8;
			  String contentsToEncode = contents;
			    if (contentsToEncode == null) {
			      return null;
			    }
			    Map<EncodeHintType,Object> hints = null;
			    String encoding = guessAppropriateEncoding(contentsToEncode);
			    if (encoding != null) {
			      hints = new EnumMap<EncodeHintType,Object>(EncodeHintType.class);
			      hints.put(EncodeHintType.CHARACTER_SET, encoding);
			    }
			    MultiFormatWriter writer = new MultiFormatWriter();
			    BitMatrix result = writer.encode(contentsToEncode, format, smallersmallerDimension, smallersmallerDimension, hints);
			    int width = result.getWidth();
			    int height = result.getHeight();
			    int[] pixels = new int[(width+2) * (height+2)];
			    for (int y = 1; y <=height; y++) {
			      int offset = y * (width+2);
			      for (int x = 1; x <= width; x++) {
			        pixels[offset + x] = result.get(x-1, y-1) ? BLACK : WHITE;
			      }
			    }
			//    
			    for(int y=0;y<height+2;y++){
			    	pixels[y*(width+2)]=WHITE;
			    	pixels[(y+1)*(width+2)-1]=WHITE;
			    }
			    for(int x=0;x<width+2;x++){
			    	pixels[x]=WHITE;
			    	pixels[(x+(width+2)*(height+1))]=WHITE;
			    }

			    Bitmap bitmap = Bitmap.createBitmap(width+2, height+2, Bitmap.Config.ARGB_8888);
			    bitmap.setPixels(pixels, 0, width+2, 0, 0, width+2, height+2);
			    return bitmap;
			  }

			  private static String guessAppropriateEncoding(CharSequence contents) {
			    // Very crude at the moment
			    for (int i = 0; i < contents.length(); i++) {
			      if (contents.charAt(i) > 0xFF) {
			        return "UTF-8";
			      }
			    }
			    return null;
			  }
			  
//			  获得物品种类名称对应的Id
			  private int getGoodsTypeId(String goodsType,Spinner sp){
				  int number=sp.getCount();
//				  Toast.makeText(this,goodsType+number, Toast.LENGTH_LONG).show();
				  for(int i=0;i<number;i++){
					  String item=(sp.getItemAtPosition(i)).toString();
					  
					  if(item.equals(goodsType))
						  return i+1;
				  }
				  return 0;
			  }
		  
		  
//			  获取条目在spinner中的位置
			  private void setItemPosition(String item,Spinner sp){
				  int num=sp.getCount();
				  Log.e(item, ""+num);
				  for(int i=0;i<num;i++){
					  if(sp.getItemAtPosition(i).toString().equals(item)){
						 
						  sp.setSelection(i,true);
						  return ;
					  }
				  }
				  return ;
			  }
}
