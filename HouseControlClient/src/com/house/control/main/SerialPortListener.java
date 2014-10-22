package com.house.control.main;

import com.friendlyarm.AndroidSDK.HardwareControler;

import android.os.Handler;
import android.os.Message;

/*
 * 经测试发现，当监听到数据的时候要立马就取，否则收不到了
 * 另外byte与string的转换不能直接用tostring，而用new string(bytes)
 * message.arg1,message.what.message.arg2,message.obj区分开
 */
public class SerialPortListener extends Thread {
	private Handler handler;
	public final static int DATA_TO_READ=7890;
	public final static int NO_DATA_TO_READ=8901;
	public final static int NO_SERIAL_PORT=9012;
	
	
	private static boolean isStop=false;
	
	private static int TIME_OF_WAIT_FOR_DATA=15;
	
	 private static final String[] serial_port={"/dev/s3c2410_serial0","/dev/s3c2410_serial1","/dev/s3c2410_serial2"};
	 private static final int[] baud_rate={2400,4800,9600,19200,115200};
	
	 private static  int currentSerialPortId=-1;
	 private static int currentBaudRateId=-1;
//	端口的文件描述符
	private static int fd=-1;
	public SerialPortListener(Handler handler){
		this.handler=handler;
	}
	
	public  static boolean openSerialPort(int serialId,int baudRateId){
		currentSerialPortId=serialId;
		currentBaudRateId=baudRateId;
		fd = HardwareControler.openSerialPort(serial_port[serialId],baud_rate[baudRateId], 8, 1);//打开串口
      return fd!=-1;
	}
	
	
	public static void closeSerialPort(){
		HardwareControler.close(fd);
		fd=-1;
	}
	
	public static boolean writeDataToSerialPort(byte[] bytes){
		 if(HardwareControler.write(fd, bytes)==-1)
			 return false;
		 else
			 return true;
	}
	
	
	public static String readDataFromSerialPort(byte[] buf,int length){
		
		int len = HardwareControler.read(fd, buf, length);
		if(len<0)
			return null;
		else if(len==0)
			return "";
		return new String(buf,0,len);
	}
	
	public static void stopListen(){
		isStop=true;
	}
	
	 public void run()
     {
         Message msg = new Message();
         HardwareControler.setLedState(0, 0);
         if(fd==-1){
        	 msg.what = NO_SERIAL_PORT;  
         }
         else if (HardwareControler.select(fd,TIME_OF_WAIT_FOR_DATA, 0)==1) {            
             msg.what = DATA_TO_READ;
             byte[] buf=new byte[1024];
             int len = HardwareControler.read(fd, buf, 1024);
             String result=null;
     		if(len<0)
     			result=null;
     		else if(len==0)
     			result="";
     		else
     		result=new String(buf,0,len);
             msg.obj=result;
         }
         else {
             msg.what =NO_DATA_TO_READ;
             HardwareControler.setLedState(0, 1);
         }
         handler.sendMessage(msg);
     }
	
}
