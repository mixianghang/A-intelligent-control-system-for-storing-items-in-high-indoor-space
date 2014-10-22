package com.android.fileManager;

import java.util.List;

import com.house.control.R;



import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toast;
//ʹ��BaseAdapter���洢ȡ�õ��ļ�
public class IconifiedTextListAdapter extends BaseAdapter
{
	private Context				mContext	= null;
	// ������ʾ�ļ����б�
	private List<IconifiedText>	mItems		= null;
	private LayoutInflater mInflater        = null;
	private String fname = null;
	private int i;
//	private boolean judgemcc;
	static int enNum = 0;
	static int deNum = 0;
	public IconifiedTextListAdapter(Context context,List<IconifiedText> list)
	{
		mContext = context;
		mItems = list;
		mInflater = LayoutInflater.from(context); 
	}
	private class ViewHolder {
		protected TextView mText;
		protected CheckBox mBox;
		protected ImageView	mIcon;
	}
	private class ViewHolder2 {
		protected TextView mText;
		protected ImageView	mIcon;
	}

	//���һ�һ���ļ���
	public void addItem(IconifiedText it) { mItems.add(it); }
	//�����ļ��б�
	public void setListItems(List<IconifiedText> lit) { mItems = lit; }
	//�õ��ļ�����Ŀ,�б�ĸ���
	@Override
	public int getCount() { return mItems.size(); }
	//�õ�һ���ļ�
	@Override
	public Object getItem(int position) { return mItems.get(position); }
	//�ܷ�ȫ��ѡ��
	public boolean areAllItemsSelectable() { return false; }
	//�ж�ָ���ļ��Ƿ�ѡ��
	public boolean isSelectable(int position) 
	{ 
		return mItems.get(position).isSelectable();
	}
	//�õ�һ���ļ���ID
	@Override
	public long getItemId(int position) { return position; }
	//��дgetView����������һ��IconifiedTextView�������Զ�����ļ����֣�����
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;	
		
		if(mItems.get(position).isfile()) {
			view = mInflater.inflate(R.layout.file_linearview, null); 
			final ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.mText = (TextView) view.findViewById(R.id.mText);
			fname = mItems.get(position).getText();
			viewHolder.mText.setText(fname);
			i = fname.lastIndexOf(".");
			if(i > 0){
				String endString = fname.substring(i);
				if(endString.equals(".mcc")) {
					viewHolder.mText.setTextColor(Color.RED);
				}
			}
								
			viewHolder.mIcon = (ImageView) view.findViewById(R.id.mIcon);
			viewHolder.mIcon.setImageDrawable(mItems.get(position).getIcon());
								
			viewHolder.mBox = (CheckBox) view.findViewById(R.id.mBox);
			viewHolder.mBox.setFocusableInTouchMode(false);                            
			viewHolder.mBox.setFocusable(false);
//			viewHolder.mBox.setOnClickListener(
//					new CheckBox.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						mItems.get(position).setSelected(viewHolder.mBox.isChecked());
//						judgemcc = false;
//						fname = mItems.get(position).getText();
//						viewHolder.mText.setText(fname);
//						i = fname.lastIndexOf(".");
//						if(i > 0){
//							String endString = fname.substring(i);
//							if(endString.equals(".mcc")) {
//								judgemcc = true;
//								if(!((CheckBox)v).isChecked()) {
//									deNum--;
//								}
//							}
//						}
////						if(!judgemcc) {
////							if(!((CheckBox)v).isChecked()) {
////								enNum--;
//
////							}
////						}
//						if(enNum == 0) FileManager.EncryptedButton.setEnabled(false);
//						if(deNum == 0) FileManager.DecryptedButton.setEnabled(false);
//						}
//					});
			viewHolder.mBox.setOnCheckedChangeListener(
				new CheckBox.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
					mItems.get(position).setSelected(viewHolder.mBox.isChecked());
//					judgemcc = false;
//					fname = mItems.get(position).getText();
//					viewHolder.mText.setText(fname);
//					i = fname.lastIndexOf(".");
//					if(i > 0){
//						String endString = fname.substring(i);
//						if(endString.equals(".mcc")) {
//							judgemcc = true;
//							if(!viewHolder.mBox.isChecked()) FileManager.DecryptedButton.setEnabled(false);
//							else {
//								FileManager.DecryptedButton.setEnabled(true);
//							}
//							
//						}
//					}
//					if(!judgemcc) {
//						if(!viewHolder.mBox.isChecked()) FileManager.EncryptedButton.setEnabled(false);
//						else {
//							FileManager.EncryptedButton.setEnabled(true);
//						}
//						
//					}
////					if(enNum == 0) FileManager.EncryptedButton.setEnabled(false);
////					if(deNum == 0) FileManager.DecryptedButton.setEnabled(false);
////					if(enNum != 0) FileManager.EncryptedButton.setEnabled(true);
////					if(deNum != 0) FileManager.DecryptedButton.setEnabled(true);
					}
				});
			viewHolder.mBox.setChecked(mItems.get(position).isSelected());
		}
		else {
			view = mInflater.inflate(R.layout.file_view2, null);  
			final ViewHolder2 viewHolder2 = new ViewHolder2();
							
			viewHolder2.mText = (TextView) view.findViewById(R.id.mText);
			String fname = mItems.get(position).getText();
			viewHolder2.mText.setText(fname);
			int i = fname.lastIndexOf(".");
			if(i > 0){
				String endString = fname.substring(i);
				if(endString.equals(".mcc")) {
					viewHolder2.mText.setTextColor(Color.RED);
				}
			}
							
			viewHolder2.mIcon = (ImageView) view.findViewById(R.id.mIcon);
			viewHolder2.mIcon.setImageDrawable(mItems.get(position).getIcon());
		}
		return view;
	}
}
