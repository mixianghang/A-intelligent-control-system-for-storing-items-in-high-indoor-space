package com.house.control.main;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.house.control.R;
import com.house.control.datastore.DataBaseHelper;
import com.house.control.datastore.Session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class SearchGoodsActivity extends Activity implements OnClickListener {
	private EditText searchContentEdit;
	private Spinner goodsTypeSpinner;
	private ListView goodsListView;
	ListAdapter1 adapter;
	
	private Handler handler=null;
	
	
	
//	填入spinner的数据
	String[] goodsType={
			"全部","衣服","食品","饮料","书籍"
	};
	
//	当前物品类型表中的被选择项的位置
	private int goodsTypePosition=0;
	
//	当前物品列表的数据
	String[][] data;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_goods);
        Intent intent=getIntent();
        if(intent!=null){
        	Bundle bundle=intent.getExtras();
        	handler=((HouseControlActivityGroup)(bundle.get("handler"))).getHandler();
        }
        searchContentEdit=(EditText)findViewById(R.id.searchContentEdit);
        goodsTypeSpinner=(Spinner)findViewById(R.id.goodsTypeSpinner);
        goodsListView=(ListView)findViewById(R.id.goodsListView);
        goodsListView.setDividerHeight(10);
//        设置如下为false，才能防止滑动的时候背景变黑
        goodsListView.setScrollingCacheEnabled(false);
        goodsTypeSpinner.setPrompt("选择物品类型"); 
        goodsTypeSpinner.setAdapter(this.getgoodsTypeAdapter());
