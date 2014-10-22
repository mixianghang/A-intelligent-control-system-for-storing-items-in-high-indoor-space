/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.encode.QRCodeEncoder;
import com.google.zxing.client.android.history.HistoryActivity;
import com.google.zxing.client.android.history.HistoryItem;
import com.google.zxing.client.android.history.HistoryManager;
import com.google.zxing.client.android.result.ResultButtonListener;
import com.google.zxing.client.android.result.ResultHandler;
import com.google.zxing.client.android.result.ResultHandlerFactory;
import com.google.zxing.client.android.result.supplement.SupplementalInfoRetriever;
import com.google.zxing.client.android.share.ShareActivity;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.ResultParser;
import com.house.control.R;
import com.house.control.main.AddGoodsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.ClipboardManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This activity opens the camera and does the actual scanning on a background thread. It draws a
 * viewfinder to help the user place the barcode correctly, shows feedback as the image processing
 * is happening, and then overlays the results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class CaptureActivity extends Activity implements SurfaceHolder.Callback {

  
	String message1="您输入的一维条形码有误，请输入13位的EAN_13码";
	public static String productName;
	private static boolean resultCanUse=false;
	private static String type=null;
	
	private String data=null;
//	表示扫描的是一维条形码
	public final static String ISBN_PRODUCT="ISBN";
//	表示扫描的二维码是之前生成的保存物品信息的二维码
	public final static String PRINTED_BARCODE="printedBarcode";
	
private static final String TAG = CaptureActivity.class.getSimpleName();
  EditText edit1;
  
  public static final int REQUEST_IMG_PATH=1;
  public static final int REQUEST_1D_BARCODE=2;
//  此为菜单栏五个选项的编号
  private static final int SHARE_ID = Menu.FIRST;
  private static final int HISTORY_ID = Menu.FIRST + 1;
  private static final int SETTINGS_ID = Menu.FIRST + 2;
  private static final int HELP_ID = Menu.FIRST + 3;
  private static final int ABOUT_ID = Menu.FIRST + 4;
  private static final int DECODE_IMAGE = Menu.FIRST + 5;
  private static final int DECODE_1D_BARCODE = Menu.FIRST + 6;
  private static final int OK = Menu.FIRST + 7;
  private static final int CANCEL = Menu.FIRST + 8;

  private static final long DEFAULT_INTENT_RESULT_DURATION_MS = 1500L;
  private static final long BULK_MODE_SCAN_DELAY_MS = 1000L;

  private static final String PACKAGE_NAME = "com.google.zxing.client.android";
//  产品搜索的网址前缀
  private static final String PRODUCT_SEARCH_URL_PREFIX = "http://www.google";
  private static final String PRODUCT_SEARCH_URL_SUFFIX = "/m/products/scan";
  private static final String[] ZXING_URLS = { "http://zxing.appspot.com/scan", "zxing://scan/" };
  private static final String RETURN_CODE_PLACEHOLDER = "{CODE}";
  private static final String RETURN_URL_PARAM = "ret";

  public static final int HISTORY_REQUEST_CODE = 0x0000bacc;

  private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES =
      EnumSet.of(ResultMetadataType.ISSUE_NUMBER,
                 ResultMetadataType.SUGGESTED_PRICE,
                 ResultMetadataType.ERROR_CORRECTION_LEVEL,
                 ResultMetadataType.POSSIBLE_COUNTRY);

  private CameraManager cameraManager;
  private CaptureActivityHandler handler;
  private Result savedResultToShow;
  private ViewfinderView viewfinderView;
  private TextView statusView;
  private View resultView;
  private Result lastResult;
  private boolean hasSurface;
  private boolean copyToClipboard;
  private IntentSource source;
  private String sourceUrl;
  private String returnUrlTemplate;
  private Collection<BarcodeFormat> decodeFormats;
  private String characterSet;
  private String versionName;
  private HistoryManager historyManager;
  private InactivityTimer inactivityTimer;
  private BeepManager beepManager;

//  点击关于菜单的时候，访问官方网站
  private final DialogInterface.OnClickListener aboutListener =
      new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
      Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.zxing_url)));
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      startActivity(intent);
    }
  };

  ViewfinderView getViewfinderView() {
    return viewfinderView;
  }

  public Handler getHandler() {
    return handler;
  }

  CameraManager getCameraManager() {
    return cameraManager;
  }

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);

    Window window = getWindow();
