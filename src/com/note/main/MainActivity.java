package com.note.main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.example.searchviewdm.R;
import com.note.db.NoteDAO;
import com.note.vo.NoteListAdapter;
import com.note.vo.NoteVO;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		lv = (ListView) findViewById(R.id.listitem);
		inputSearch = (EditText) findViewById(R.id.searchBar);
		deleteBatchBtn = (Button) findViewById(R.id.deleteBatchBtn);
		cancelDeleteAllBtn = (Button) findViewById(R.id.cancelDeletebtn);
		deleteBatchBtn.setOnClickListener(this);
		cancelDeleteAllBtn.setOnClickListener(this);
		tos = Toast.makeText(this, "", Toast.LENGTH_LONG);
		searchBtn = (ImageButton) findViewById(R.id.searchBtn);
		searchBtn.setOnClickListener(this);
		special_query = (LinearLayout) findViewById(R.id.specical_query);
		returnBtn = (Button) findViewById(R.id.returnBtn);
		returnBtn.setOnClickListener(this);
		filterText = (TextView) findViewById(R.id.filerText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
			// TODO: 新增query todos 邏輯
			
			filterText.setText(getResources().getString(R.string.query_todos));
			noteList.clear();
			Calendar c = Calendar.getInstance();
			noteList.addAll(noteDAO.getAllByDate(c.getTimeInMillis()));
			noteListAdapter.notifyDataSetChanged();
			special_query.setVisibility(View.VISIBLE);
			return true;
		case R.id.action_query_after_date:
			// TODO: add pick date logic
//            filterText.setText(getResources().getString(R.string.query_after_date));
//            special_query.setVisibility(View.VISIBLE);
            return true;
		case R.id.action_sample:
			// 新增測試資料
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
			noteList.addAll(noteDAO.getAll());

			noteListAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
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
		noteDAO = new NoteDAO(getApplicationContext());
		noteList = noteDAO.getAll();
		noteListAdapter = new NoteListAdapter(this, R.layout.notelist, noteList);
		noteListAdapter.setNotifyOnChange(true);
		lv.setAdapter(noteListAdapter);
		lv.setOnItemClickListener(this);
		resetUnDelete();
		special_query.setVisibility(View.GONE);
//		inputSearch.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// MainActivity.this.adapter.getFilter().filter(s);
//				MainActivity.this.noteListAdapter.getFilter().filter(s);
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//
//			}
//		});
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
				noteList.clear();
				noteList.addAll(noteDAO.getAll());
				noteListAdapter.notifyDataSetChanged();
				resetUnDelete();
				break;
			case R.id.cancelDeletebtn:
				resetUnDelete();
				break;
			case R.id.searchBtn:
				String searchText = inputSearch.getText().toString().trim();
				noteList.clear();
				if(searchText.length() > 0){
					noteList.addAll(noteDAO.getAllByTitle(searchText));
				}
				else{
					noteList.addAll(noteDAO.getAll());
				}
				noteListAdapter.notifyDataSetChanged();
				break;
			case R.id.returnBtn:
				String queryTitle = inputSearch.getText().toString().trim();
				noteList.clear();
				if( queryTitle.length() > 0 ){
					noteList.addAll(noteDAO.getAllByTitle(queryTitle));
				}
				else{
					noteList.addAll(noteDAO.getAll());
				}
				noteListAdapter.notifyDataSetChanged();
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
		noteList.clear();
		noteList.addAll(noteDAO.getAll());
		noteListAdapter.notifyDataSetChanged();
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
		
		NoteVO note = noteList.get(position);	
		System.out.println("NOTE: "+ note);
		
	}
}
