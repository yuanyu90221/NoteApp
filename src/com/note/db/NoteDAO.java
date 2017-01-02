package com.note.db;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.note.constant.Constant;
import com.note.vo.NoteVO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @description 操作memoList這個table的物件
 * 
 * @author json
 *
 */
public class NoteDAO {
	
	/**
	 * TABLE NAME: memoList
	 */
	static final String TB_NAME = Constant.TB_NAME;
	
	/**
	 * field name: _id
	 */
	static final String _ID = "_id";
	
	/**
	 * field name: create_time
	 */
	static final String CREATE_TIME = "create_time";
	
	/**
	 * field name: exec_time
	 */
	static final String EXEC_TIME = "exec_time";
	
	/**
	 * field name: title
	 */
	static final String TITLE = "title";
	
	/**
	 * field name: content
	 */
	static final String CONTENT = "content";
	/**
	 * CREATE TABLE SCHEMA: 建立table的DDL
	 */
	static final String createTable = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ("
            + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CREATE_TIME + " INTEGER NOT NULL,"
            + EXEC_TIME + " INTEGER,"    
            + TITLE + " VARCHAR(50) NOT NULL,"
            + CONTENT + " TEXT)";
	
	private static SQLiteDatabase db;
	Cursor cursor;
	
	/**
	 * 建構子 ，一般的應用 都不需要改
	 * 
	 * @param context 傳入目前所在的Activity物件
	 */
	public NoteDAO(Context context){
		db = MyDBHelper.getDatabase(context);
	}
	
	/**
	 * 關閉資料庫，一般的應用都不需要改
	 */
	public void close() {
        db.close();
    }
	
	/**
	 * 
	 * 
	 * @param note
	 * @return
	 */
	public NoteVO insert(NoteVO note){
		ContentValues cv = new ContentValues();
		cv.put(TITLE, note.getTitle());
		if (note.getCreateTime() == 0) {
			Calendar c = Calendar.getInstance();
			note.setCreateTime(c.getTimeInMillis());
		}
		cv.put(CREATE_TIME, note.getCreateTime());
		if (note.getExecTime() != 0) {
			cv.put(EXEC_TIME, note.getExecTime());
		}
		cv.put(CONTENT, note.getContent());
		long _id = db.insert(TB_NAME, null, cv);
		note.set_id(_id);
		return note;
	}
	
	public boolean update(NoteVO note){
		ContentValues cv = new ContentValues();
		Calendar c = Calendar.getInstance();
		note.setCreateTime(c.getTimeInMillis());
		cv.put(CREATE_TIME, note.getCreateTime() );
		cv.put(TITLE, note.getTitle());
		cv.put(CONTENT, note.getContent());
		if (note.getExecTime() != 0) {
			cv.put(EXEC_TIME, note.getExecTime());
		}
		String where = _ID +"="+note.get_id();
		return db.update(TB_NAME, cv, where, null) > 0;
	}
	
	/**
	 * @description delete all note in ids
	 * 
	 * @param ids String array of id
	 * @return whether have delete some note
	 */
	public boolean batchDelete(String[] ids){
		String where = _ID + "=?";
		return db.delete(TB_NAME, where, ids) > 0;
	}
	
	/**
	 * @description delete all note before input time
	 * 
	 * @param beforeTime
	 * @return
	 */
	public boolean deleteBefore(long beforeTime){
		String where = CREATE_TIME + "<" + beforeTime;
		return db.delete(TB_NAME, where, null) > 0;
	}
	
	/**
	 * @description delete all note from table
	 * 
	 * @return delete number
	 */
	public int deleteAll(){
		return db.delete(TB_NAME, null, null);
	}
	
	/**
	 * @description get all the notes
	 * 
	 * @return
	 */
	public List<NoteVO> getAll(){
		List<NoteVO> noteList = new ArrayList<NoteVO>();
		String orderBy = CREATE_TIME + " DESC";
		Cursor cursor = db.query(TB_NAME, null, null, null, null, null, orderBy);
		while(cursor.moveToNext()){
			noteList.add(getNote(cursor));
		}
		cursor.close();
		return noteList;
	}
	
	/**
	 * @description get the note by id
	 * 
	 * @param _id
	 * @return
	 */
	public NoteVO getById(long _id){
		NoteVO note = null;
		String where = _ID + "=" + _id;
		Cursor result = db.query(
                TB_NAME, null, where, null, null, null, null, null);
		if(result.moveToFirst()){
			note = getNote(result);
		}
		result.close();
		return note;
	}
	
	/**
	 * @description get all the note after date
	 * 
	 * @param date
	 * @return all the note after date
	 */
	public List<NoteVO> getAllByDate(long date){
		List<NoteVO> noteList = new ArrayList<NoteVO>();
		String where = CREATE_TIME + ">=" + date;
		String orderBy = CREATE_TIME + " DESC";
		Cursor result = db.query(
                TB_NAME, null, where, null, null, null, null, orderBy);
		while(result.moveToNext()){
			noteList.add(getNote(result));
		}
		result.close();
		return noteList;
	}
	
	/**
	 * process cursor to NoteVO
	 * 
	 * @param cursor
	 * @return
	 */
	public NoteVO getNote(Cursor cursor){
		NoteVO note = new NoteVO();
		note.set_id(cursor.getLong(0));
		note.setCreateTime(cursor.getLong(cursor.getColumnIndex(CREATE_TIME)));
		note.setExecTime(cursor.getLong(cursor.getColumnIndex(EXEC_TIME)));
		note.setTitle(cursor.getString(3));
		note.setContent(cursor.getString(4));
		
		return note;
	}
	
	public void sample(){
		Calendar c = Calendar.getInstance();
		c.set(2015, 1-1, 22, 10, 30);
		NoteVO note1 = new NoteVO(0, c.getTimeInMillis(), 0, "測試1", "");
		c.set(2017, 1-1, 22, 0, 30);
		NoteVO note2 = new NoteVO(0, c.getTimeInMillis(), 0, "測試2", "");
		System.out.println(note2);
		c.set(2016, 12-1, 31, 12, 30);
		NoteVO note3 = new NoteVO(0, c.getTimeInMillis(), 0, "這是中文", "");
		System.out.println(note3);
		c = Calendar.getInstance();
		NoteVO note4 = new NoteVO(0, c.getTimeInMillis(), 0, "這是English", "");
		System.out.println(note4);
		insert(note1);
		insert(note2);
		insert(note3);
		insert(note4);
	}
}
