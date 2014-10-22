package com.android.fileManager;

import android.graphics.drawable.Drawable;

public class IconifiedText implements Comparable<IconifiedText>
{
	/* �ļ��� */
	private String		mText		= "";
	/* �ļ���ͼ��ICNO */
	private Drawable	mIcon		= null;
	/* �ܷ�ѡ�� */
	private boolean	mSelectable	= true;
	/* �Ƿ�ѡ�� */
	private boolean selected;
	private boolean file;
	public IconifiedText(String text, Drawable bullet, boolean judge)
	{
		mIcon = bullet;
		mText = text;
		selected = false;
		file = judge;
	}
	//�Ƿ����ѡ��
	public boolean isSelectable()
	{
		return mSelectable;
	}
	//�����Ƿ����ѡ��
	public void setSelectable(boolean selectable)
	{
		mSelectable = selectable;
	}
	//�õ��ļ���
	public String getText()
	{
		return mText;
	}
	//�����ļ���
	public void setText(String text)
	{
		mText = text;
	}
	//����ͼ��
	public void setIcon(Drawable icon)
	{
		mIcon = icon;
	}
	//�õ�ͼ��
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

	//�Ƚ��ļ����Ƿ���ͬ
	public int compareTo(IconifiedText other)
	{
		if (this.mText != null)
			return this.mText.compareTo(other.getText());
		else
			throw new IllegalArgumentException();
	}
}
