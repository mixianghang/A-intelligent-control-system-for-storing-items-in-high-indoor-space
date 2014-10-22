package com.house.control.bluetooth;
import java.sql.Date;
import java.util.Set;

import com.house.control.R;
import com.house.control.main.HouseControlActivityGroup;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BlueToothActivity extends Activity implements OnClickListener {
	
	HouseControlActivityGroup parent=null;
	
	private Button enableBtButton;//开启蓝牙按钮
	private Button enableDiscoverableButton;//设置本地蓝牙适配器对外可见
	private Button scanButton;//扫描蓝牙设备
	private Button sendMessageButton;//向已经连接的蓝牙设备发送信息
	private EditText discoverableTimeEdit;//输入蓝牙适配器对外可见的时间
	private EditText newReceivedEdit;//显示对方最新发送的的信息
	private EditText newSendEdit;//输入发送给对方的信息
	private ListView pairedDevicesList;
	private ListView newFoundDeviceList;
	
    //此处用于监测程序运行  Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
//    当前程序状态
    private final String disconnected="未连接任何设备";
    private final String connecting="正在连接中......";
    private final String connected="已连接设备：";
    
  //接收到的消息的类型： Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;//连接状态改变的消息
    public static final int MESSAGE_READ = 2689;//接收到的对方消息
    public static final int MESSAGE_WRITE = 3689;//正在发给对方的消息
    public static final int MESSAGE_DEVICE_NAME = 4;//
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // 当要求连接设备及开启蓝牙时，发送的请求代码，Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    private BluetoothAdapter mBtAdapter=null;//蓝牙适配器
    //执行后台任务的 类的对象 Member object for the chat services
    private BluetoothChatService mChatService = null;
    private String BluetoothDeviceName;//连接的蓝牙设备名称
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;//与配对的设别列表绑定
    private ArrayAdapter<String> mNewDevicesArrayAdapter;//与新发现的设备列表绑定
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在调用getWindow来定义窗口某一部分的特色的时候，首先请求允许自定义该部分的特色，定义窗口的特色 Set up the window layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bluetooth);
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        
        parent=(HouseControlActivityGroup)(this.getParent().getParent());
        mHandler=parent.getHandler();

        
        enableBtButton=(Button)findViewById(R.id.enableBlueToothButton);
        enableDiscoverableButton=(Button)findViewById(R.id.enableDiscoverableButton);
        scanButton=(Button)findViewById(R.id.scanBlueToothButton);
    
        
        enableBtButton.setOnClickListener(this);
        enableDiscoverableButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
//        sendMessageButton.setOnClickListener(this);
        
        //初始化三个edittext
        discoverableTimeEdit=(EditText)findViewById(R.id.discoverableTime);
        
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        
        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.pairedDeiviceList);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        ListView newDevicesListView = (ListView) findViewById(R.id.newFoundDeiviceList);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        
        if(mBtAdapter==null){
			Toast.makeText(this, "本设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return;
		}
		else if (!mBtAdapter.isEnabled()) {//监测蓝牙是否开启
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        }
		else{
//			Toast.makeText(this, "蓝牙已经开启！", Toast.LENGTH_LONG).show();
			if(mChatService==null){
				
				mChatService=BluetoothChatService.getInstance(parent,mHandler);
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
		              // 使程序处于监听其他设备主动连接的状态，Start the Bluetooth chat services
		              mChatService.start();
		            }
			}
		}
        
     

        
    }
    
    private void openBluetooth(){
        if(mBtAdapter==null){
   			Toast.makeText(this, "本设备不支持蓝牙", Toast.LENGTH_LONG).show();
               return;
   		}
   		else if (!mBtAdapter.isEnabled()) {//监测蓝牙是否开启
               Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
           // Otherwise, setup the chat session
           }
   		else{
//   			Toast.makeText(this, "蓝牙已经开启！", Toast.LENGTH_LONG).show();
   			if(mChatService==null){
   				
   				mChatService=BluetoothChatService.getInstance(parent,mHandler);
   				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
   		              // 使程序处于监听其他设备主动连接的状态，Start the Bluetooth chat services
   		              mChatService.start();
   		            }
   			}
   		}
    }
    
    //监听回车键的器 The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                if (message.length() > 0) {
    	            // Get the message bytes and tell the BluetoothChatService to write
    	            byte[] send = message.getBytes();
    	            mChatService.write(send);

    	            // Reset out string buffer to zero and clear the edit text field
    	            newSendEdit.setText(""); 
    	        }
            }
            if(D) Log.i(TAG, "END onEditorAction");
            return true;
        }
    };
    
    @Override
    public void onDestroy() {
        super.onDestroy();
//        // 此处值得学习，当你退出程序时，应该确保该程序的其他一些线程同样停止，Stop the Bluetooth chat services
//        if (mChatService != null) mChatService.stop();
//        if(D) Log.e(TAG, "--- ON DESTROY ---");
    }
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            if(info.length()<17){
            	Toast.makeText(BlueToothActivity.this, "当前没有搜寻到新设备", Toast.LENGTH_LONG).show();
            	return ;
            }
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            // Attempt to connect to the deviceif
            if(mChatService!=null)
            mChatService.connect(device);
            else{
            	Log.e("null", "mchatService is null");
            	Toast.makeText(BlueToothActivity.this, "系统无蓝牙模块或蓝牙模块未开启！", Toast.LENGTH_LONG).show();
            }
