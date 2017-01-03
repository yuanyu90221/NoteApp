package com.note.vo;

import java.util.ArrayList;
import java.util.List;

import com.example.searchviewdm.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoteListAdapter extends ArrayAdapter<NoteVO>{

	/**
	 * field: layout resource
	 */
	private int resource;
	
	/**
	 * field: content List
	 */
	private List<NoteVO> noteList;
	
	private List<String> deleteIds = new ArrayList<String>();
	/**
	 * @description constructor 
	 * 
	 * @param context
	 * @param resource
	 * @param noteList
	 */
	public NoteListAdapter(Context context, int resource, List<NoteVO> noteList) {
		super(context, resource, noteList);
		
		this.resource = resource;
		
		this.noteList = noteList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout itemView;
		
		final NoteVO note = getItem(position);
		
		if (convertView == null) {
			// 建立項目畫面元件
			itemView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(resource, itemView, true);
		} else {
			itemView = (LinearLayout) convertView;
		}
		
		// 讀取記事顏色、已選擇、標題與日期時間元件
		TextView title = (TextView) itemView.findViewById(R.id.title);
		TextView createDate = (TextView) itemView.findViewById(R.id.create_date);
		TextView createTime = (TextView) itemView.findViewById(R.id.create_time);
		CheckBox deleteChk = (CheckBox) itemView.findViewById(R.id.deleteChk);
		
		// 
		title.setText(note.getTitle());
		createDate.setText(note.getFormatCreateDate());
		createTime.setText(note.getFormatCreateTime());
		int visibility = note.isDeletable() == true ? View.VISIBLE: View.GONE;
		deleteChk.setVisibility(visibility);
		deleteChk.setChecked(false);
		deleteChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String id = Long.toString(note.get_id());
				System.out.println("NOTE: "+ id);
				
				int pos = deleteIds.indexOf(id); 
				System.out.println("NOTE: pos = "+ pos);
				if(isChecked){
					if(pos==-1){
						deleteIds.add(id);
					}
				}
				else{
					if(pos!=-1){
						deleteIds.remove(id);
					}
				}
			}
		});
		return itemView;
	}
	
	/**
	 * @description 設定指定編號的記事資料
	 * 
	 * @param index
	 * @param note
	 */
	public void set(int index, NoteVO note){
		if(index >= 0 && index <= noteList.size()){
			noteList.set(index, note);
			notifyDataSetChanged();
		}
	}
	
	/**
	 * @description 讀取指定編號的記事資料
	 * 
	 * @param index
	 * @return
	 */
	public NoteVO get(int index){
		return noteList.get(index);
	}
	
	public void clearDeleteIds(){
		deleteIds.clear();
	}
	
	public List<String> getDeleteIds(){
		return deleteIds;
	}
}