package com.house.control.main;

import java.text.SimpleDateFormat;

import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryActivity extends Activity {
    /** Called when the activity is first created. */
    private Gallery gallery =null;
    private int goodsType=0;
    private String[][] goodsList;
    
    public static  final int EDIT_GOODS_INFO=1;
   public static final int DELETE_GOODS_INFO=2;
   public static final int SEARCH_GOODS_INFO=3;
   
   private int currentLongClickItem=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);//注意顺序
        setContentView(R.layout.gallery);
        
        gallery = (Gallery)findViewById(R.id.gallery);
        Intent intent=this.getIntent();
        if(intent!=null){
//        	goodsType=TabTestActivity1.GOODS_TYPE_ID;
        	goodsType=intent.getIntExtra(HomeActivity.GOODS_TYPE, 0);
//        	Toast.makeText(this, "dd"+goodsType, Toast.LENGTH_SHORT).show();
        	DataBaseHelper database=new DataBaseHelper(this);
        	Session session=new Session(this);
        	int userId=session.getUserId();
        	goodsList=database.getGoodsListByType(goodsType, userId);
        	
        }
        //设置图片适配器
        gallery.setAdapter(new ImageAdapter1(this,goodsList));
        gallery.setSpacing(20);
        
        //设置监听器
     
        this.registerForContextMenu(gallery);
        
        gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

     		
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					GalleryActivity.this.currentLongClickItem=arg2;
//					Toast.makeText(GalleryActivity.this, "number"+arg2, Toast.LENGTH_LONG).show();
					
//					Log.e("curentNumvber", ""+arg2);
					return false;
				}
             	
             });
       
       
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    		super.onCreateContextMenu(menu, v, menuInfo);
    		menu.add(0, EDIT_GOODS_INFO, 0, "编辑物品信息");
    		menu.add(0, DELETE_GOODS_INFO, 0,  "取出物品");
    		menu.add(0, SEARCH_GOODS_INFO, 0,  "联网搜索物品信息");
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	Log.e("currentLongItem", ""+this.currentLongClickItem);
//    	gallery.get
    	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	  LinearLayout l=(LinearLayout)gallery.getAdapter().getView(this.currentLongClickItem, null, null);
//    	  LinearLayout l=(LinearLayout)gallery.getItemAtPosition(this.currentLongClickItem);
//    	  Toast.makeText(getApplicationContext(), ""+gallery.getCount(), Toast.LENGTH_LONG).show();
    	  TextView goodsNameText=(TextView)l.findViewById(R.id.galleryText);
    	  TextView goodsIdText=(TextView)l.findViewById(R.id.galleryIdText);
    	  
    	  String goodsId=goodsIdText.getText().toString();
//    	  Log.e("goodsId", goodsId);
    	  String goodsName=goodsNameText.getText().toString();
//    	  Toast.makeText(this, goodsName, Toast.LENGTH_LONG).show();
    	  HouseControlActivityGroup activity= (HouseControlActivityGroup)((this.getParent()).getParent());
    	  switch (item.getItemId()) {
    	  case EDIT_GOODS_INFO:
    		 
    		 DataBaseHelper database=new DataBaseHelper(this);
    		 Bundle bundle=database.getGoodsInfoBundle(goodsId);
    		 Intent intent=new Intent();
    		 intent.putExtras(bundle);
    		 activity.handleActivityResult(EditGoodsActivity.EDIT_GOODS_INFO, Activity.RESULT_OK, intent);
    		 return true;
    	  case DELETE_GOODS_INFO:
    		  DataBaseHelper database1=new DataBaseHelper(this);
    		  int storeLocation=database1.getStoreLocation(goodsId);
    		  activity.getGoodsOut(storeLocation, goodsId);
    	    return true;
    	  case SEARCH_GOODS_INFO:
//    		  此处将物品名称：去掉
    		  activity.startSearchDialog(goodsName.substring(5, goodsName.length()));
    		  return true;
    	  default:
    	    return super.onContextItemSelected(item);

    	  }
    }
}

class ImageAdapter1 extends BaseAdapter{
    private Context context;
    //图片源数组
    private String[][] goodsList;
    public ImageAdapter1(Context c,String[][] goods){
    	super();
    	context=c;
    	goodsList=goods;
    }
    // 获取图片的个数
    public int getCount() {
        return goodsList[0].length;
    }
    // 获取图片在库中的位置  
    public Object getItem(int position) {
        return position;
    }
    // 获取图片ID  
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
    	
      if(convertView==null){
    	  LayoutInflater mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	  convertView=mInflater.inflate(R.layout.gallery_item, null);
      }
      
    	  TextView text=(TextView)convertView.findViewById(R.id.galleryText);
    	  TextView goodsIdText=(TextView)convertView.findViewById(R.id.galleryIdText);
    	  TextView goodsStoreTime=(TextView)convertView.findViewById(R.id.galleryTimeText);
    	  
    	  ImageView image=(ImageView)convertView.findViewById(R.id.galleryImage);
    	  image.setAdjustViewBounds(false);
    	  Bitmap bit=BitmapFactory.decodeFile(goodsList[2][position]);
    	  if(goodsList[1][position].length()<=12)
    	  text.setText("物品名称:"+goodsList[1][position]);
    	  else
        	  text.setText("物品名称:"+goodsList[1][position].substring(0, 10)+"...");
//    	  SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//    	  由于日期转化的误差，则还是将goodsId以隐藏字符串进行存储
    	  goodsIdText.setText(goodsList[0][position]);
//    	  goodsIdText.setVisibility(View.GONE);
    	  image.setImageBitmap(bit);
    	  goodsStoreTime.setText("存储日期:"+goodsList[3][position]);
//    	  只要固定住imageview的大小，则图片自动伸缩即可
    	  image.setScaleType(ImageView.ScaleType.FIT_XY);
    	  
//    	  image.setLayoutParams(new LayoutParams(120,120));
    	     /* 设置这个ImageView对象的宽度，单位为dip*/
    	  
//    	     convertView.setLayoutParams(new Gallery.LayoutParams(220,420));
    	 return convertView;
      
    }
}