//        adapter= new ArrayAdapter< String> ( this ,android.R .layout .simple_spinner_item ,goodsType) ;
//
//        // 将可选内容与 ArrayAdapter 连接
//
//        adapter.setDropDownViewResource ( android.R .layout .simple_spinner_dropdown_item ) ;
//
//        // 设置下拉列表的风格
//
//        goodsTypeSpinner.setAdapter ( adapter) ;
        goodsTypeSpinner.setOnItemSelectedListener( new OnItemSelectedListener() {
            public void onItemSelected(
                    AdapterView<?> parent, View view, int position, long id) {
            	if(goodsTypePosition!=position){
            		goodsTypePosition=position;
                	refreshGoodsList();
            	}
            	
            	
            }

            public void onNothingSelected(AdapterView<?> parent) {
               
            }
        });
        
        searchContentEdit.addTextChangedListener(textWatcher);
        
        refreshGoodsList();
        goodsListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(arg2==adapter.getCount()-1)
					return ;
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
        
	}
	
	private TextWatcher textWatcher = new TextWatcher() {  
        
        @Override    
        public void afterTextChanged(Editable s) {     
            // TODO Auto-generated method stub     
            Log.d("TAG","afterTextChanged--------------->");
//            showToast(searchContentEdit.getText().toString());
            refreshGoodsList();
        }   
          
        @Override 
        public void beforeTextChanged(CharSequence s, int start, int count,  
                int after) {  
            // TODO Auto-generated method stub  
            Log.d("TAG","beforeTextChanged--------------->");  
            
        }  
 
         @Override    
        public void onTextChanged(CharSequence s, int start, int before,     
                int count) {     
            Log.d("TAG","onTextChanged--------------->");    
             
                              
        }                    
    };  
	public void showToast(String toast){
		Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
	}
	private void  refreshGoodsList(){
		DataBaseHelper database=new DataBaseHelper(this);
		Session session=new Session(this);
		int userId=session.getUserId();
		String goodsName=searchContentEdit.getText().toString();
		char[] buffer=new char[goodsName.length()];
		if(goodsName.length()!=0)
			goodsName.getChars(0, goodsName.length(), buffer, 0);
//		showToast(new String(buffer)+goodsName.length()+goodsName);
		char[] buffer1=new char[goodsName.length()*2+1];
		buffer1[0]='%';
		for(int i=0;i<goodsName.length();i++){
			buffer1[2*i+1]=buffer[i];
			buffer1[2*i+2]='%';
		}
		String s=new String(buffer1);
		data=database.getData(s,goodsTypePosition,userId);
//		Toast.makeText(this, searchContentEdit.getText().toString()+goodsTypePosition, Toast.LENGTH_LONG).show();
		adapter=new ListAdapter1(this,data);
        goodsListView.setAdapter(adapter);
	}
	
	ArrayAdapter< String>  getgoodsTypeAdapter(){

		DataBaseHelper data=new DataBaseHelper(this);
		List<String> list=data.getgoodsTypeList();
		
		ArrayAdapter< String> adapter = new ArrayAdapter< String>(
				this,
				android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
				adapter.add("全部");
				Iterator<String> it=list.iterator();
				while(it.hasNext()){
					adapter.add(it.next());
				}
		return adapter;
	}

	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub	
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
	        return goodsList[0].length+1;
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
	    	
	    	if(position==getCount()-1){
	    		TextView text=new TextView(context);
	    		
	    		if(getCount()==1){
	    			text.setText("没有条目");
	    		}
	    		else{
	    			text.setText("已到搜索列表末尾！");
	    		}
	    		text.setTextColor(R.color.black);
	    		return text;
	    	}
	      
	    	  LayoutInflater mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	  convertView=mInflater.inflate(R.layout.goods_list, null);
	      
	      
	    	  TextView text=(TextView)convertView.findViewById(R.id.goodsNameText);
	    	  TextView goodsIdText=(TextView)convertView.findViewById(R.id.goodsId);
	    	  TextView goodsStoreTime=(TextView)convertView.findViewById(R.id.goodsStoreTime);
	    	  
	    	  ImageView image=(ImageView)convertView.findViewById(R.id.goodsImageView);
	    	  Bitmap bit=BitmapFactory.decodeFile(goodsList[2][position]);
	    	  text.setText("物品名称:\n"+goodsList[1][position]);
//	    	  SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//	    	  由于日期转化的误差，则还是将goodsId以隐藏字符串进行存储
	    	  goodsIdText.setText(goodsList[0][position]);
	    	  goodsIdText.setVisibility(View.GONE);
	    	  image.setImageBitmap(bit);
	    	  goodsStoreTime.setText("存储日期:\n"+goodsList[3][position]);
//	    	  只要固定住imageview的大小，则图片自动伸缩即可
	    	  image.setScaleType(ImageView.ScaleType.FIT_XY);
	    	  image.setAdjustViewBounds(true);
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
//					Toast.makeText(SearchGoodsActivity.this, "position="+id, Toast.LENGTH_LONG).show();
					 String goodsId=goodsList[0][id];
					 HouseControlActivityGroup activity= (HouseControlActivityGroup)(SearchGoodsActivity.this.getParent());
					  DataBaseHelper database1=new DataBaseHelper(SearchGoodsActivity.this);
		    		  int storeLocation=database1.getStoreLocation(goodsId);
		    		  activity.getGoodsOut(storeLocation, goodsId);
				}
	    		 
	    	 });
	    	 editGoodsInfoButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Toast.makeText(SearchGoodsActivity.this, "position="+id, Toast.LENGTH_LONG).show();
						HouseControlActivityGroup activity= (HouseControlActivityGroup)(SearchGoodsActivity.this.getParent());
			    		 DataBaseHelper database=new DataBaseHelper(SearchGoodsActivity.this);
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
						HouseControlActivityGroup activity= (HouseControlActivityGroup)(SearchGoodsActivity.this.getParent());
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

class Utility { 
    public static void setListViewHeightBasedOnChildren(ListView listView) { 
        ListAdapter listAdapter = listView.getAdapter();  
        if (listAdapter == null) { 
            // pre-condition 
            return; 
        } 
 
        int totalHeight = 0; 
        for (int i = 0; i < listAdapter.getCount(); i++) { 
            View listItem = listAdapter.getView(i, null, listView); 
            listItem.measure(0, 0); 
            totalHeight += listItem.getMeasuredHeight(); 
        } 
 
        ViewGroup.LayoutParams params = listView.getLayoutParams(); 
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)); 
        listView.setLayoutParams(params); 
    } 
}


