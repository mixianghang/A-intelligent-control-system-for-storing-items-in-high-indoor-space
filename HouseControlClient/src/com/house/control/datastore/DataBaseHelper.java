package com.house.control.datastore;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.house.control.R;
import com.house.control.main.EditGoodsActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Bundle;
import android.util.Log;
//此类用于进行数据库的一系列操作；
public class DataBaseHelper extends SQLiteOpenHelper {
	private SQLiteDatabase sqlDatabase=null;
    private static final String DATABASE_NAME = "houseControl.db";
    private static final int DATABASE_VERSION = 2;
    private static final int INITIAL_CAPACITY=100;
//    创建 用户信息表的语句
	public static final String FAMILY_MEMBER_INFO_COMMAND=
			"create table familyMemberInfo(userId int  primary key ,userName varchar(128) unique," +
			"password varchar(128),securityQuestionId int," +
			"securityAnswer varchar(128),maxTimeOfStore varchar(128),timeAlarmOfQuality varchar(128))";
	public static final String FAMILY_MEMBER_INFO="familyMemberInfo";//用户信息表名称
	//	以下为用户信息表中各列名称
	public static final String USER_ID="userId";
	public static final String USERNAME="userName";
	public static final String PASSWORD="password";
	public static final String SECURITY_ANSWER="securityAnswer";
	public static final String 	SECURITY_QUESTION_ID="securityQuestionId";
	public static final String MAXTIME_OF_STORE="maxTimeOfStore";
	public static final String TIME_ALARM_OF_QUALITY="timeAlarmOfQuality";
	
//	此为创建安全问题表的语句
	public static final String SECURITY_QUESTION_COMMAND="create table securityQuestion" +
			"(securityQuestionId int primary key,securityQuestionContent varchar(128));";
	public static final String SECURITY_QUESTION="securityQuestion";//问题信息表名称
//	此为安全信息表中各列的名称（securityQuestionId已经定义）
	public static final String SECURITY_QUESTION_CONTENT="securityQuestionContent";
	
//	此为物品信息表创建语句
	public static final String GOODS_INFO_COMMAND="create table goodsInfo(goodsId varchar(128) primary key," +
			"goodsName varchar(128),goodsImagepath varchar(128),storeTime varchar(128),produceTime varchar(128),qualityTime varchar(128)," +
			"goodsTypeId int," +"containerId int,userId int);";
	public static final String GOODS_INFO="goodsInfo";//物品信息表名称
//	此为物品信息表中各列名称
	public static final String GOODS_ID="goodsId";
	public static final String GOODS_NAME="goodsName";
	public static final String GOOS_IMAGE="goodsImage";
	public static final String STORE_TIME="storeTime";
	public static final String PRODUCE_TIME="produceTime";
	public static final String QUALITY_TIME="qualityTime";
	public static final String GOODS_TYPE_ID="goodsTypeId";
	public static final String CONTAINER_ID="containerId";
	
//	此为创建物品类型表的语句
	public static final String GOODS_TYPE_COMMAND="create table goodsType(typeId int primary key,typeName varchar(128));";
	public static final String GOODS_TYPE="goodsType";//物品类型表名称
//	此为物品类型表中各列名称	
	public static final String TYPE_NAME="typeName";
	
//	此为箱子信息表创建语句
	public static final String CONTAINER_COMMAND="create table container(containerId int primary key,containerStatus int,userID int);";
	public static final String CONTAINER="container";//箱子列表名称
//	此为箱子信息表各列名称
	public static final String CONTAINER_STATUS="containerStatus";
	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		sqlDatabase=arg0;
		sqlDatabase.execSQL(FAMILY_MEMBER_INFO_COMMAND);//创建用户信息表\
//		向用户信息表中插入公共用户，其编号为1，名字为'public',最长存储时间为30天，保质期提前十天预警
		sqlDatabase.execSQL("insert into familyMemberInfo(userId ,userName," +"maxTimeOfStore,timeAlarmOfQuality ) values(1,'public','2592000000','864000')");
		sqlDatabase.execSQL(GOODS_INFO_COMMAND);//创建物品信息表
//		sqlDatabase.execSQL("insert into goodsinfo(goodsId,goodsName, ");
		sqlDatabase.execSQL(CONTAINER_COMMAND);//创建箱子信息表
//		插入八个箱子条目
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(1,0)");
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(2,0)");
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(3,0)");
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(4,0)");
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(5,0)");
		sqlDatabase.execSQL("insert into container(containerId,containerStatus) values(6,0)");
		sqlDatabase.execSQL(SECURITY_QUESTION_COMMAND);//创建安全问题表
//		插入5个安全问题、
		sqlDatabase.execSQL("insert into securityQuestion values(1,'你最喜欢的水果')");
		sqlDatabase.execSQL("insert into securityQuestion values(2,'你母亲的名字')");
		sqlDatabase.execSQL("insert into securityQuestion values(3,'你所在的省份')");
		sqlDatabase.execSQL("insert into securityQuestion values(4,'你的幸运数字')");
		sqlDatabase.execSQL("insert into securityQuestion values(5,'你最喜欢的一位老师的名字')");
		
