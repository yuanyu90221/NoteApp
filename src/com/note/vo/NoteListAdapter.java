package com.note.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

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

/**
 * A self-made Adapter to render NoteList
 * 
 * @author json
 *
 */
public class NoteListAdapter extends ArrayAdapter<NoteVO>{

	private final Logger logger = Logger.getLogger(NoteListAdapter.class);
	

	/**
	 * field: layout resource
	 */
	private int resource;
	
	/**
	 * field: content List
	 */
	private List<NoteVO> noteList;
	
	/**
	 * field: delete List
	 */
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
		Layout layout = new PatternLayout();
		logger.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
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
		logger.info("NOTE: position = "+ position+", isCheck = " + note.isChecked());
		deleteChk.setChecked(note.isChecked());
		deleteChk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				String id = Long.toString(note.get_id());
				logger.info("NOTE: id = "+ id);
				
				int pos = deleteIds.indexOf(id); 
				logger.info("NOTE: pos = "+ pos+", isCheck = " + note.isChecked());
				if(isChecked){
					if(pos==-1){
						deleteIds.add(id);
						note.setChecked(true);
					}
				}
				else{
					if(pos!=-1){
						deleteIds.remove(id);
						note.setChecked(false);

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
	
	/**
	 * clear delete list
	 */
	public void clearDeleteIds(){
		deleteIds.clear();
	}
	
	/**
	 * return ids to delete
	 * 
	 * @return
	 */
	public List<String> getDeleteIds(){
		return deleteIds;
	}
}
