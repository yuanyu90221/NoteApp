package com.note.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

/**
 * @description 用來傳遞Note內容的物件
 * 
 * @author json
 *
 */
public class NoteVO implements Serializable{
	
	/**
	 * field SID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * field :id
	 */
	private long _id;
	
	/**
	 * field: createTime
	 */
	private long createTime;
	
	/**
	 * field: execTime 有可能沒有
	 */
	private long execTime = 0;
	

	/**
	 * field: title
	 */
	private String title;
	
	/**
	 * field: content
	 */
	private String content = "";
	
	/**
	 * field: deletable
	 */
	private boolean deletable = false;
	
	private boolean isChecked = false;
	
	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public long get_id() {
		return _id;
	}

	public void set_id(long _id) {
		this._id = _id;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getExecTime() {
		return execTime;
	}

	public void setExecTime(long execTime) {
		this.execTime = execTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public NoteVO(long _id, long createTime, long execTime, String title, String content) {
		
		this._id = _id;
		this.createTime = createTime;
		this.execTime = execTime;
		this.title = title;
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "NoteVO [_id=" + _id + ", createTime=" + createTime + ", execTime=" + execTime + ", title=" + title
				+ ", content=" + content + "]";
	}

	public NoteVO(){
		this._id = -1;
		this.createTime = 0;
		this.execTime = 0;
		this.title = "";
		this.content = "";
	}
	
	public String getFormatCreateTime(){
		
		return String.format(Locale.getDefault(), "%tR", new Date(createTime));
	}
	
	public String getFormatCreateDate(){

		return String.format(Locale.getDefault(), "%tF", new Date(createTime));
		
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof NoteVO == false){
			return false;
		}
		NoteVO compareObj = (NoteVO) o;
		return title.equals(compareObj.getTitle());
	}
}
