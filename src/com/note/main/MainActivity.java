package com.note.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.searchviewdm.R;
import com.note.db.NoteDAO;
import com.note.vo.NoteListAdapter;
import com.note.vo.NoteVO;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @description 主要查詢畫面的Activity
 * 
 * @author json
 *
 */
public class MainActivity extends Activity implements OnClickListener,OnItemClickListener{

	private ListView lv;
	private NoteDAO noteDAO = null;
	NoteListAdapter noteListAdapter;
	List<NoteVO> noteList = null;
	private EditText inputSearch;
	Button deleteBatchBtn, cancelDeleteAllBtn, returnBtn;
	ImageButton searchBtn;
	LinearLayout special_query;
	TextView filterText;
	Toast tos;
	// 用來確認是否可以刪除的flag
	boolean changeDeletable = false;
    List<String> idsToDelete = new ArrayList<String>();
    // 設定公用calendar變數
    Calendar m_Calendar = Calendar.getInstance();
    // 設定datepicker 用來監聽設定時間之後的動作
	DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			m_Calendar.set(Calendar.YEAR, year);
			m_Calendar.set(Calendar.MONTH, monthOfYear);
			m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			doDateSearch(m_Calendar);
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        // bind 每個元件到ui 
		lv = (ListView) findViewById(R.id.listitem);
		inputSearch = (EditText) findViewById(R.id.searchBar);
		deleteBatchBtn = (Button) findViewById(R.id.deleteBatchBtn);
		cancelDeleteAllBtn = (Button) findViewById(R.id.cancelDeletebtn);
		deleteBatchBtn.setOnClickListener(this);
		cancelDeleteAllBtn.setOnClickListener(this);
		// 設定顯示 tos
		tos = Toast.makeText(this, "", Toast.LENGTH_LONG);
		// 設定查詢ui以及button
		searchBtn = (ImageButton) findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		special_query = (LinearLayout) findViewById(R.id.specical_query);
		returnBtn = (Button) findViewById(R.id.returnBtn);
		returnBtn.setOnClickListener(this);
		filterText = (TextView) findViewById(R.id.filerText);
		// 設定初始化 ListViewAdapter
		noteDAO = new NoteDAO(getApplicationContext());
		noteList = noteDAO.getAll();
		noteListAdapter = new NoteListAdapter(this, R.layout.notelist, noteList);
		noteListAdapter.setNotifyOnChange(true);
		lv.setAdapter(noteListAdapter);
		lv.setOnItemClickListener(this);
		// 設定onTextWatcher
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				doSearch();
				resetUnDelete();
				special_query.setVisibility(View.GONE);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		resetUnDelete();
		special_query.setVisibility(View.GONE);
		switch (id) {
		case R.id.action_delete_batch:
			// 打開 刪除的checkbox
			if(noteList.size()> 0){
				setDeleteStatus();
			}
			else{
				tos.setText("There is no notes to delete!");
				tos.show();
			}
			return true;
		case R.id.action_add:
			// TODO :新增 note 邏輯
			return true;
		case R.id.action_query_todos:
			// query todos 邏輯
			filterText.setText(getResources().getString(R.string.query_todos));
			Calendar c = Calendar.getInstance();
			doDateSearch(c);
			return true;
		case R.id.action_query_after_date:
			// add pick date logic
             filterText.setText(getResources().getString(R.string.query_after_date));
			 // 設定DatePickerDialog樣式
             DatePickerDialog dialog = new DatePickerDialog(
											this, 
											datepicker, 
											m_Calendar.get(Calendar.YEAR), 
											m_Calendar.get(Calendar.MONTH),
											m_Calendar.get(Calendar.DAY_OF_MONTH));
			// 顯示dialog直到 date被選取
            dialog.show();
			 
            return true;
		case R.id.action_sample:
			// 新增測試資料 邏輯
			noteDAO = new NoteDAO(getApplicationContext());
			noteDAO.sample();
			noteList.clear();
			noteList.addAll(noteDAO.getAll());
			noteListAdapter.notifyDataSetChanged();
			return true;
		case R.id.action_delete_all:
			// delete all notes
			noteDAO = new NoteDAO(getApplicationContext());
			noteDAO.deleteAll();
			noteList.clear();
			noteListAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	/**
	 * 修改是否顯示checkbox
	 * 
	 * @param deletable
	 */
	private void changeDeleteStatus(boolean deletable){
		for(NoteVO note: noteList){
			note.setDeletable(deletable);
		}
		noteListAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Resume");
		// 更新畫面List
		doSearch();
		resetUnDelete();
		special_query.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.deleteBatchBtn:
				// add process to delete batch
				System.out.println(noteListAdapter.getDeleteIds().size());
				idsToDelete = noteListAdapter.getDeleteIds();
				String[] ids = idsToDelete.toArray(new String[0]);
				System.out.println("Note: before delete!");
				noteDAO.batchDelete(ids);
				doSearch();
				resetUnDelete();
				break;
			case R.id.cancelDeletebtn:
				// 回復未刪除狀態
				resetUnDelete();
				doSearch();
				break;
			case R.id.searchBtn:
				// 做基本查詢
				doSearch();
				break;
			case R.id.returnBtn:
				// 做基本查詢 回復基本查詢狀態
				doSearch();
				special_query.setVisibility(View.GONE);
				break;
		}
		
	}
	
	/**
	 * back to undelete status
	 */
	public void resetUnDelete(){
		changeDeletable = false;
		changeDeleteStatus(changeDeletable);
		deleteBatchBtn.setVisibility(View.GONE);
		cancelDeleteAllBtn.setVisibility(View.GONE);
		noteListAdapter.clearDeleteIds();
	}
	
	/**
	 * set to onDelete status
	 */
	public void setDeleteStatus(){
		changeDeletable = true;
		changeDeleteStatus(changeDeletable);
		deleteBatchBtn.setVisibility(View.VISIBLE);
		cancelDeleteAllBtn.setVisibility(View.VISIBLE);
		noteListAdapter.clearDeleteIds();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//TODO: 新增update note 邏輯
		NoteVO note = noteList.get(position);	
		System.out.println("NOTE: "+ note);
		
	}
	
	/**
	 * do default Search
	 */
	public void doSearch(){
		// 取得基本查詢的值
		String searchText = inputSearch.getText().toString().trim();
		System.out.println("searchText: "+ searchText);
		noteList.clear();
		if(searchText.length() > 0){
			noteList.addAll(noteDAO.getAllByTitle(searchText));
		}
		else{
			noteList.addAll(noteDAO.getAll());
		}
		noteListAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 執行用執行時間來選取記事
	 * 
	 * @param c
	 */
	public void doDateSearch(Calendar c){
		String queryTitle = inputSearch.getText().toString().trim();
	    noteList.clear();
	    if(queryTitle.length() == 0){
	    	noteList.addAll(noteDAO.getAllByDate(c.getTimeInMillis()));
	    }
	    else{
	    	NoteVO criteria = new NoteVO();
	    	criteria.setTitle(queryTitle);
	    	criteria.setCreateTime(c.getTimeInMillis());
	    	noteList.addAll(noteDAO.getNotesByCriteria(criteria));
	    }
		noteListAdapter.notifyDataSetChanged();
		special_query.setVisibility(View.VISIBLE);
	}
}
