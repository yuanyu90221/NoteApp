package com.note.db;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.note.constant.Constant;
import com.note.vo.NoteListAdapter;
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
	private final Logger logger = Logger.getLogger(NoteListAdapter.class);
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
            + EXEC_TIME + " INTEGER NOT NULL,"    
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
		Layout layout = new PatternLayout();
		logger.addAppender(new ConsoleAppender(layout, ConsoleAppender.SYSTEM_OUT));
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
		String where = _ID +" in (";
		StringBuilder sb = new StringBuilder(where);
		int size = ids.length;
		int count = 0;
		for(String id: ids){
			sb.append(id);
			count++;
			if(count!=size){
				sb.append(",");
			}
			else{
				sb.append(")");
			}
		}
		return db.delete(TB_NAME, sb.toString(), null) > 0;
	}
	
	/**
	 * @description delete all note before input time
	 * 
	 * @param beforeTime
	 * @return whether have delete some note
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
	 * @description delete note by id
	 * 
	 * @param _id
	 * @return
	 */
	public boolean deleteById(long _id){
		String where = _ID +"="+Long.toString(_id);
		return db.delete(TB_NAME, where,null) > 0;
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
		String where = EXEC_TIME + " >= " + date;
		String orderBy = CREATE_TIME + " DESC";
		Cursor result = db.query(TB_NAME, null, where, null, null, null, orderBy);
		while(result.moveToNext()){
			noteList.add(getNote(result));
		}
		result.close();
		return noteList;
	}
	
	/**
	 * get notes by Title
	 * 
	 * @param title
	 * @return
	 */
	public List<NoteVO> getAllByTitle(String title){
		List<NoteVO> noteList = new ArrayList<NoteVO>();
		String where = TITLE + " LIKE '%" + title +"%'";
		String orderBy = CREATE_TIME + " DESC";
		Cursor result = db.query(TB_NAME, null, where, null, null, null, orderBy);
		while(result.moveToNext()){
			noteList.add(getNote(result));
		}
		result.close();
		return noteList;
	}
	
	/**
	 * get Notes By Criteria
	 * 
	 * @param criteria
	 * @return
	 */
	public List<NoteVO> getNotesByCriteria(NoteVO criteria){
		List<NoteVO> noteList = new ArrayList<NoteVO>();
		StringBuilder sb = new StringBuilder("SELECT * FROM ");
		sb.append(TB_NAME);
		int item_count = 0;
		if(criteria.getCreateTime() > 0 ){
			if(item_count < 1){
			  sb.append(" WHERE ");
			  sb.append(CREATE_TIME);
			  sb.append(" >= ");
			  sb.append(Long.toString(criteria.getCreateTime()));
			}
			item_count++;
		}
		if(!criteria.getTitle().equals("")){
			if(item_count < 1){
			  sb.append(" WHERE ");
			}
			else{
			  sb.append(" AND ");
			}
			sb.append(TITLE);
			sb.append(" LIKE ");
			sb.append("'%"+criteria.getTitle()+"%'");
			item_count++;
		}
		sb.append(" ORDER BY "+CREATE_TIME+" DESC");
		Cursor result = db.rawQuery(sb.toString(), null);
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
	
	/**
	 * add some sample data
	 */
	public void sample(){
		Calendar c = Calendar.getInstance();
		c.set(2015, 1-1, 22, 10, 30);
		NoteVO note1 = new NoteVO(0, c.getTimeInMillis(), c.getTimeInMillis(), "測試1", "測試內文1");
		c.set(2017, 1-1, 22, 0, 30);
		NoteVO note2 = new NoteVO(0, c.getTimeInMillis(), c.getTimeInMillis(), "測試2", "測試內文2");
		logger.info(note2);
		c.set(2016, 12-1, 31, 12, 30);
		NoteVO note3 = new NoteVO(0, c.getTimeInMillis(), c.getTimeInMillis(), "這是中文", "");
		logger.info(note3);
		c = Calendar.getInstance();
		NoteVO note4 = new NoteVO(0, c.getTimeInMillis(), c.getTimeInMillis(), "這是English", "");
		logger.info(note4);
		insert(note1);
		insert(note2);
		insert(note3);
		insert(note4);
	}
}