		sqlDatabase.execSQL(GOODS_TYPE_COMMAND);//创建物品类型表
//		插入四种类型
		sqlDatabase.execSQL("insert into goodsType values(1,'食品')");
		sqlDatabase.execSQL("insert into goodsType values(2,'衣服')");
		sqlDatabase.execSQL("insert into goodsType values(3,'书籍')");
		sqlDatabase.execSQL("insert into goodsType values(4,'其他')");
	}
	
	
	
//	检查用户是否存在
	public int checkUser(String where,String[] whereArgs){
		SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(FAMILY_MEMBER_INFO, null, where, whereArgs, null, null, null);  
		if(c.getCount()==1){
			c.moveToFirst();
			int userId=c.getInt(c.getColumnIndex(USER_ID));
			db.close();
			return userId;
		}
		db.close();
        return -1;
	}
	
//	获得用户ID
	public int getUserId(String userName){
		SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(FAMILY_MEMBER_INFO, null, "userName=?", new String[]{userName}, null, null, null);
        c.moveToFirst();
        int userId=(int) c.getLong(c.getColumnIndex(USER_ID));
        db.close();
        return userId;
	}
	
//	获得用户名称
	public String getUserName(int userId){
		SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(FAMILY_MEMBER_INFO, null, "userId="+userId, null, null, null, null);
        c.moveToFirst();
        String userName=c.getString(c.getColumnIndex(USERNAME));
        db.close();
        return userName;
		
		
	}
	
	
	
	
//	向表中插入行
	public boolean insertIntoTable(String tableName,ContentValues values){
		SQLiteDatabase db = getWritableDatabase();  
        if(db.insert(tableName, null, values)!=-1){
        	db.close();
        	return true;
        } 
        db.close();  
		return false;
	}
	
//更新用户表内数据
	public boolean updateTable(String table, ContentValues values, int userId){
		SQLiteDatabase db = getWritableDatabase();  
		if(db.update(table, values, "userId="+userId, null)==1){
			db.close();
			return true;
		}
		db.close();
		return false;
	}
//	更新物品列表
	public boolean updateGoodsTable(String table, ContentValues values, String goodsId){
		SQLiteDatabase db = getWritableDatabase();  
		if(db.update(table, values, "goodsId=?", new String[]{goodsId})==1){
			db.close();
			return true;
		}
		db.close();
		return false;
	}

//	检查用户名是否存在，防止重复的用户名
	public boolean checkUserNameExist(String userName){
		SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(FAMILY_MEMBER_INFO, null, "userName=?", new String[]{userName}, null, null, null);  
		if(c.getCount()==1){
			db.close();
			return false;
		}
		db.close();
        return true;
	}
	
//修改密码
	public boolean alterPassword(String userName,ContentValues values){
		SQLiteDatabase db = getWritableDatabase();  
		if(db.update(FAMILY_MEMBER_INFO, values, "userName=?", new String[]{userName})==1){
			db.close();
			return true;
		}
		db.close();
		return false;
	}
	
//	保质期提醒 没有关闭db!!!
	public Cursor timeAlarmOfQuality(int time){
		SQLiteDatabase db = getWritableDatabase(); 
		Date date=new Date();
		long time1=date.getTime()+time;
        Cursor c = db.query(this.GOODS_INFO, null, "?<="+time1, new String[]{this.QUALITY_TIME}, null, null, null);  
        return c;
	}
//存储时间提醒 没有关闭db!!!
	public Cursor AlarmOfStoreTime(int time){
		SQLiteDatabase db = getWritableDatabase(); 
		Date date=new Date();
		long time1=date.getTime()-time;
        Cursor c = db.query(this.GOODS_INFO, null, "?>"+time1, new String[]{this.STORE_TIME}, null, null, null);  
        return c;
	}
	

	
//	获得分类物品列表内容 没有关闭db!!!
	public Cursor getGoodsList(int type,int userId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c = db.query(GOODS_INFO, null, "goodsTypeId="+type+" and userId="+userId,null, null, null, null); 
		return c;
	}
