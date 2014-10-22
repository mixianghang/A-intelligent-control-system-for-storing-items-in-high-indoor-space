package com.android.fileManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.house.control.R;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class FileManager extends ListActivity
{
	private List<IconifiedText>	directoryEntries = new ArrayList<IconifiedText>();
	private File				currentDirectory = new File("/sdcard/");
	private File 				myTmpFile 		 = null;
	private int 				myTmpOpt		 = -1;
	private String fname;
	private int i;
	private boolean judgeen;
	private boolean judgede;
	private Intent intent;
	
	public static final String CURRENT_DIR="current_dir";
	public static final String IMG_PATH="img_path";
	//建立一个MIME类型与文件后缀名的匹配表
	private final String[][] MIME_MapTable={
		//{后缀名，	MIME类型}
		{".3gp",	"video/3gpp"},
		{".apk",	"application/vnd.android.package-archive"},
		{".asf",	"video/x-ms-asf"},
		{".avi",	"video/x-msvideo"},
		{".bin",	"application/octet-stream"},
		{".bmp",  	"image/bmp"},
		{".c",		"text/plain"},
		{".class",	"application/octet-stream"},
		{".conf",	"text/plain"},
		{".cpp",	"text/plain"},
		{".doc",	"application/msword"},
		{".exe",	"application/octet-stream"},
		{".gif",	"image/gif"},
		{".gtar",	"application/x-gtar"},
		{".gz",		"application/x-gzip"},
		{".h",		"text/plain"},
		{".htm",	"text/html"},
		{".html",	"text/html"},
		{".jar",	"application/java-archive"},
		{".java",	"text/plain"},
		{".jpeg",	"image/jpeg"},
		{".jpg",	"image/jpeg"},
		{".js",		"application/x-javascript"},
		{".log",	"text/plain"},
		{".m3u",	"audio/x-mpegurl"},
		{".m4a",	"audio/mp4a-latm"},
		{".m4b",	"audio/mp4a-latm"},
		{".m4p",	"audio/mp4a-latm"},
		{".m4u",	"video/vnd.mpegurl"},
		{".m4v",	"video/x-m4v"},	
		{".mov",	"video/quicktime"},
		{".mp2",	"audio/x-mpeg"},
		{".mp3",	"audio/x-mpeg"},
		{".mp4",	"video/mp4"},
		{".mpc",	"application/vnd.mpohun.certificate"},		
		{".mpe",	"video/mpeg"},	
		{".mpeg",	"video/mpeg"},	
		{".mpg",	"video/mpeg"},	
		{".mpg4",	"video/mp4"},	
		{".mpga",	"audio/mpeg"},
		{".msg",	"application/vnd.ms-outlook"},
		{".ogg",	"audio/ogg"},
		{".pdf",	"application/pdf"},
		{".png",	"image/png"},
		{".pps",	"application/vnd.ms-powerpoint"},
		{".ppt",	"application/vnd.ms-powerpoint"},
		{".prop",	"text/plain"},
		{".rar",	"application/x-rar-compressed"},
		{".rc",		"text/plain"},
		{".rmvb",	"audio/x-pn-realaudio"},
		{".rtf",	"application/rtf"},
		{".sh",		"text/plain"},
		{".tar",	"application/x-tar"},	
		{".tgz",	"application/x-compressed"}, 
		{".txt",	"text/plain"},
		{".wav",	"audio/x-wav"},
		{".wma",	"audio/x-ms-wma"},
		{".wmv",	"audio/x-ms-wmv"},
		{".wps",	"application/vnd.ms-works"},
		{".xml",	"text/plain"},
		{".z",		"application/x-compress"},
		{".zip",	"application/zip"},
		{"",		"*/*"}	
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle)
	{
		super.onCreate(icicle);
		setContentView(R.layout.file_main); 
		intent=this.getIntent();
		if(intent!=null){
			String currentDir=(String) intent.getCharSequenceExtra(CURRENT_DIR);
			if(currentDir!=null)
			browseTo(new File(currentDir));
			else
				browseToRoot();
		}
		else
			browseToRoot();
		
		this.setSelection(0);
	}
	//浏览文件系统的根目录
	private void browseToRoot() 
	{
		browseTo(new File("/sdcard/"));
    }
	//返回上一级目录
	private void upOneLevel()
	{
		if(this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}
	//浏览指定的目录,如果是文件则进行打开操作
	private void browseTo(final File file)
	{
		this.setTitle(file.getAbsolutePath());
		if (file.isDirectory())
		{
			this.currentDirectory = file;
			fill(file.listFiles());
		}
		else
		{
			fileOptMenu(file);
		}
	}
	private String getMIMEType(File file)
	{
		String type="*/*";
		String fName=file.getName();
		//获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if(dotIndex < 0){
			return type;
		}
		/* 获取文件的后缀名 */
		String end=fName.substring(dotIndex,fName.length()).toLowerCase();
		if(end=="")return type;
		//在MIME和文件类型的匹配表中找到对应的MIME类型。
		for(int i=0;i<MIME_MapTable.length;i++){
			if(end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	//打开指定文件
	protected void openFile(File aFile)
	{
		
		Intent intent = new Intent();
		intent.putExtra(this.IMG_PATH, aFile.getAbsolutePath());
		setResult(RESULT_OK,intent);
//		File file = new File(aFile.getAbsolutePath());
//
//		String type = getMIMEType(file);
//		// 根据不同的文件类型来打开文件
//		intent.setDataAndType(Uri.fromFile(file), type);
//		startActivity(intent);
		finish();
	}
	//这里可以理解为设置ListActivity的源
	private void fill(File[] files)
	{
		//清空列表
		this.directoryEntries.clear();

		//添加一个当前目录的选项
		this.directoryEntries.add(new IconifiedText(getString(R.string.current_dir),
				getResources().getDrawable(R.drawable.refresh), false));
		//如果不是根目录则添加上一级目录项
		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add(new IconifiedText(getString(R.string.up_one_level), 
					getResources().getDrawable(R.drawable.uponelevel), false));

		Drawable currentIcon = null;
		boolean judge = false;
		for (File currentFile : files)
		{
			if( !(currentFile.getName().equals("data") || currentFile.getName().equals("cache") 
				|| currentFile.getName().equals("sbin") || currentFile.getName().equals("config")
			|| currentFile.getName().equals("root")) ) {
			currentFile = currentFile.getAbsoluteFile();

			//判断是一个文件夹还是一个文件
			if(currentFile.exists()&&!currentFile.isHidden())
			{
			if (currentFile.isDirectory())
			{
				judge = false;
				currentIcon = getResources().getDrawable(R.drawable.folder);
			}
			else if(currentFile.isFile())
			{
			
				judge = true;
				//取得文件名
				String fileName = currentFile.getName();
				
				//根据文件名来判断文件类型，设置不同的图标
				if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingImage)))
				{
					currentIcon = getResources().getDrawable(R.drawable.image);
				}
				else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingWebText)))
				{
					currentIcon = getResources().getDrawable(R.drawable.webtext);
				}
				else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingPackage)))
				{
					currentIcon = getResources().getDrawable(R.drawable.packed);
				}
				else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingAudio)))
				{
					currentIcon = getResources().getDrawable(R.drawable.audio);
				}
				else if (checkEndsWithInStringArray(fileName, getResources().getStringArray(R.array.fileEndingVideo)))
				{
					currentIcon = getResources().getDrawable(R.drawable.video);
				}
				else
				{
					currentIcon = getResources().getDrawable(R.drawable.text);
				}


			}
			//确保只显示文件名、不显示路径如：/sdcard/111.txt就只是显示111.txt
			int currentPathStringLenght = currentFile.getAbsolutePath().lastIndexOf("/") + 1;
			this.directoryEntries.add(new IconifiedText(currentFile.getAbsolutePath().substring(currentPathStringLenght),
					currentIcon, judge));
			}
		}
			
		}
		Collections.sort(this.directoryEntries);
		IconifiedTextListAdapter itla = new IconifiedTextListAdapter(this,this.directoryEntries);
		this.setListAdapter(itla);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);
		// 取得选中的一项的文件名
		String selectedFileString = this.directoryEntries.get(position).getText();
		
		if (selectedFileString.equals(getString(R.string.current_dir)))
		{
			//如果选中的是刷新
			this.browseTo(this.currentDirectory);
		}
		else if (selectedFileString.equals(getString(R.string.up_one_level)))
		{
			//返回上一级目录
			this.upOneLevel();
		}
		else
		{
					
			File clickedFile = null;
			clickedFile = new File(this.currentDirectory.getAbsolutePath()+ "/" +this.directoryEntries.get(position).getText());
			if(clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
	//通过文件名判断是什么类型的文件
	private boolean checkEndsWithInStringArray(String checkItsEnd, 
					String[] fileEndings)
	{
		for(String aEnd : fileEndings)
		{
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}
	
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, "新建目录").setIcon(R.drawable.addfolderr);
		menu.add(0, 1, 0, "删除目录").setIcon(R.drawable.delete);
		menu.add(0, 2, 0, "粘贴文件").setIcon(R.drawable.paste);
		menu.add(0, 3, 0, "根目录").setIcon(R.drawable.goroot);
		menu.add(0, 4, 0, "上一级").setIcon(R.drawable.uponelevel);
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId())
		{
			case 0:
				Mynew();
				break;
			case 1:
				//注意：删除目录，谨慎操作，该例子提供了
				//deleteFile（删除文件）和deleteFolder（删除整个目录）
				MyDelete();
				break;
			case 2:
				MyPaste();
				break;
			case 3:
				this.browseToRoot();
				break;
			case 4:
				this.upOneLevel();
				break;
		}
		return false;
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onPrepareOptionsMenu(menu);
	}
	//粘贴操作
	public void MyPaste()
	{
		if ( myTmpFile == null )
		{
			Builder builder = new Builder(FileManager.this);
			builder.setTitle("提示");
			builder.setMessage("没有复制或剪切操作");
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();	
						}
					});
			builder.setCancelable(false);
			builder.create();
			builder.show();
		}
		else
		{
			if ( myTmpOpt == 0 )//复制操作
			{
				if(new File(GetCurDirectory()+"/"+myTmpFile.getName()).exists())
				{
					Builder builder = new Builder(FileManager.this);
					builder.setTitle("粘贴提示");
					builder.setMessage("该目录有相同的文件，是否需要覆盖？");
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									copyFile(myTmpFile,new File(GetCurDirectory()+"/"+myTmpFile.getName()));
									browseTo(new File(GetCurDirectory()));
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				}	
				else
				{
					copyFile(myTmpFile,new File(GetCurDirectory()+"/"+myTmpFile.getName()));
					browseTo(new File(GetCurDirectory()));
				}
			}
			else if(myTmpOpt == 1)//粘贴操作
			{
				if(new File(GetCurDirectory()+"/"+myTmpFile.getName()).exists())
				{
					Builder builder = new Builder(FileManager.this);
					builder.setTitle("粘贴提示");
					builder.setMessage("该目录有相同的文件，是否需要覆盖？");
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									moveFile(myTmpFile.getAbsolutePath(),GetCurDirectory()+"/"+myTmpFile.getName());
									browseTo(new File(GetCurDirectory()));
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				}	
				else
				{
					moveFile(myTmpFile.getAbsolutePath(),GetCurDirectory()+"/"+myTmpFile.getName());
					browseTo(new File(GetCurDirectory()));	
				}
			}
		}
	}
	//删除整个文件夹
	public void MyDelete()
	{
		//取得当前目录
		File tmp=new File(this.currentDirectory.getAbsolutePath());
		//跳到上一级目录
		this.upOneLevel();
		//删除取得的目录
		if ( deleteFolder(tmp) )
		{
			Builder builder = new Builder(FileManager.this);
			builder.setTitle("提示");
			builder.setMessage("删除成功");
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();	
						}
					});
			builder.setCancelable(false);
			builder.create();
			builder.show();
		}
		else 
		{
			Builder builder = new Builder(FileManager.this);
			builder.setTitle("提示");
			builder.setMessage("删除失败");
			builder.setPositiveButton(android.R.string.ok,
					new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			builder.setCancelable(false);
			builder.create();
			builder.show();
		}
		this.browseTo(this.currentDirectory);	
	}
	//新建文件夹
	public void Mynew()
	{
		final LayoutInflater factory = LayoutInflater.from(FileManager.this);
		final View dialogview = factory.inflate(R.layout.file_dialog, null);
		//设置TextView
		((TextView) dialogview.findViewById(R.id.TextView_PROM)).setText("请输入新建文件夹的名称！");
		//设置EditText
		((EditText) dialogview.findViewById(R.id.EditText_PROM)).setText("文件夹名称...");
		
		Builder builder = new Builder(FileManager.this);
		builder.setTitle("新建文件夹");
		builder.setView(dialogview);
		builder.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						String value = ((EditText) dialogview.findViewById(R.id.EditText_PROM)).getText().toString();
						if ( newFolder(value) )
						{
							Builder builder = new Builder(FileManager.this);
							builder.setTitle("提示");
							builder.setMessage("新建文件夹成功");
							builder.setPositiveButton(android.R.string.ok,
									new AlertDialog.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											//点击确定按钮之后,继续执行网页中的操作
											dialog.cancel();
										}
									});
							builder.setCancelable(false);
							builder.create();
							builder.show();
						}
						else
						{
							Builder builder = new Builder(FileManager.this);
							builder.setTitle("提示");
							builder.setMessage("新建文件夹失败");
							builder.setPositiveButton(android.R.string.ok,
									new AlertDialog.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
											//点击确定按钮之后,继续执行网页中的操作
											dialog.cancel();
										}
									});
							builder.setCancelable(false);
							builder.create();
							builder.show();	
						}
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						dialog.cancel();
					}
				});
		builder.show();
	}
	//新建文件夹
	public boolean newFolder(String file)
	{
		File dirFile = new File(this.currentDirectory.getAbsolutePath()+"/"+file);
		try
		{
			if (!(dirFile.exists()) && !(dirFile.isDirectory()))
			{
				boolean creadok = dirFile.mkdirs();
				if (creadok)
				{
					this.browseTo(this.currentDirectory);
					return true;
				}
				else
				{
					return false;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(e);
			return false;
		}
		return true;
	}
	//删除文件
    public boolean deleteFile(File file)
	{
		boolean result = false;
		if (file != null)
		{
			try
			{
				File file2 = file;
				file2.delete();
				result = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				result = false;
			}
		}
		return result;
	} 
    //删除文件夹
	public boolean deleteFolder(File folder)
	{
		boolean result = false;
		try
		{
			String childs[] = folder.list();
			if (childs == null || childs.length <= 0)
			{
				if (folder.delete())
				{
					result = true;
				}
			}
			else
			{
				for (int i = 0; i < childs.length; i++)
				{
					String childName = childs[i];
					String childPath = folder.getPath() + File.separator + childName;
					File filePath = new File(childPath);
					if (filePath.exists() && filePath.isFile())
					{
						if (filePath.delete())
						{
							result = true;
						}
						else
						{
							result = false;
							break;
						}
					}
					else if (filePath.exists() && filePath.isDirectory())
					{
						if (deleteFolder(filePath))
						{
							result = true;
						}
						else
						{
							result = false;
							break;
						}
					}
				}
				folder.delete();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	} 
	
	//处理文件，包括打开等操作
	public void fileOptMenu(final File file)
	{
		OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				if (which == 0)
				{
					openFile(file);
				}
				else if (which == 1)
				{
					//自定义一个带输入的对话框由TextView和EditText构成
					final LayoutInflater factory = LayoutInflater.from(FileManager.this);
					final View dialogview = factory.inflate(R.layout.file_rename, null);
					//设置TextView的提示信息
					((TextView) dialogview.findViewById(R.id.TextView01)).setText("重命名");
					//设置EditText输入框初始值
					((EditText) dialogview.findViewById(R.id.EditText01)).setText(file.getName());					
					
					Builder builder = new Builder(FileManager.this);
					builder.setTitle("重命名");
					builder.setView(dialogview);
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									//点击确定之后
									final String value = GetCurDirectory()+"/"+((EditText) dialogview.findViewById(R.id.EditText01)).getText().toString();
									if( (file.getName().substring(file.getName().lastIndexOf(".")).equals(".mcc"))
										&& !(value.substring(value.lastIndexOf(".")).equals(".mcc")) ) {
										Builder builder = new Builder(FileManager.this);
										builder.setTitle("重命名");
										builder.setMessage("加密文件不能更改后缀名");
										builder.setPositiveButton(android.R.string.ok,
												new AlertDialog.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														browseTo(new File(GetCurDirectory()));
														dialog.cancel();
													}
												});
										builder.setCancelable(false);
										builder.create();
										builder.show();
									}								
									else if( !(file.getName().substring(file.getName().lastIndexOf(".")).equals
											(value.substring(value.lastIndexOf(".")))) ){
										Builder builder = new Builder(FileManager.this);
										builder.setTitle("重命名");
										builder.setMessage("确定要更改后缀名吗？");
										builder.setPositiveButton(android.R.string.ok,
												new AlertDialog.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														file.renameTo(new File(value));
														browseTo(new File(GetCurDirectory()));
														dialog.cancel();
													}
												});
										builder.setNegativeButton(android.R.string.cancel,
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														dialog.cancel();
													}
												});
										builder.setCancelable(false);
										builder.create();
										builder.show();
									}
									else if(new File(value).exists())
									{
										Builder builder = new Builder(FileManager.this);
										builder.setTitle("重命名");
										builder.setMessage("文件名重复，是否需要覆盖？");
										builder.setPositiveButton(android.R.string.ok,
												new AlertDialog.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														String str2 = GetCurDirectory()+"/"+((EditText) dialogview.findViewById(R.id.EditText01)).getText().toString();
														file.renameTo(new File(str2));
														browseTo(new File(GetCurDirectory()));
													}
												});
										builder.setNegativeButton(android.R.string.cancel,
												new DialogInterface.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														dialog.cancel();
													}
												});
										builder.setCancelable(false);
										builder.create();
										builder.show();
									}
									else 
									{
										//重命名
										file.renameTo(new File(value));
										browseTo(new File(GetCurDirectory()));
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
								public void onCancel(DialogInterface dialog) {
									dialog.cancel();
								}
							});
					builder.show();
				}
				else if ( which == 2 )
				{
					Builder builder = new Builder(FileManager.this);
					builder.setTitle("删除文件");
					builder.setMessage("确定删除"+file.getName()+"？");
					builder.setPositiveButton(android.R.string.ok,
							new AlertDialog.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									if ( deleteFile(file) )
									{
										Builder builder = new Builder(FileManager.this);
										builder.setTitle("提示对话框");
										builder.setMessage("删除成功");
										builder.setPositiveButton(android.R.string.ok,
												new AlertDialog.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														//点击确定按钮之后
														browseTo(new File(GetCurDirectory()));
														dialog.cancel();												
													}
												});
										builder.setCancelable(false);
										builder.create();
										builder.show();
									}
									else 
									{
										Builder builder = new Builder(FileManager.this);
										builder.setTitle("提示对话框");
										builder.setMessage("删除失败");
										builder.setPositiveButton(android.R.string.ok,
												new AlertDialog.OnClickListener() {
													public void onClick(DialogInterface dialog, int which) {
														//点击确定按钮之后
														dialog.cancel();
													}
												});
										builder.setCancelable(false);
										builder.create();
										builder.show();	
									}
								}
							});
					builder.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							});
					builder.setCancelable(false);
					builder.create();
					builder.show();
				}
				else if ( which == 3 )//复制
				{
					//保存我们复制的文件目录
					myTmpFile = file;
					//这里我们用0表示复制操作
					myTmpOpt = 0;
				}
				else if ( which == 4 )//剪切
				{
					//保存我们复制的文件目录
					myTmpFile = file;
					//这里我们用0表示剪切操作
					myTmpOpt = 1;	 
				}

			}
		};
		//显示操作菜单
	    String[] menu={"打开","重命名","删除","复制","剪切"};
	    new AlertDialog.Builder(FileManager.this)
	        .setTitle("请选择你要进行的操作")
	        .setItems(menu,listener)
	        .show();
	}
	//得到当前目录的绝对路劲
	public String GetCurDirectory()
	{
		return this.currentDirectory.getAbsolutePath();
	}
	//移动文件
	public void moveFile(String source, String destination)
	{
		new File(source).renameTo(new File(destination));   
	}
	//复制文件
	public void copyFile(File src, File target)
	{
		InputStream in = null;
		OutputStream out = null;

		BufferedInputStream bin = null;
		BufferedOutputStream bout = null;
		try
		{
			in = new FileInputStream(src);
			out = new FileOutputStream(target);
			bin = new BufferedInputStream(in);
			bout = new BufferedOutputStream(out);

			byte[] b = new byte[8192];
			int len = bin.read(b);
			while (len != -1)
			{
				bout.write(b, 0, len);
				len = bin.read(b);
			}

		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (bin != null)
				{
					bin.close();
				}
				if (bout != null)
				{
					bout.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
