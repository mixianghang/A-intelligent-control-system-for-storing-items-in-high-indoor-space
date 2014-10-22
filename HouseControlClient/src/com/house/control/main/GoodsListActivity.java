package com.house.control.main;

import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class GoodsListActivity extends ListActivity {
	private int listType=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();
		if(intent!=null){
			Bundle bundle=intent.getExtras();
			listType=bundle.getInt("listType");
		}
		ListAdapter1 adapter;
		DataBaseHelper database=new DataBaseHelper(this);
		Session session=new Session(this);
		int userId=session.getUserId();
		String[][] result=null;
		if(listType==0){
			result=database.getStoreTimeAlarmGoodsInfo(userId);
		}
		else{
			result=database.getQualityTimeAlarmGoodsInfo(userId);
		}
		adapter=new ListAdapter1(this,result);
		this.setListAdapter(adapter);
		ListView list=this.getListView();
		list.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				LinearLayout l=(LinearLayout)arg1.findViewById(R.id.goodsItemButtons);
				if(l.getVisibility()==View.INVISIBLE||l.getVisibility()==View.GONE){
					l.setVisibility(View.VISIBLE);	
//					Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
				}
				else{
					l.setVisibility(View.GONE);
//					Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
				}
//				Toast.makeText(SearchGoodsActivity.this, "goodsLiust", Toast.LENGTH_LONG).show();
			}
        	
        });
		list.setFadingEdgeLength(0);
		list.setScrollingCacheEnabled(false);
		list.setDividerHeight(10);
		
	}
	
	class ListAdapter1 extends BaseAdapter{
	    private Context context;
	    //图片源数组
	    private String[][] goodsList;
	 
	    public ListAdapter1(Context c,String[][] goods){
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
	    	  convertView=mInflater.inflate(R.layout.goods_list, null);
	      }
	      
	    	  TextView text=(TextView)convertView.findViewById(R.id.goodsNameText);
	    	  TextView goodsIdText=(TextView)convertView.findViewById(R.id.goodsId);
	    	  TextView goodsStoreTime=(TextView)convertView.findViewById(R.id.goodsStoreTime);
	    	  
	    	  ImageView image=(ImageView)convertView.findViewById(R.id.goodsImageView);
	    	  Bitmap bit=BitmapFactory.decodeFile(goodsList[2][position]);
	    	  text.setText("物品名称:\n"+goodsList[1][position]);
//	    	  SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//	    	  由于日期转化的误差，则还是将goodsId以隐藏字符串进行存储
	    	  goodsIdText.setText(goodsList[0][position]);
	    	  goodsIdText.setVisibility(View.INVISIBLE);
	    	  image.setImageBitmap(bit);
	    	  goodsStoreTime.setText(goodsList[3][position]);
//	    	  只要固定住imageview的大小，则图片自动伸缩即可
	    	  image.setScaleType(ImageView.ScaleType.FIT_XY);
	    	  
//	    	  image.setLayoutParams(new LayoutParams(120,120));
	    	     /* 设置这个ImageView对象的宽度，单位为dip*/
	    	  
//	    	 convertView.setLayoutParams(new Gallery.LayoutParams(220,420));
	    	  final int id=position;
	    	  Button getGoodsOutButton=(Button)convertView.findViewById(R.id.getGoodOutButton);
				Button editGoodsInfoButton=(Button)convertView.findViewById(R.id.editGoodsInfoButton);
				Button searchGoodsInfoButton=(Button)convertView.findViewById(R.id.searchGoodsInfoButton);
				getGoodsOutButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					Toast.makeText(GoodsListActivity.this, "position="+id, Toast.LENGTH_LONG).show();
					 String goodsId=goodsList[0][id];
					 HouseControlActivityGroup activity= (HouseControlActivityGroup)(((AlarmActivity)((GoodsListActivity.this.getParent()))).getParent());
					  DataBaseHelper database1=new DataBaseHelper(GoodsListActivity.this);
		    		  int storeLocation=database1.getStoreLocation(goodsId);
		    		  activity.getGoodsOut(storeLocation, goodsId);
				}
	    		 
	    	 });
	    	 editGoodsInfoButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Toast.makeText(GoodsListActivity.this, "position="+id, Toast.LENGTH_LONG).show();
						HouseControlActivityGroup activity= (HouseControlActivityGroup)(((AlarmActivity)((GoodsListActivity.this.getParent()))).getParent());
			    		 
						DataBaseHelper database=new DataBaseHelper(GoodsListActivity.this);
			    		 String goodsId=goodsList[0][id];
			    		 Bundle bundle=database.getGoodsInfoBundle(goodsId);
			    		 Intent intent=new Intent();
			    		 intent.putExtras(bundle);
			    		 activity.handleActivityResult(EditGoodsActivity.EDIT_GOODS_INFO, Activity.RESULT_OK, intent);
					}
		    		 
		    	 });
	    	 searchGoodsInfoButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						HouseControlActivityGroup activity= (HouseControlActivityGroup)(((AlarmActivity)((GoodsListActivity.this.getParent()))).getParent());
						activity.startSearchDialog(goodsList[1][id]);
						
					}
		    		 
		    	 });
//	    	 if(this.getCount()==1){
//	    		 LinearLayout l=(LinearLayout)convertView.findViewById(R.id.goodsItemButtons);
//	    		 l.setVisibility(View.VISIBLE);
//	    	 }
				return convertView;
	      
	    }
	}
}
