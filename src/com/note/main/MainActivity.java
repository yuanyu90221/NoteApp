package com.note.main;

import java.util.ArrayList;
import java.util.List;

import com.example.searchviewdm.R;
import com.note.db.NoteDAO;
import com.note.vo.NoteListAdapter;
import com.note.vo.NoteVO;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener,OnItemClickListener{

	private ListView lv;
	private NoteDAO noteDAO = null;
	NoteListAdapter noteListAdapter;
	List<NoteVO> noteList = null;
	private EditText inputSearch;
	Button deleteBatchBtn, cancelDeleteAllBtn;
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

			return true;
		case R.id.action_query_after_date:

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
		inputSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// MainActivity.this.adapter.getFilter().filter(s);
				MainActivity.this.noteListAdapter.getFilter().filter(s);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.deleteBatchBtn:
				// TODO : add process to delete batch
				noteDAO.batchDelete(idsToDelete.toArray(new String[0]));
				noteList.clear();
				noteList.addAll(noteDAO.getAll());
				resetUnDelete();
				break;
			case R.id.cancelDeletebtn:
				resetUnDelete();
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
		idsToDelete.clear();
	}
	
	/**
	 * set to onDelete status
	 */
	public void setDeleteStatus(){
		changeDeletable = true;
		changeDeleteStatus(changeDeletable);
		deleteBatchBtn.setVisibility(View.VISIBLE);
		cancelDeleteAllBtn.setVisibility(View.VISIBLE);
		idsToDelete.clear();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NoteVO note = noteList.get(position);	
		System.out.println("NOTE: "+ note);
		ListView currentList = (ListView) parent;
		System.out.println("NOTE: " + (currentList.getId()==R.id.listitem));
		if(currentList.isItemChecked(position)){// batch delete logic
			System.out.println("NOTE: "+changeDeletable);
//		    note = noteList.get(position);	
			System.out.println("NOTE: "+ note);
			idsToDelete.add(Long.toString(note.get_id()));
		  
		}
		else{
			System.out.println("NOTE: "+lv.isItemChecked(position));
		}
		
	}
}