//    当此窗口可见的时候，保持屏幕亮度
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.capture);

    hasSurface = false;
//    浏览历史的一些函数与变量，数据库操作类
    historyManager = new HistoryManager(this);
//    删除大于500以上的记录
    historyManager.trimHistory();
//    在手机靠电池供电的时候，在一段静止无操作后关闭activity
    inactivityTimer = new InactivityTimer(this);
    
//    管理该activity发出的声音的音量及振动效果等等
    beepManager = new BeepManager(this);

//    设置sharedpreference的默认值
    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
//第一次使用的时候显示帮助
    showHelpOnFirstLaunch();
//    Intent it = new Intent(this,com.android.fileManager.FileManager.class);
////    Uri data = Uri.parse("/sdcard");//sdcard中的多媒体文件
////    it.setData(data);
//    startActivity(it);
    
  }

  @Override
  protected void onResume() {
    super.onResume();
//有些事情不一定要在onCreate()中完成，充分利用activity提供的函数
    // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
    // want to open the camera driver and measure the screen size if we're going to show the help on
    // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
    // off screen.
    cameraManager = new CameraManager(getApplication());
//    cameraManager = new CameraManager(this);
//Log.e("get ","it");
    viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
    viewfinderView.setCameraManager(cameraManager);
    

    resultView = findViewById(R.id.result_view);
    statusView = (TextView) findViewById(R.id.status_view);

//    处理所有的消息
    handler = null;
//    将解码结果以图片方式返回
    lastResult = null;

    resetStatusView();

    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
    SurfaceHolder surfaceHolder = surfaceView.getHolder();
    if (hasSurface) {
      // The activity was paused but not stopped, so the surface still exists. Therefore
      // surfaceCreated() won't be called, so init the camera here.
      initCamera(surfaceHolder);
    } else {
      // Install the callback and wait for surfaceCreated() to init the camera.
      surfaceHolder.addCallback(this);
      surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
//处理程序音效
    beepManager.updatePrefs();

    inactivityTimer.onResume();

    Intent intent = getIntent(); 

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    copyToClipboard = prefs.getBoolean(PreferencesActivity.KEY_COPY_TO_CLIPBOARD, true)
        && (intent == null || intent.getBooleanExtra(Intents.Scan.SAVE_HISTORY, true));

    source = IntentSource.NONE;
    decodeFormats = null;
    characterSet = null;
//根据请求此activity的intent的类型及携带的数据决定扫描的格式、宽度、高度等
    if (intent != null) {

      String action = intent.getAction();
      String dataString = intent.getDataString();

      if (Intents.Scan.ACTION.equals(action)) {

        // Scan the formats the intent requested, and return the result to the calling activity.
        source = IntentSource.NATIVE_APP_INTENT;
        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);

        if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
          int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
          int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
          if (width > 0 && height > 0) {
            cameraManager.setManualFramingRect(width, height);
          }
        }
        
        String customPromptMessage = intent.getStringExtra(Intents.Scan.PROMPT_MESSAGE);
        if (customPromptMessage != null) {
          statusView.setText(customPromptMessage);
        }

      } else if (dataString != null &&
                 dataString.contains(PRODUCT_SEARCH_URL_PREFIX) &&
                 dataString.contains(PRODUCT_SEARCH_URL_SUFFIX)) {

        // Scan only products and send the result to mobile Product Search.
        source = IntentSource.PRODUCT_SEARCH_LINK;
        sourceUrl = dataString;
        decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

      } else if (isZXingURL(dataString)) {

        // Scan formats requested in query string (all formats if none specified).
        // If a return URL is specified, send the results there. Otherwise, handle it ourselves.
        source = IntentSource.ZXING_LINK;
        sourceUrl = dataString;
        Uri inputUri = Uri.parse(sourceUrl);
        returnUrlTemplate = inputUri.getQueryParameter(RETURN_URL_PARAM);
        decodeFormats = DecodeFormatManager.parseDecodeFormats(inputUri);

      }

      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

    }
  
  }
  
  private static boolean isZXingURL(String dataString) {
    if (dataString == null) {
      return false;
    }
    for (String url : ZXING_URLS) {
      if (dataString.startsWith(url)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected void onPause() {
    if (handler != null) {
      handler.quitSynchronously();
      handler = null;
    }
    inactivityTimer.onPause();
    cameraManager.closeDriver();
    if (!hasSurface) {
      SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
      SurfaceHolder surfaceHolder = surfaceView.getHolder();
      surfaceHolder.removeCallback(this);
    }
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    inactivityTimer.shutdown();
    super.onDestroy();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (source == IntentSource.NATIVE_APP_INTENT) {
        setResult(RESULT_CANCELED);
        finish();
        return true;
      } else if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
        restartPreviewAfterDelay(0L);
        return true;
      }
    } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
      // Handle these events so they don't launch the Camera app
      return true;
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    menu.add(Menu.NONE, OK, Menu.NONE, R.string.button_done)
    .setIcon(android.R.drawable.ic_menu_info_details);
    menu.add(Menu.NONE, CANCEL, Menu.NONE, R.string.button_cancel)
    .setIcon(android.R.drawable.ic_menu_info_details);
    menu.add(Menu.NONE, DECODE_IMAGE, Menu.NONE, R.string.menu_decode_image)
    .setIcon(android.R.drawable.ic_menu_info_details);
menu.add(Menu.NONE, DECODE_1D_BARCODE, Menu.NONE, R.string.menu_decode_1d_barcode)
.setIcon(android.R.drawable.ic_menu_info_details);
    menu.add(Menu.NONE, SHARE_ID, Menu.NONE, R.string.menu_share)
        .setIcon(android.R.drawable.ic_menu_share);
    menu.add(Menu.NONE, HISTORY_ID, Menu.NONE, R.string.menu_history)
        .setIcon(android.R.drawable.ic_menu_recent_history);
    menu.add(Menu.NONE, SETTINGS_ID, Menu.NONE, R.string.menu_settings)
        .setIcon(android.R.drawable.ic_menu_preferences);
    menu.add(Menu.NONE, HELP_ID, Menu.NONE, R.string.menu_help)
        .setIcon(android.R.drawable.ic_menu_help);
   
   
    
    return true;
  }

  // Don't display the share menu item if the result overlay is showing.
//  在扫描结果出来的时候，不要显示分享菜单
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(SHARE_ID).setVisible(lastResult == null);
    menu.findItem(OK).setVisible(this.resultCanUse&&type!=null);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    switch (item.getItemId()) {
      case SHARE_ID:
//    	  TextView text=(TextView)findViewById(R.id.contents_text_view);
//    	  Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
        intent.setClassName(this, ShareActivity.class.getName());
        startActivity(intent);
        break;
      case HISTORY_ID:
//    	  TextView text=(TextView)findViewById(R.id.contents_supplement_text_view);
//    	  Toast.makeText(this, text.getText(), Toast.LENGTH_LONG).show();
        intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setClassName(this, HistoryActivity.class.getName());
        startActivityForResult(intent, HISTORY_REQUEST_CODE);
        break;
      case SETTINGS_ID:
        intent.setClassName(this, PreferencesActivity.class.getName());
        startActivity(intent);
        break;
      case HELP_ID:
        intent.setClassName(this, HelpActivity.class.getName());
        startActivity(intent);
        break;
      case ABOUT_ID:
//    	  让用户选择是否访问网站
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.title_about) + versionName);
        builder.setMessage(getString(R.string.msg_about) + "\n\n" + getString(R.string.zxing_url));
        builder.setIcon(R.drawable.launcher_icon);
        builder.setPositiveButton(R.string.button_open_browser, aboutListener);
        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();
        break;
      case DECODE_IMAGE:
    	  intent=new Intent(this,com.android.fileManager.FileManager.class);
    	  intent.putExtra(com.android.fileManager.FileManager.CURRENT_DIR, "/sdcard/");
    	  startActivityForResult(intent,this.REQUEST_IMG_PATH);
