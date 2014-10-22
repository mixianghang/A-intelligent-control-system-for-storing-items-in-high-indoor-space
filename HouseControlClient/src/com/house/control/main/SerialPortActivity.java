package com.house.control.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.friendlyarm.AndroidSDK.HardwareControler;
import com.house.control.R;

public class SerialPortActivity extends Activity
{
    private static final String[] serial_port={"/dev/s3c2410_serial0","/dev/s3c2410_serial1","/dev/s3c2410_serial2"};
    private static final String[] baud_rate={"2400","4800","9600","19200","115200"};
    
    TextView chooseserialPortView;
    TextView choosebaudRateView;
    TextView commucationView;
    EditText editmsg;
    private Button stopButton;
    private Button sendButton;
    private Button connectButton;
    private Button listenButton;
    private Button stopListenButton;
    private Spinner choose_serialport;
    private Spinner choose_baudrate;
    private ArrayAdapter<String> serialportAdapter;
    private ArrayAdapter<String> baudrateAdaptera;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SerialPortListener.DATA_TO_READ:
//                int len = HardwareControler.read(fd, buf, 300);
                String string = (String) msg.obj;
                if(string!=null)
                	commucationView.append(string+"\n");
                Log.e(thread,"接收到数据，新线程启动");
                new SerialPortListener(handler).start();
                break;
            case SerialPortListener.NO_DATA_TO_READ:
                HardwareControler.setLedState(1, 0);
                new SerialPortListener(handler).start();
                Log.e(thread,"没有数据，新线程启动");
                break;
            case SerialPortListener.NO_SERIAL_PORT:
            	Toast.makeText(getApplicationContext(), "串口并未打开！", Toast.LENGTH_LONG).show();
            	break;
            default:
                break;
            }
        }
    };
    
   
    
    private int fd = 0;
    String thread = "readThread";
    int choosed_serial = 0;
    int choosed_buad = 0;
    byte[] buf= new byte[300];

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serial);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        chooseserialPortView = (TextView)findViewById(R.id.choose_serialPort_text);
        choose_serialport = (Spinner)findViewById(R.id.choose_seriaPort_spinner);
        chooseserialPortView = (TextView)findViewById(R.id.choose_baudRate_text);
        choose_baudrate = (Spinner)findViewById(R.id.choose_baudRate_spinner);
                
        serialportAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,serial_port);//建立下拉控件的适配器
        baudrateAdaptera = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,baud_rate);
        serialportAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        baudrateAdaptera.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        choose_serialport.setAdapter(serialportAdapter);//连接控件和适配器
        choose_baudrate.setAdapter(baudrateAdaptera);
        choose_serialport.setSelection(2);
        choose_baudrate.setSelection(2);
        
        choose_serialport.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                choosed_serial = arg2;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
            
        choose_baudrate.setOnItemSelectedListener(new Spinner.OnItemSelectedListener()
        {
            
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                choosed_buad = arg2;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
            
        });
    
        stopButton = (Button)findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new ClickEvent());
        
        sendButton = (Button)findViewById(R.id.sendButton);//发送消息
        sendButton.setOnClickListener(new OnClickListener() {
            
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if(SerialPortListener.writeDataToSerialPort(editmsg.getText().toString().getBytes()))
                commucationView.append(editmsg.getText()+"\n");
                else
                	Toast.makeText(getApplicationContext(), "发送数据失败，请检查串口是否打开", Toast.LENGTH_LONG).show();
                	
            }
        });
        
        connectButton=(Button)findViewById(R.id.connectSerialPortButton);
        connectButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(SerialPortListener.openSerialPort(choosed_serial, choosed_buad))
					Toast.makeText(getApplicationContext(), "成功打开串口，波特率为"+baud_rate[choosed_buad], Toast.LENGTH_LONG).show();
				else{
					Toast.makeText(getApplicationContext(), "打开串口失败", Toast.LENGTH_LONG).show();
				}
			}
        	
        });
        listenButton=(Button)findViewById(R.id.listenSerialPortButton);
        listenButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new SerialPortListener(handler).start();
			}
        	
        });
        
        
        stopListenButton=(Button)findViewById(R.id.stopListenSerialPortButton);
        stopListenButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				listener.stopListen();
			}
        	
        });

        commucationView = (TextView)findViewById(R.id.commucation_window);
        commucationView.setMovementMethod(ScrollingMovementMethod.getInstance()); //让textview实现滚动
        editmsg = (EditText)findViewById(R.id.editmsg);
        
        
    }
    
    public class ClickEvent implements Button.OnClickListener//退出
    {
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            SerialPortListener.closeSerialPort();
            Toast.makeText(getApplicationContext(), "串口已经关闭！", Toast.LENGTH_LONG).show();
        }
    }
    
    
    class readThread extends Thread//读取串口信息线程
    {   
    	private Handler handler;
    	public readThread(Handler handler){
    		this.handler=handler;
    	}
        public void run()
        {
            Message msg = new Message();
            HardwareControler.setLedState(0, 0);
            if (HardwareControler.select(fd,1, 1000000)==1) {            
                msg.arg1 = 0;            
            }
            else {
                msg.arg1 =1;
                HardwareControler.setLedState(0, 1);
            }
            handler.sendMessage(msg);
        }
    }
}