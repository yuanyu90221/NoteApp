package com.note.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @description 用來建立Sqlite DB connection的物件
 * 
 * @author yuanyu
 *
 */
public class MyDBHelper extends SQLiteOpenHelper {
	
    /**
     * SQLite操作物件
     */
    private static SQLiteDatabase database;
    /**
     * DB NAME : memo
     */
    public static final String DB_NAME = "memo";
	
    /**
     * 用來確保相容性的板號
     */
    public static final int VERSION = 1;
    
	/**
	 * 建構子
	 * 
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public MyDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	
    /**
     * @description 用來取得Connection的method
     * 
     * @param context 目前所在的Activity
     * @return SQLiteDatabase 用來取得Connection的物件
     */
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DB_NAME, 
                    null, VERSION).getWritableDatabase();
        }
 
        return database;
    }
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// create DB schema
		db.execSQL(NoteDAO.createTable); 	 
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 // update DB schema
		db.execSQL("DROP TABLE IF EXISTS "+NoteDAO.TB_NAME);
		
		onCreate(db);
	}

}