////    	  if (handler == null) {
//    	        handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
//    	      }
//    	  String imagePath="/sdcard/barcode.png";
//          Message message = Message.obtain(handler, R.id.decode_image, imagePath);
//          
//          message.sendToTarget();
    	  
    	  break;
      case DECODE_1D_BARCODE:
    	  Log.e("decode 1d", "hkba");
    		LinearLayout l=(LinearLayout)getLayoutInflater().inflate(R.layout.input_1dbarcode, null);
			ButtonOnclick buttonOnclick=new ButtonOnclick();
			builder=new AlertDialog.Builder(this);
			builder.setTitle("输入一维EAN_13条形码")
			.setView(l)
			.setPositiveButton("提交", buttonOnclick)
			.setNegativeButton("取消", buttonOnclick)
			.setCancelable(false)
			.create(); 
			builder.show();
			edit1=(EditText)l.findViewById(R.id.barcodeEdit);
    	  break;
      case OK:
    	  Intent intent1=new Intent();
    	  intent1.putExtra("type",type );
    	  if(type.equals(ISBN_PRODUCT)){
    		  intent1.putExtra("data", productName);
    		  Log.e("productName",productName);
//    		  Toast.makeText(this, productName, Toast.LENGTH_LONG).show();
    	  }
    	  else
    		  intent1.putExtra("data", data);
    	  setResult(Activity.RESULT_OK,intent1);
    	  type=null;
    	  resultCanUse=false;
    	  productName=null;
    	  finish();
    	  break;
      case CANCEL:
    	  finish();
    	  break;
      default:
        return super.onOptionsItemSelected(item);
    }
    return true;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  Log.e("解析有误", "0");
	  if (resultCode == RESULT_OK) {
		  Log.e("解析有误", "1");
      if (requestCode == HISTORY_REQUEST_CODE) {
        int itemNumber = intent.getIntExtra(Intents.History.ITEM_NUMBER, -1);
        if (itemNumber >= 0) {
          HistoryItem historyItem = historyManager.buildHistoryItem(itemNumber);
          decodeOrStoreSavedBitmap(null, historyItem.getResult());
        }
        
      }
      else if(requestCode==REQUEST_IMG_PATH){
    	  String imgPath=(String) intent.getCharSequenceExtra(com.android.fileManager.FileManager.IMG_PATH);
    	  Bitmap bit=BitmapFactory.decodeFile(imgPath);
    	  Log.e("获得路径", imgPath);
    	  if (handler == null) {
  	        handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
  	      }
  	  
        Message message = Message.obtain(handler, R.id.decode_image, bit);
        
        message.sendToTarget();
      }
      else if(requestCode==REQUEST_1D_BARCODE){
    	  
    	  
    	 Log.e("nihoa", "llls");
    	  Bitmap bit=(Bitmap)(intent.getParcelableExtra("nihao"));
    	  if(bit==null){
    		  Log.e("解析有误", "");
    		  return;
    	  }
//    	   statusView.setVisibility(View.GONE);
//    	    viewfinderView.setVisibility(View.GONE);
//    	    resultView.setVisibility(View.VISIBLE);
//
//    	    ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
//    	    barcodeImageView.setImageBitmap(bit);
    	  if (handler == null) {
    	        handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
    	      }
    	  
          Message message = Message.obtain(handler, R.id.decode_image, bit);
          Log.e("解析完毕", "");
          message.sendToTarget();
      }
    }
	  else if(resultCode==Activity.RESULT_CANCELED){
		  if(requestCode==REQUEST_1D_BARCODE){
	    	 Toast.makeText(this, "解析一维码失败！", Toast.LENGTH_LONG).show();
	      }
	  }
  }

  private void decodeOrStoreSavedBitmap(Bitmap bitmap, Result result) {
    // Bitmap isn't used yet -- will be used soon
    if (handler == null) {
      savedResultToShow = result;
    } else {
      if (result != null) {
        savedResultToShow = result;
      }
      if (savedResultToShow != null) {
        Message message = Message.obtain(handler, R.id.decode_succeeded, savedResultToShow);
        handler.sendMessage(message);
      }
      savedResultToShow = null;
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder holder) {
    if (holder == null) {
      Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
    }
    if (!hasSurface) {
      hasSurface = true;
      initCamera(holder);
    }
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder holder) {
    hasSurface = false;
  }

  @Override
  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  /**
   * A valid barcode has been found, so give an indication of success and show the results.
   *
   * @param rawResult The contents of the barcode.
   * @param barcode   A greyscale bitmap of the camera data which was decoded.
   */
//  此处显示解析的数据以及根据解析的数据生成对应的图片
  public void handleDecode(Result rawResult, Bitmap barcode) {
    inactivityTimer.onActivity();
    lastResult = rawResult;
    ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
    historyManager.addHistoryItem(rawResult, resultHandler);

    if (barcode == null) {
      // This is from history -- no saved barcode
      handleDecodeInternally(rawResult, resultHandler, null);
    } else {
      beepManager.playBeepSoundAndVibrate();
//      当结果出来的时候，在图片中高亮一条线（一维码），二维码高亮几个关键点
//      drawResultPoints(barcode, rawResult);
//      此处针对扫描出来的码的种类不同，显示不同的操作按钮
      switch (source) {
        case NATIVE_APP_INTENT:
        case PRODUCT_SEARCH_LINK:
//        	以上几种情况，将调用其他程序来处理结果
          handleDecodeExternally(rawResult, resultHandler, barcode);
          break;
        case ZXING_LINK:
          if (returnUrlTemplate == null){
            handleDecodeInternally(rawResult, resultHandler, barcode);
          } else {
            handleDecodeExternally(rawResult, resultHandler, barcode);
          }
          break;
        case NONE:
          SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
          if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
            Toast.makeText(this, R.string.msg_bulk_mode_scanned, Toast.LENGTH_SHORT).show();
            // Wait a moment or else it will scan the same barcode continuously about 3 times
            restartPreviewAfterDelay(BULK_MODE_SCAN_DELAY_MS);
          } else {
//        	  用当前程序的ui显示结果
            handleDecodeInternally(rawResult, resultHandler, barcode);
          }
          break;
      }
    }
  }

  /**
   * Superimpose a line for 1D or dots for 2D to highlight the key features of the barcode.
   *
   * @param barcode   A bitmap of the captured image.
   * @param rawResult The decoded results which contains the points to draw.
   */
  private void drawResultPoints(Bitmap barcode, Result rawResult) {
    ResultPoint[] points = rawResult.getResultPoints();
    if (points != null && points.length > 0) {
      Canvas canvas = new Canvas(barcode);
      Paint paint = new Paint();
      paint.setColor(getResources().getColor(R.color.result_image_border));
      paint.setStrokeWidth(3.0f);
      paint.setStyle(Paint.Style.STROKE);
      Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
      canvas.drawRect(border, paint);

      paint.setColor(getResources().getColor(R.color.result_points));
      if (points.length == 2) {
        paint.setStrokeWidth(4.0f);
        drawLine(canvas, paint, points[0], points[1]);
      } else if (points.length == 4 &&
                 (rawResult.getBarcodeFormat() == BarcodeFormat.UPC_A ||
                  rawResult.getBarcodeFormat() == BarcodeFormat.EAN_13)) {
        // Hacky special case -- draw two lines, for the barcode and metadata
        drawLine(canvas, paint, points[0], points[1]);
        drawLine(canvas, paint, points[2], points[3]);
      } else {
        paint.setStrokeWidth(10.0f);
        for (ResultPoint point : points) {
          canvas.drawPoint(point.getX(), point.getY(), paint);
        }
      }
    }
  }

  private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
    canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
  }

  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
	  resultCanUse=false;
	  type=null;
	  statusView.setVisibility(View.GONE);
    viewfinderView.setVisibility(View.GONE);
    resultView.setVisibility(View.VISIBLE);
    ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
    if (barcode == null) {
      barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),
          R.drawable.launcher_icon));
    } else {
      barcodeImageView.setImageBitmap(barcode);
    }

    TextView formatTextView = (TextView) findViewById(R.id.format_text_view);
    formatTextView.setText(rawResult.getBarcodeFormat().toString());

    TextView typeTextView = (TextView) findViewById(R.id.type_text_view);
    typeTextView.setText(resultHandler.getType().toString());

    DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    String formattedTime = formatter.format(new Date(rawResult.getTimestamp()));
    TextView timeTextView = (TextView) findViewById(R.id.time_text_view);
    timeTextView.setText(formattedTime);


    TextView metaTextView = (TextView) findViewById(R.id.meta_text_view);
    View metaTextViewLabel = findViewById(R.id.meta_text_view_label);
    metaTextView.setVisibility(View.GONE);
    metaTextViewLabel.setVisibility(View.GONE);
    Map<ResultMetadataType,Object> metadata = rawResult.getResultMetadata();
    if (metadata != null) {
      StringBuilder metadataText = new StringBuilder(20);
      for (Map.Entry<ResultMetadataType,Object> entry : metadata.entrySet()) {
        if (DISPLAYABLE_METADATA_TYPES.contains(entry.getKey())) {
          metadataText.append(entry.getValue()).append('\n');
        }
      }
      if (metadataText.length() > 0) {
        metadataText.setLength(metadataText.length() - 1);
        metaTextView.setText(metadataText);
        metaTextView.setVisibility(View.VISIBLE);
        metaTextViewLabel.setVisibility(View.VISIBLE);
      }
    }
    TextView supplementTextView = (TextView) findViewById(R.id.contents_supplement_text_view);
    supplementTextView.setText("");
    TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
    contentsTextView.setText("");
    CharSequence displayContents = resultHandler.getDisplayContents();
    if(ResultParser.parseResult(rawResult).getType()==ParsedResultType.TEXT){
    	
    	JSONParser parser=new JSONParser();

//		  System.out.println("=======decode=======");
//    	如果不是json格式，会报错
    	JSONObject object;
    	try {
			object = (JSONObject) parser.parse( displayContents.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			contentsTextView.setText("无法解析物品信息");
			resultCanUse=false;
    		type=null;
			return ;
		}
    	String goodsName=null;
    	goodsName=(String) object.get(AddGoodsActivity.JSON_GOODS_NAME);
    	if(object!=null&&goodsName!=null){
    		resultCanUse=true;
    		type=PRINTED_BARCODE;
    		data=displayContents.toString();
    		String content="物品名称:"+goodsName+"\n";
    		if(object.get(AddGoodsActivity.JSON_TYPE)!=null){
				content+="物品种类："+object.get(AddGoodsActivity.JSON_TYPE)+"\n";
				if(object.get(AddGoodsActivity.JSON_IMG_PATH)!=null){
					 Bitmap bit=BitmapFactory.decodeFile((String) object.get(AddGoodsActivity.JSON_IMG_PATH));
					 ImageView view=(ImageView)findViewById(R.id.capturegoodsImageView);
					 view.setImageBitmap(bit);
					 view.setVisibility(View.VISIBLE);
				}
				contentsTextView.setText(content);
			}
    	}
    	else{
    		
			resultCanUse=false;
    		type=null;
    		contentsTextView.setText("无法解析物品信息");
			return ;
    	}
    }
    else if(ResultParser.parseResult(rawResult).getType()==ParsedResultType.ISBN||ResultParser.parseResult(rawResult).getType()==ParsedResultType.PRODUCT){
    	    contentsTextView.setText(displayContents);
    	    resultCanUse=true;
    	    type=ISBN_PRODUCT;
    }
    else{
    	contentsTextView.setText("无法解析物品信息");
    	return;
    }
    // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
//    int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
//    contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);

   
    supplementTextView.setOnClickListener(null);
    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
        PreferencesActivity.KEY_SUPPLEMENTAL, true)) {
      SupplementalInfoRetriever.maybeInvokeRetrieval(supplementTextView,
                                                     resultHandler.getResult(),
                                                     handler,
                                                     historyManager,
                                                     this);
    }

    int buttonCount = resultHandler.getButtonCount();
    ViewGroup buttonView = (ViewGroup) findViewById(R.id.result_button_view);
    buttonView.requestFocus();
    for (int x = 0; x < ResultHandler.MAX_BUTTON_COUNT; x++) {
      TextView button = (TextView) buttonView.getChildAt(x);
      if (x < buttonCount) {
        button.setVisibility(View.VISIBLE);
        button.setText(resultHandler.getButtonText(x));
        button.setOnClickListener(new ResultButtonListener(resultHandler, x));
      } else {
        button.setVisibility(View.GONE);
      }
    }

   
  }

  // Briefly show the contents of the barcode, then handle the result outside Barcode Scanner.
  private void handleDecodeExternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
    viewfinderView.drawResultBitmap(barcode);

    // Since this message will only be shown for a second, just tell the user what kind of
    // barcode was found (e.g. contact info) rather than the full contents, which they won't
    // have time to read.
    statusView.setText(getString(resultHandler.getDisplayTitle()));

    if (copyToClipboard && !resultHandler.areContentsSecure()) {
      ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
      clipboard.setText(resultHandler.getDisplayContents());
    }

    if (source == IntentSource.NATIVE_APP_INTENT) {
      
      // Hand back whatever action they requested - this can be changed to Intents.Scan.ACTION when
      // the deprecated intent is retired.
      Intent intent = new Intent(getIntent().getAction());
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
      intent.putExtra(Intents.Scan.RESULT, rawResult.toString());
      intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
      byte[] rawBytes = rawResult.getRawBytes();
      if (rawBytes != null && rawBytes.length > 0) {
        intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
      }
      Map<ResultMetadataType,?> metadata = rawResult.getResultMetadata();
      if (metadata != null) {
        Integer orientation = (Integer) metadata.get(ResultMetadataType.ORIENTATION);
        if (orientation != null) {
          intent.putExtra(Intents.Scan.RESULT_ORIENTATION, orientation.intValue());
        }
        String ecLevel = (String) metadata.get(ResultMetadataType.ERROR_CORRECTION_LEVEL);
        if (ecLevel != null) {
          intent.putExtra(Intents.Scan.RESULT_ERROR_CORRECTION_LEVEL, ecLevel);
        }
        Iterable<byte[]> byteSegments = (Iterable<byte[]>) metadata.get(ResultMetadataType.BYTE_SEGMENTS);
        if (byteSegments != null) {
          int i = 0;
          for (byte[] byteSegment : byteSegments) {
            intent.putExtra(Intents.Scan.RESULT_BYTE_SEGMENTS_PREFIX + i, byteSegment);
            i++;
          }
        }
      }
      sendReplyMessage(R.id.return_scan_result, intent);
      
    } else if (source == IntentSource.PRODUCT_SEARCH_LINK) {
      
      // Reformulate the URL which triggered us into a query, so that the request goes to the same
      // TLD as the scan URL.
      int end = sourceUrl.lastIndexOf("/scan");
      String replyURL = sourceUrl.substring(0, end) + "?q=" + resultHandler.getDisplayContents() + "&source=zxing";      
      sendReplyMessage(R.id.launch_product_query, replyURL);
      
    } else if (source == IntentSource.ZXING_LINK) {
      
      // Replace each occurrence of RETURN_CODE_PLACEHOLDER in the returnUrlTemplate
      // with the scanned code. This allows both queries and REST-style URLs to work.
      if (returnUrlTemplate != null) {
        String codeReplacement = String.valueOf(resultHandler.getDisplayContents());
        try {
          codeReplacement = URLEncoder.encode(codeReplacement, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          // can't happen; UTF-8 is always supported. Continue, I guess, without encoding
        }
        String replyURL = returnUrlTemplate.replace(RETURN_CODE_PLACEHOLDER, codeReplacement);
        sendReplyMessage(R.id.launch_product_query, replyURL);
      }
      
    }
  }
  
  private void sendReplyMessage(int id, Object arg) {
    Message message = Message.obtain(handler, id, arg);
    long resultDurationMS = getIntent().getLongExtra(Intents.Scan.RESULT_DISPLAY_DURATION_MS,
                                                     DEFAULT_INTENT_RESULT_DURATION_MS);
    if (resultDurationMS > 0L) {
      handler.sendMessageDelayed(message, resultDurationMS);
    } else {
      handler.sendMessage(message);
    }
  }

  /**
   * We want the help screen to be shown automatically the first time a new version of the app is
   * run. The easiest way to do this is to check android:versionCode from the manifest, and compare
   * it to a value stored as a preference.
   */
  private boolean showHelpOnFirstLaunch() {
    try {
      PackageInfo info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
      int currentVersion = info.versionCode;
      // Since we're paying to talk to the PackageManager anyway, it makes sense to cache the app
      // version name here for display in the about box later.
      this.versionName = info.versionName;
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      int lastVersion = prefs.getInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, 0);
      if (currentVersion > lastVersion) {
        prefs.edit().putInt(PreferencesActivity.KEY_HELP_VERSION_SHOWN, currentVersion).commit();
        Intent intent = new Intent(this, HelpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        // Show the default page on a clean install, and the what's new page on an upgrade.
        String page = lastVersion == 0 ? HelpActivity.DEFAULT_PAGE : HelpActivity.WHATS_NEW_PAGE;
        intent.putExtra(HelpActivity.REQUESTED_PAGE_KEY, page);
        startActivity(intent);
        return true;
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.w(TAG, e);
    }
    return false;
  }

  private void initCamera(SurfaceHolder surfaceHolder) {
    try {
      cameraManager.openDriver(surfaceHolder);
      // Creating the handler starts the preview, which can also throw a RuntimeException.
      if (handler == null) {
        handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
      }
      decodeOrStoreSavedBitmap(null, null);
    } catch (IOException ioe) {
      Log.w(TAG, ioe);
      displayFrameworkBugMessageAndExit();
    } catch (RuntimeException e) {
      // Barcode Scanner has seen crashes in the wild of this variety:
      // java.?lang.?RuntimeException: Fail to connect to camera service
      Log.w(TAG, "Unexpected error initializing camera", e);
      displayFrameworkBugMessageAndExit();
    }
  }

  private void displayFrameworkBugMessageAndExit() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle(getString(R.string.app_name));
    builder.setMessage(getString(R.string.msg_camera_framework_bug));
    builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
    builder.setOnCancelListener(new FinishListener(this));
    builder.show();
  }

  public void restartPreviewAfterDelay(long delayMS) {
    if (handler != null) {
      handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
    }
    resetStatusView();
  }

  private void resetStatusView() {
    resultView.setVisibility(View.GONE);
//    显示请将条形码置于。。。的提示
    statusView.setText(R.string.msg_default_status);
    statusView.setVisibility(View.VISIBLE);
    viewfinderView.setVisibility(View.VISIBLE);
    lastResult = null;
  }

  public void drawViewfinder() {
    viewfinderView.drawViewfinder();
  }
  