//	根据typeId 与userId来筛选物品信息，用于填充gallery
	public String[][] getGoodsListByType(int type,int userId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c ;
		if(type!=0)
			c=db.query(GOODS_INFO, null, "goodsTypeId="+type+" and userId="+userId,null, null, null, null); 
		else
			c=db.query(GOODS_INFO, null, "userId="+userId,null, null, null, null); 
		String[][] goodsInfo=new String[4][c.getCount()];
		c.moveToFirst();
		int i=0;
		while(!c.isAfterLast()){
			goodsInfo[0][i]=c.getString(c.getColumnIndex("goodsId"));
			goodsInfo[1][i]=c.getString(c.getColumnIndex("goodsName"));
			goodsInfo[2][i]=c.getString(c.getColumnIndex("goodsImagepath"));
			String dateString=c.getString(c.getColumnIndex("storeTime"));
			long millsecond=Long.parseLong(dateString);
//			Log.e("微秒数", ""+millsecond);
			Date date=new Date(millsecond);
			String storeTime="";
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			storeTime+=format.format(date)+" "+date.getHours()+"时"+date.getMinutes()+"分";
			goodsInfo[3][i]=storeTime;
			i++;
			c.moveToNext();
		}
		c.close();
		db.close();
		return goodsInfo;
		
	}
	
//	获得空闲可用箱子列表 没有关闭db!!!
	public Cursor getContainerList(int userId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c = db.query(this.CONTAINER, null, "?="+userId+"or ?=0"+userId, new String[]{this.USER_ID,this.CONTAINER_STATUS}, null, null, null); 
		return c;
	}
//	删除物品
	public boolean deleteTableContent(int goodsId,int containerId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c = db.query(this.CONTAINER, null, "?="+containerId, new String[]{this.CONTAINER_ID}, null, null, null); 
		c.moveToFirst();
		int goodsNumber=c.getInt(c.getColumnIndex(this.CONTAINER_ID));
		ContentValues values=new ContentValues();
		values.put(this.CONTAINER_ID, containerId);
		values.put(this.CONTAINER_STATUS, goodsNumber);
		db.update(this.CONTAINER, values, "?="+containerId, new String[]{this.CONTAINER_ID});
		if(db.delete(this.GOODS_INFO, "?="+goodsId, new String[]{this.GOODS_ID})==1){
			db.close();
			return true;
		}
		db.close();
		return false;
	}
	
//	获得问题列表内全部内容 没有关闭db!!!
	public List<String> getSecurityQuestionContent(){
		List<String> list=new ArrayList<String>();
		SQLiteDatabase db = getWritableDatabase(); 
		
		Cursor c=db.query("securityQuestion", new String[]{"securityQuestionContent"}, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			list.add(c.getString(c.getColumnIndex("securityQuestionContent")));
			
	        c.moveToNext();
		}
		c.close();
		db.close();
		return list;
	}
	
//	获得物品类型列表内全部内容
	public List<String> getgoodsTypeList(){
		List<String> list=new ArrayList<String>();
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c;
		c=db.query(GOODS_TYPE, new String[]{"typeName"}, null, null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			list.add(c.getString(c.getColumnIndex("typeName")));
			
	        c.moveToNext();
		}
		db.close();
		return list;
	}
	
//	计算表中条目的数量
	public int getRowsCount(String tableName){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c=db.query(tableName, null, null, null, null,null, null);
		int num=c.getCount();
		db.close();
		return num;
	}
	
