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
	
	private Button enableBtButton;//����������ť
	private Button enableDiscoverableButton;//���ñ�����������������ɼ�
	private Button scanButton;//ɨ�������豸
	private Button sendMessageButton;//���Ѿ����ӵ������豸������Ϣ
	private EditText discoverableTimeEdit;//������������������ɼ���ʱ��
	private EditText newReceivedEdit;//��ʾ�Է����·��͵ĵ���Ϣ
	private EditText newSendEdit;//���뷢�͸��Է�����Ϣ
	private ListView pairedDevicesList;
	private ListView newFoundDeviceList;
	
    //�˴����ڼ���������  Debugging
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    
//    ��ǰ����״̬
    private final String disconnected="δ�����κ��豸";
    private final String connecting="����������......";
    private final String connected="�������豸��";
    
  //���յ�����Ϣ�����ͣ� Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;//����״̬�ı����Ϣ
    public static final int MESSAGE_READ = 2689;//���յ��ĶԷ���Ϣ
    public static final int MESSAGE_WRITE = 3689;//���ڷ����Է�����Ϣ
    public static final int MESSAGE_DEVICE_NAME = 4;//
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // ��Ҫ�������豸����������ʱ�����͵�������룬Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    
    private BluetoothAdapter mBtAdapter=null;//����������
    //ִ�к�̨����� ��Ķ��� Member object for the chat services
    private BluetoothChatService mChatService = null;
    private String BluetoothDeviceName;//���ӵ������豸����
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;//����Ե�����б��
    private ArrayAdapter<String> mNewDevicesArrayAdapter;//���·��ֵ��豸�б��
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //�ڵ���getWindow�����崰��ĳһ���ֵ���ɫ��ʱ���������������Զ���ò��ֵ���ɫ�����崰�ڵ���ɫ Set up the window layout
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
        
        //��ʼ������edittext
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
			Toast.makeText(this, "���豸��֧������", Toast.LENGTH_LONG).show();
            return;
		}
		else if (!mBtAdapter.isEnabled()) {//��������Ƿ���
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        }
		else{
//			Toast.makeText(this, "�����Ѿ�������", Toast.LENGTH_LONG).show();
			if(mChatService==null){
				
				mChatService=BluetoothChatService.getInstance(parent,mHandler);
				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
		              // ʹ�����ڼ��������豸�������ӵ�״̬��Start the Bluetooth chat services
		              mChatService.start();
		            }
			}
		}
        
     

        
    }
    
    private void openBluetooth(){
        if(mBtAdapter==null){
   			Toast.makeText(this, "���豸��֧������", Toast.LENGTH_LONG).show();
               return;
   		}
   		else if (!mBtAdapter.isEnabled()) {//��������Ƿ���
               Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
           // Otherwise, setup the chat session
           }
   		else{
//   			Toast.makeText(this, "�����Ѿ�������", Toast.LENGTH_LONG).show();
   			if(mChatService==null){
   				
   				mChatService=BluetoothChatService.getInstance(parent,mHandler);
   				if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
   		              // ʹ�����ڼ��������豸�������ӵ�״̬��Start the Bluetooth chat services
   		              mChatService.start();
   		            }
   			}
   		}
    }
    
    //�����س������� The action listener for the EditText widget, to listen for the return key
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
//        // �˴�ֵ��ѧϰ�������˳�����ʱ��Ӧ��ȷ���ó��������һЩ�߳�ͬ��ֹͣ��Stop the Bluetooth chat services
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
            	Toast.makeText(BlueToothActivity.this, "��ǰû����Ѱ�����豸", Toast.LENGTH_LONG).show();
            	return ;
            }
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
            // Attempt to connect to the deviceif
            if(mChatService!=null)
            mChatService.connect(device);
            else{
            	Log.e("null", "mchatService is null");
            	Toast.makeText(BlueToothActivity.this, "ϵͳ������ģ�������ģ��δ������", Toast.LENGTH_LONG).show();
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
		case R.id.enableBlueToothButton:{//��������
			if(mBtAdapter==null){
				Toast.makeText(this, "���豸��֧������", Toast.LENGTH_LONG).show();
	            return;
			}
			else if (!mBtAdapter.isEnabled()) {//��������Ƿ���
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        }
			else{
				Toast.makeText(this, "�����Ѿ�������", Toast.LENGTH_LONG).show();
				if(mChatService==null){
					
					mChatService=BluetoothChatService.getInstance(parent,mHandler);
					if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			              // ʹ�����ڼ��������豸�������ӵ�״̬��Start the Bluetooth chat services
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
				Toast.makeText(getApplicationContext(), "����������ɼ�������", Toast.LENGTH_LONG).show();
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
	            String noDevices = "û����Ե��豸";
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
//	            Toast.makeText(this, "û�����������豸��", Toast.LENGTH_SHORT).show();
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
//	ɨ�����������豸
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
            	Toast.makeText(this, "�����Ѿ�������", Toast.LENGTH_LONG).show();
				if(mChatService==null){
					mChatService=BluetoothChatService.getInstance(parent,mHandler);
					if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
			              // ʹ�����ڼ��������豸�������ӵ�״̬��Start the Bluetooth chat services
			              mChatService.start();
			            }
				}
            } else {
                // User did not enable Bluetooth or an error occured
                Log.d(TAG, "BT not enabled");
                Toast.makeText(this, "�����޷�������", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }
	
  //ʹ�Լ��ܱ����������豸����
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
                    String noDevices = "û���µ��豸";
                    mNewDevicesArrayAdapter.add(noDevices);
                    mBtAdapter.cancelDiscovery();
                }
            }
        }
    };
    
//  //���ò˵�����ѡ��
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu);//�ڴ˴�����˵�����ʽ
//        return true;
//    }
////���ò˵���ѡ�ѡ�еĴ����¼�
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