//  在此处处理图片解码失败
  public void decodeImageFailed(){
		
  }
  class ButtonOnclick implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			if(DialogInterface.BUTTON_POSITIVE==which){
				WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
			    Display display = manager.getDefaultDisplay();
			    int width = display.getWidth();
			    int height = display.getHeight();
			    int smallerDimension = width < height ? width : height;
			    smallerDimension = smallerDimension * 7 / 8;

				
				String number=edit1.getText().toString();
				Log.e("number", number);
				if(checkEAN(number)){
					Log.e("number1", number);
					Intent intent1 = new Intent("com.google.zxing.client.android.ENCODE");

			  	    intent1.putExtra("ENCODE_FORMAT", "EAN_13");
			  	    intent1.putExtra("ENCODE_DATA", number);
			  	      boolean useVCard =false;
			  	    QRCodeEncoder qrCodeEncoder=null;
			  	  Bitmap bitmap=null;
			  	    try{
			  	      qrCodeEncoder = new QRCodeEncoder(CaptureActivity.this, intent1, smallerDimension, useVCard);
			  	      bitmap = qrCodeEncoder.encodeAsBitmap();
			  	    }
			  	    catch(Exception e){
			  	    	
			  	    }
			  	    
			  	      
			  	      if (bitmap == null) {
			  	        Log.w(TAG, "Could not encode barcode");
//			  	        showErrorMessage(R.string.msg_encode_contents_failed);
			  	        qrCodeEncoder = null;
			  	        return;
			  	      }
			  	    if (handler == null) {
		    	        handler = new CaptureActivityHandler(CaptureActivity.this, decodeFormats, characterSet, cameraManager);
		    	      }
		    	  
		          Message message = Message.obtain(handler, R.id.decode_image, bitmap);
		          Log.e("解析完毕", "");
		          message.sendToTarget();
			  	    
				}
				else{
					Toast.makeText(getApplicationContext(), message1, Toast.LENGTH_SHORT).show();
				}
			}
			
			else{
				dialog.dismiss();
			}
		}
		
		boolean checkEAN(String code){
			if(code.length()!=13)
				return false;
			for(int i=0;i<13;i++){
				if(code.charAt(i)<'0'||code.charAt(i)>'9')
					return false;
			}
			return true;
		}
		
	}
  
  
}




