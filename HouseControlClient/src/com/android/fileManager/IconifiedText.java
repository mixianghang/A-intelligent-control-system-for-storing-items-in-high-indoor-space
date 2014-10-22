package com.android.fileManager;

import android.graphics.drawable.Drawable;

public class IconifiedText implements Comparable<IconifiedText>
{
	/* 文件名 */
	private String		mText		= "";
	/* 文件的图标ICNO */
	private Drawable	mIcon		= null;
	/* 能否选中 */
	private boolean	mSelectable	= true;
	/* 是否选中 */
	private boolean selected;
	private boolean file;
	public IconifiedText(String text, Drawable bullet, boolean judge)
	{
		mIcon = bullet;
		mText = text;
		selected = false;
		file = judge;
	}
	//是否可以选中
	public boolean isSelectable()
	{
		return mSelectable;
	}
	//设置是否可用选中
	public void setSelectable(boolean selectable)
	{
		mSelectable = selectable;
	}
	//得到文件名
	public String getText()
	{
		return mText;
	}
	//设置文件名
	public void setText(String text)
	{
		mText = text;
	}
	//设置图标
	public void setIcon(Drawable icon)
	{
		mIcon = icon;
	}
	//得到图标
	public Drawable getIcon()
	{
		return mIcon;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public boolean isfile() {
//		if(mText.equals("init.rc"))
//			file = true;
//		else if(mText.equals("sbin")||mText.equals("sqlite_stmt_journals")||mText.equals("system"))
//			file = false;
		return file;
	}
	public void setfile(boolean file) {
		this.file = file;
	}

	//比较文件名是否相同
	public int compareTo(IconifiedText other)
	{
		if (this.mText != null)
			return this.mText.compareTo(other.getText());
		else
			throw new IllegalArgumentException();
	}
}