//	获得用户信息 没有关闭db!!!
	public HashMap<String,String> getUserInfo(int userId){
		HashMap<String,String> map=new HashMap<String,String>();
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c=db.query(FAMILY_MEMBER_INFO, null, "userId="+userId, null, null,null, null);
		c.moveToFirst();
		map.put(USERNAME, c.getString(c.getColumnIndex(DataBaseHelper.USERNAME)));
		map.put(SECURITY_QUESTION_ID, ""+c.getLong(c.getColumnIndex(DataBaseHelper.SECURITY_QUESTION_ID)));
		map.put(SECURITY_ANSWER, c.getString(c.getColumnIndex(DataBaseHelper.SECURITY_ANSWER)));
		c.close();
		db.close();
		return map;
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
//	返回goodsList的数据
	public String[][] getData(String searchContent,int goodsType,int userId) {
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c;
		if(searchContent.equals("")&&goodsType==0){
			c=db.query(GOODS_INFO, null,  "userId="+userId,null, null, null, null);
		}
		else if(searchContent.equals("")){
			c=db.query(GOODS_INFO, null,  "userId="+userId+" and goodsTypeId="+goodsType,null, null, null, null);
		}
		else if(goodsType==0){
			c=db.query(GOODS_INFO, null,  "userId="+userId+" and goodsName like ?",new String[]{searchContent}, null, null, null);
		}
		else{
			c=db.query(GOODS_INFO, null,  "userId="+userId+" and goodsTypeId="+goodsType+" and goodsName like ?",new String[]{searchContent}, null, null, null);
		}
		String[][] goodsInfo=new String[4][c.getCount()];
		c.moveToFirst();
		int i=0;
		while(!c.isAfterLast()){
			goodsInfo[0][i]=c.getString(c.getColumnIndex("goodsId"));
			goodsInfo[1][i]=c.getString(c.getColumnIndex("goodsName"));
			goodsInfo[2][i]=c.getString(c.getColumnIndex("goodsImagepath"));
			String dateString=c.getString(c.getColumnIndex("storeTime"));
			long millsecond=Long.parseLong(dateString);
			Date date=new Date(millsecond);
			String storeTime="";
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			storeTime+=format.format(date)+" "+date.getHours()+"时"+date.getMinutes()+"分";
			goodsInfo[3][i]=storeTime;
			i++;
			c.moveToNext();
		}
		c.close();
		db.close();
		return goodsInfo;

	}
	
	public List<Integer> getContainers(int userId){
		
		List<Integer> list=new ArrayList<Integer>();
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c;
		c=db.query(this.CONTAINER, new String[]{"containerId"}, "userId="+userId+" or containerStatus=0", null, null, null, null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			list.add(c.getInt(c.getColumnIndex("containerId")));
			
	        c.moveToNext();
		}
		db.close();
		return list;
	}
	
	public Bundle getGoodsInfoBundle(String goodsId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c ;
		c=db.query(GOODS_INFO, null, "goodsId =?",new String[]{goodsId}, null, null, null); 
		Bundle bundle=new Bundle();
		c.moveToFirst();

//		create table goodsInfo(goodsId varchar(128) primary key," +
//	"goodsName varchar(128),goodsImagepath varchar(128),
//		storeTime varchar(128),produceTime varchar(128),
//		qualityTime varchar(128)," +
//	"goodsTypeId int," +"containerId int,userId int);";
//		bundle.putString(EditGoodsActivity.JSON_GOODS_Id, value)
//		String goodsId=c.getString(c.getColumnIndex("goodsId"));
		String goodsName=c.getString(c.getColumnIndex("goodsName"));
		String goodsImagePath=c.getString(c.getColumnIndex("goodsImagepath"));
		String storeTimeString=c.getString(c.getColumnIndex("storeTime"));
		String produceTimeString=c.getString(c.getColumnIndex("produceTime"));
		String qualityTimeString=c.getString(c.getColumnIndex("qualityTime"));
		int storeLocation=c.getInt(c.getColumnIndex("containerId"));
		int goodsTypeId=c.getInt(c.getColumnIndex("goodsTypeId"));
		long qualityTime=(Long.parseLong(qualityTimeString))/24*60*60*1000;
		Date date=new Date(Long.parseLong(produceTimeString));
		String year=""+(date.getYear()+1900);
		String month=""+date.getMonth();
		String day=""+date.getDay();
		bundle.putString(EditGoodsActivity.JSON_GOODS_Id, goodsId);
		bundle.putString(EditGoodsActivity.JSON_GOODS_NAME, goodsName);
		bundle.putString(EditGoodsActivity.JSON_IMG_PATH, goodsImagePath);
		bundle.putString(EditGoodsActivity.JSON_DAY, day);
		bundle.putString(EditGoodsActivity.JSON_MONTH, month);
		bundle.putString(EditGoodsActivity.JSON_YEAR, year);
		bundle.putString(EditGoodsActivity.JSON_QUALITY, ""+qualityTime);
		bundle.putInt(EditGoodsActivity.JSON_TYPE, goodsTypeId);
		bundle.putString(EditGoodsActivity.JSON_LOCATION, ""+storeLocation);
	
		
		c.close();
		db.close();
		return bundle;
		
	}
	
	public int getStoreLocation(String goodsId){
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c ;
		c=db.query(GOODS_INFO, null, "goodsId =?",new String[]{goodsId}, null, null, null); 
		c.moveToFirst();
		int location=c.getInt(c.getColumnIndex("containerId"));
		c.close();
		db.close();
		return location;
	}
//获得用户列表中某列的值	
	public String getUserInfo1(int userId,String columnName){
		String result;
		SQLiteDatabase db = getReadableDatabase(); 
		Cursor c=db.query(FAMILY_MEMBER_INFO, null, "userId="+userId, null, null,null, null);
		c.moveToFirst();
		result=c.getString(c.getColumnIndex(columnName));
		c.close();
		db.close();
		return result;
	}
	public String[][] getStoreTimeAlarmGoodsInfo(int userId){
		long currentTime=new Date().getTime();
		long maxOfStoreTime=Long.parseLong(getUserInfo1(userId,"maxTimeOfStore"));
		
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c;
		
		c=db.query(GOODS_INFO, null,  "userId="+userId,null, null, null, null);
		String[][] goodsInfo=new String[4][c.getCount()];
		c.moveToFirst();
		int i=0;
		while(!c.isAfterLast()){
			String dateString=c.getString(c.getColumnIndex("storeTime"));
			long storeTime1=Long.parseLong(dateString);
			if(storeTime1+maxOfStoreTime<=currentTime){
				goodsInfo[0][i]=c.getString(c.getColumnIndex("goodsId"));
				goodsInfo[1][i]=c.getString(c.getColumnIndex("goodsName"));
				goodsInfo[2][i]=c.getString(c.getColumnIndex("goodsImagepath"));
				Date date=new Date(storeTime1);
				String storeTime="";
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				storeTime+=format.format(date)+" "+date.getHours()+"时"+date.getMinutes()+"分";
				
				goodsInfo[3][i]=storeTime;
				i++;
			}
			
			c.moveToNext();
		}
		c.close();
		db.close();
		String[][] goodsInfo1=new String[4][i];
		for(int j=0;j<i;j++){
			goodsInfo1[0][j]=goodsInfo[0][j];
			goodsInfo1[1][j]=goodsInfo[1][j];
			goodsInfo1[2][j]=goodsInfo[2][j];
			goodsInfo1[3][j]=goodsInfo[3][j];
		}
		return goodsInfo1;

	}
	
//	只对食品检查生产日期与保质期
	public String[][] getQualityTimeAlarmGoodsInfo(int userId){
		long currentTime=new Date().getTime();
//		BigInteger currentTime=new BigInteger(""+new Date().getTime());
		long maxOfQualityTime=Long.parseLong(getUserInfo1(userId,"timeAlarmOfQuality"));
		
		SQLiteDatabase db = getWritableDatabase(); 
		Cursor c;
		
		c=db.query(GOODS_INFO, null,  "userId="+userId+" and goodsTypeId="+1,null, null, null, null);
		String[][] goodsInfo=new String[4][c.getCount()];
		c.moveToFirst();
		int i=0;
		while(!c.isAfterLast()){
			String dateString=c.getString(c.getColumnIndex("produceTime"));
			long produceTime=Long.parseLong(dateString);
			dateString=c.getString(c.getColumnIndex("qualityTime"));
			long qualityTime=Long.parseLong(dateString);
			dateString=c.getString(c.getColumnIndex("storeTime"));
			long storeTime=Long.parseLong(dateString);
			if(currentTime+maxOfQualityTime>=produceTime+qualityTime){
				goodsInfo[0][i]=c.getString(c.getColumnIndex("goodsId"));
				goodsInfo[1][i]=c.getString(c.getColumnIndex("goodsName"));
				goodsInfo[2][i]=c.getString(c.getColumnIndex("goodsImagepath"));
				Date date=new Date(storeTime);
				String Time="";
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
				Time+="存储日期：\n"+format.format(date)+" "+date.getHours()+"时"+date.getMinutes()+"分";
				date=new Date(produceTime);
				Time+="\n生产日期："+format.format(date)+" ";
				Time+="\n保质期"+(qualityTime/(24*60*60*1000))+"天";
				goodsInfo[3][i]=Time;
				i++;
			}
			
			c.moveToNext();
		}
		c.close();
		db.close();
		String[][] goodsInfo1=new String[4][i];
		for(int j=0;j<i;j++){
			goodsInfo1[0][j]=goodsInfo[0][j]; 
			goodsInfo1[1][j]=goodsInfo[1][j];
			goodsInfo1[2][j]=goodsInfo[2][j];
			goodsInfo1[3][j]=goodsInfo[3][j];
		}
		return goodsInfo1;

	}
	
	public boolean checkGoodsExist(String goodsId){
		SQLiteDatabase db = getReadableDatabase(); 
		Cursor c=db.query(GOODS_INFO, null, "goodsId=?", new String[]{goodsId}, null,null, null);
		if(c.getCount()==0){
			c.close();
			db.close();
			return false;
		}
		else{
			c.close();
			db.close();
			return true;
		}
		
	}

}