//            // Create the result Intent and include the MAC address
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
//
//            // Set result and finish this Activity
//            setResult(Activity.RESULT_OK, intent);
//            finish();
        }
    };

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.enableBlueToothButton:{//开启蓝牙
			if(mBtAdapter==null){
				Toast.makeText(this, "本设备不支持蓝牙", Toast.LENGTH_LONG).show();
	            return;
			}
			else if (!mBtAdapter.isEnabled()) {//监测蓝牙是否开启
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        }
			else{
				Toast.makeText(this, "蓝牙已经开启！", Toast.LENGTH_LONG).show();
				if(mChatService==null){
					
					mChatService=BluetoothChatService.getInstance(parent,mHandler);
					if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			              // 使程序处于监听其他设备主动连接的状态，Start the Bluetooth chat services
			              mChatService.start();
			            }
				}
			}
			break;
		}
		
		case R.id.enableDiscoverableButton:{
			this.openBluetooth();
			String time=discoverableTimeEdit.getText().toString();
			if(time!=null&&!(time.equals("")))
			ensureDiscoverable(discoverableTimeEdit.getText().toString());
			else{
				Toast.makeText(getApplicationContext(), "请输入允许可见的秒数", Toast.LENGTH_LONG).show();
			}
			break;
		}
		
		case R.id.scanBlueToothButton:{
			 // Get a set of currently paired devices
			this.openBluetooth();
	        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
	        mPairedDevicesArrayAdapter.clear();
	        mNewDevicesArrayAdapter.clear();
	        // If there are paired devices, add each one to the ArrayAdapter
	        if (pairedDevices.size() > 0) {
	            for (BluetoothDevice device : pairedDevices) {
	                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            }
	        } else {
	            String noDevices = "没有配对的设备";
	            mPairedDevicesArrayAdapter.add(noDevices);
	        }
	        // Register for broadcasts when a device is discovered
	        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	        this.registerReceiver(mReceiver, filter);

	        // Register for broadcasts when discovery has finished
	        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
	        this.registerReceiver(mReceiver, filter);
	        doDiscovery();

//	        this.unregisterReceiver(mReceiver);


			break;
		}
		
//		case R.id.sendMessageButton:{
//			if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
//	            Toast.makeText(this, "没有连接蓝牙设备！", Toast.LENGTH_SHORT).show();
//	            return;
//	        }
//			String message=newSendEdit.getText().toString();
//			if (message.length() > 0) {
//	            // Get the message bytes and tell the BluetoothChatService to write
//	            byte[] send = message.getBytes();
//	            mChatService.write(send);
//
//	            // Reset out string buffer to zero and clear the edit text field
//	            newSendEdit.setText("");
//	        }
//			break;
//		}
		}
	}
//	扫描其它蓝牙设备
	  private void doDiscovery() {
	        if (D) Log.d(TAG, "doDiscovery()");

	      
	        // If we're already discovering, stop it
	        if (mBtAdapter.isDiscovering()) {
	            mBtAdapter.cancelDiscovery();
	        }

	        // Request discover from BluetoothAdapter
	        mBtAdapter.startDiscovery();
	    }
	// The Handler that gets information back from the BluetoothChatService
    private  Handler mHandler = null;
//    		new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//            case MESSAGE_STATE_CHANGE:
//                if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
//                switch (msg.arg1) {
//                case BluetoothChatService.STATE_CONNECTED:
//                    title.setText(connected);
//                    title.append(BluetoothDeviceName);
////                    mConversationArrayAdapter.clear();
//                    
//                    break;
//                case BluetoothChatService.STATE_CONNECTING:
//                    title.setText(connecting);
//                    break;
//                case BluetoothChatService.STATE_LISTEN:
//                case BluetoothChatService.STATE_NONE:
//                    title.setText(disconnected);
//                    break;
//                }
//                break;
//            case MESSAGE_WRITE:
//                byte[] writeBuf = (byte[]) msg.obj;
//                // construct a string from the buffer
//                String writeMessage = new String(writeBuf);
////                newReceivedEdit.setText("Me:  " + writeMessage);
//                break;
//            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
//                // construct a string from the valid bytes in the buffer
//                String readMessage = new String(readBuf, 0, msg.arg1);
//                newReceivedEdit.setText( readMessage);
//                break;
//            case MESSAGE_DEVICE_NAME:
//                // save the connected device's name
//                BluetoothDeviceName = msg.getData().getString(DEVICE_NAME);
//                Toast.makeText(getApplicationContext(), "Connected to "
//                               + BluetoothDeviceName, Toast.LENGTH_SHORT).show();
//                break;
//            case MESSAGE_TOAST:
//                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
//                               Toast.LENGTH_SHORT).show();
//                break;
//            }
//        }
//    };
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
            	Toast.makeText(this, "蓝牙已经开启！", Toast.LENGTH_LONG).show();
				if(mChatService==null){
					mChatService=BluetoothChatService.getInstance(parent,mHandler);
					if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			              // 使程序处于监听其他设备主动连接的状态，Start the Bluetooth chat services
			              mChatService.start();
			            }
				}
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "蓝牙无法开启！", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }
	
  //使自己能被其他蓝牙设备发现
    private void ensureDiscoverable(String timeString) {

        if(D) Log.d(TAG, "ensure discoverable");
        Integer time=new Integer(timeString);
        if (mBtAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, time);
            startActivity(discoverableIntent);
        }
    }
    
 // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
//                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "没有新的设备";
                    mNewDevicesArrayAdapter.add(noDevices);
                    mBtAdapter.cancelDiscovery();
                }
            }
        }
    };
    
//  //设置菜单栏的选项
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu);//在此处定义菜单的样式
//        return true;
//    }
////设置菜单栏选项被选中的触发事件
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//        case R.id.scan:
//            // Launch the DeviceListActivity to see devices and do scan
//            Intent serverIntent = new Intent(this, DeviceListActivity.class);
//            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
//            return true;
//        case R.id.discoverable:
//            // Ensure this device is discoverable by others
//            ensureDiscoverable();
//            return true;
//        }
//        return false;
//    }

    Date date=new Date(0);
}