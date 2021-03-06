package com.note.editview;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.searchviewdm.R;
import com.note.broadcast.NoteReceiver;
import com.note.constant.Constant;
import com.note.db.NoteDAO;
import com.note.vo.NoteVO;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivityNote extends Activity implements OnClickListener{
    
	/**
	 * ActionBar
	 */
	private ActionBar bar = null;
	private Button button_delete = null, button_save = null, btn_copy_note = null;
	private static NoteDAO noteDAO = null;
	private EditText contentText = null, note_title = null;
	private TextView txv_editTime = null, txv_executeTime = null;
	private LinearLayout editTimeLayout = null;
	private Calendar exec_time = Calendar.getInstance();
	private Toast tos = null;
	TextView exec_date_picker = null, exec_time_picker = null;
	private NotificationManager nm = null;
	// set datepicker
	DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener(){

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			exec_time.set(Calendar.YEAR, year);
			exec_time.set(Calendar.MONTH, monthOfYear);
			exec_time.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			exec_date_picker.setText(formatDateString(exec_time));
		}
		
	};
	// set timepicker
	TimePickerDialog.OnTimeSetListener timepicker = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			exec_time.set(Calendar.HOUR_OF_DAY, hourOfDay);
			exec_time.set(Calendar.MINUTE, minute);
			checkExecTimeCorrectPeriod();
			exec_time_picker.setText(formatTimeString(exec_time));
		}
	};
	
    /**
     *  modified id
     */
    private long _id = 0;
    private NoteVO note = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_activity_note);
		// 取得ActionBar
		bar = getActionBar();
		//顯示返回的箭頭，並可通過onOptionsItemSelected()進行監聽，其資源ID為android.R.id.home。
		bar.setHomeButtonEnabled(true);
		// 設定 ActionBar display
		bar.setCustomView(R.layout.note_title);
		bar.setDisplayShowCustomEnabled(true);
		
		button_delete = (Button) findViewById(R.id.button_Delete);
		button_save = (Button) findViewById(R.id.button_Save);
		button_delete.setOnClickListener(this);
		button_save.setOnClickListener(this);
		noteDAO = new NoteDAO(getApplicationContext());
		contentText = (EditText) findViewById(R.id.contentText);
		note_title = (EditText) findViewById(R.id.note_title);
		btn_copy_note = (Button) findViewById(R.id.btn_copy_note);
		btn_copy_note.setOnClickListener(this);
		txv_editTime = (TextView) findViewById(R.id.txv_editTime);
		txv_executeTime = (TextView) findViewById(R.id.txv_executeTime);
		txv_executeTime.setOnClickListener(this);
        bar.setIcon(getResources().getDrawable(R.drawable.left_48));
        editTimeLayout = (LinearLayout) findViewById(R.id.editTimeLayout);
        tos = Toast.makeText(this, "", Toast.LENGTH_LONG);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        processIntent();
        
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {//必須存在用來監聽返回icon
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_note, menu);

		return true;
	}	
	/**
	 * 處理上一個Activity 發過來的Intent
	 */
	public void processIntent(){
		Intent it = getIntent();
		if (it != null) {
			String iTaction = it.getStringExtra(Constant.IT_ACTION);
			if(iTaction.equals(Constant.ADD)){ // 新增記事
				button_delete.setVisibility(View.GONE);
				btn_copy_note.setVisibility(View.GONE);
				editTimeLayout.setVisibility(View.GONE);
			    txv_executeTime.setText(formatDateTimeString(exec_time));
			}
			else{// 修改記事
				button_delete.setVisibility(View.VISIBLE);
				btn_copy_note.setVisibility(View.VISIBLE);
				note = (NoteVO)it.getSerializableExtra(Constant.NOTEVO);
				_id = note.get_id();
				fillValue(note);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()){
	        case android.R.id.home:
	        	// push left arrow will return
	        	showLeaveHintCheckDialog();
	            break;   
	    }
	    return false;
	}
	
	
	@Override
	public void onBackPressed() {
				//super.onBackPressed();
		showLeaveHintCheckDialog();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		String currentTitle = "";
		switch(id){
			case R.id.button_Delete:
					cancelBeforeAlarmManager(noteDAO.getById(_id));
					noteDAO.deleteById(_id);
					finish();
				break;
			case R.id.button_Save:
				    int visibily = button_delete.getVisibility();
				    currentTitle = note_title.getText().toString().trim();
				    note_title.setText(currentTitle);
				    if(currentTitle.length() > 0){
				    	if(checkExecTimeCorrectPeriod()){
					    	NoteVO note = getCurrentValues();
						    if(visibily==View.VISIBLE){ // update
						    	//取消之前的notify
						    	cancelBeforeAlarmManager(noteDAO.getById(_id));
						    	note.set_id(_id);
						    	noteDAO.update(note);
						    	
						    }
						    else{ //insert
						    	noteDAO.insert(note);
						    }
						    pushToAlartManager(note);
						    finish();
				    	}
				    }
				    else{
				      note_title.setHint(getResources().getString(R.string.note_title_should_not_null));
				      note_title.setHintTextColor(Color.RED);
				      showHintAlertDialog();
				    }
				break;
			case R.id.btn_copy_note:
				    currentTitle = note_title.getText().toString().trim();
				    note_title.setText(currentTitle);
				    if(currentTitle.length() > 0){
				    	if(checkExecTimeCorrectPeriod()){
					    	note = getCurrentValues();
						    Calendar c = Calendar.getInstance();
						    note.setCreateTime(c.getTimeInMillis());
						    noteDAO.insert(note);
						    pushToAlartManager(note);
						    finish();
				    	}
				    }
				    else{
				    	note_title.setHint(getResources().getString(R.string.note_title_should_not_null));	
				    	note_title.setHintTextColor(Color.RED);
			    	    showHintAlertDialog();
				       }
				break;
			case R.id.txv_executeTime:// start alertDialog to pickTime
				customDialogShow();
				break;
			case R.id.exec_date_picker:
				// 設定DatePickerDialog樣式
	             DatePickerDialog dialog = new DatePickerDialog(
												this, 
												datepicker, 
												exec_time.get(Calendar.YEAR), 
												exec_time.get(Calendar.MONTH),
												exec_time.get(Calendar.DAY_OF_MONTH));
				// 顯示dialog直到 date被選取
	            dialog.show();
				break;
			case R.id.exec_time_picker:
				TimePickerDialog timeDialog = new TimePickerDialog(
						                          this, 
						                          timepicker, 
						                          exec_time.get(Calendar.HOUR_OF_DAY),
						                          exec_time.get(Calendar.MINUTE), 
						                          true);
				timeDialog.show();
				break;
		}
	}
	
	/**
	 * set the value from VO
	 * 
	 * @param note
	 */
	public void fillValue(NoteVO note){
		note_title.setText(note.getTitle());
		contentText.setText(note.getContent());
		txv_editTime.setText(note.getFormatCreateDate()+" "+note.getFormatCreateTime());
		editTimeLayout.setVisibility(View.VISIBLE);
		exec_time.setTimeInMillis(note.getExecTime());
		txv_executeTime.setText(formatDateTimeString(exec_time));
	}
	
	/**
	 * format calendar to format string
	 * 
	 * @param c
	 * @return
	 */
	public String formatDateTimeString(Calendar c){
		long currentDT = c.getTimeInMillis();
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(Locale.getDefault(), "%tF", new Date(currentDT)));
		sb.append(" ");
		sb.append(String.format(Locale.getDefault(), "%tR", new Date(currentDT)));
		return sb.toString();
	}
	
	/**
	 * @param c
	 * @return
	 */
	public String formatDateString(Calendar c){
		long currentDT = c.getTimeInMillis();
		return String.format(Locale.getDefault(), "%tF", new Date(currentDT));
	}
	
	public String formatTimeString(Calendar c){
		long currentDT = c.getTimeInMillis();
		return String.format(Locale.getDefault(), "%tR", new Date(currentDT));
	}
	
	/**
	 * get current value to NoteVO
	 * 
	 * @return
	 */
	public NoteVO getCurrentValues(){
		NoteVO note = new NoteVO();
		String currentTitle = note_title.getText().toString().trim();
		note.setTitle(currentTitle);
		String currentContent = contentText.getText().toString();
		note.setContent(currentContent);
		note.setCreateTime(Calendar.getInstance().getTimeInMillis());
		note.setExecTime(exec_time.getTimeInMillis());
		return note;
	}
	
	/**
	 * use to show the options of datepicker and timepicker
	 */
	public void customDialogShow(){
		final View item = LayoutInflater.from(MainActivityNote.this).inflate(R.layout.datetimepicker, null);
		AlertDialog.Builder chooser = new AlertDialog.Builder(MainActivityNote.this);
		chooser.setView(item);
		
		chooser.setTitle(R.string.datetimepicker_title);
		chooser.setPositiveButton(R.string.confirm_exec_time, new DialogInterface.OnClickListener() {
			
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					txv_executeTime.setText(formatDateTimeString(exec_time));
					
				}
			}
		);
		exec_date_picker = (TextView)item.findViewById(R.id.exec_date_picker); 
		exec_time_picker = (TextView)item.findViewById(R.id.exec_time_picker);
		exec_date_picker.setText(formatDateString(exec_time));
		exec_time_picker.setText(formatTimeString(exec_time));
		exec_date_picker.setOnClickListener(this);
		exec_time_picker.setOnClickListener(this);
		chooser.show();
	}
	
	/**
	 * 顯示輸入標題不得為空Dialog提示
	 */
	public void showHintAlertDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivityNote.this);
 	    alertDialog.setMessage(getResources().getString(R.string.note_title_should_not_null));
	     
 	    alertDialog.setPositiveButton(R.string.confirm_btn_str, new DialogInterface.OnClickListener() {
				
 	    		@Override
 	    		public void onClick(DialogInterface dialog, int which) {
					
 	    				dialog.dismiss();
					
 	    			}
				}
 	     );
	     alertDialog.show();
    }
	
	/**
	 * 顯示輸入標題不得為空Toast提示
	 */
	public void showHintToast(){
		tos.setText(getResources().getString(R.string.note_title_should_not_null));
		tos.show();
	}
	
	/**
	 * 把note資料設定於 execute_time 30分鐘之前提醒
	 * 
	 * @param note
	 */
	public void pushToAlartManager(NoteVO note){
		if(note!=null){
			// 設定 註冊alarmManager時間為執行時間30分鐘之前
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(note.getExecTime());
			cal.add(Calendar.MINUTE, -30);
			Intent intent = new Intent(this, NoteReceiver.class);
			intent.putExtra(Constant.IT_ACTION, Constant.MODIFY);
			// 只要 action、data、type、class、category 這幾個屬性其中有一個不同，則系統就會視為不同的 Intent
			// 在此設定category 利用日期字串 來區別每一個intent為不同intents
			// 註解: "%tc"格式為 :星期六 十月 27 14:21:20 CST 2007 精準度到秒
			intent.addCategory(String.format(Locale.getDefault(), "%tc", new Date(cal.getTimeInMillis())));
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.NOTEVO, note);
			intent.putExtras(bundle);
			PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	         
		    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
		}
	}
	
	/**
	 * 取消之前的AlartManager Event
	 * 
	 * @param note
	 */
	public void cancelBeforeAlarmManager(NoteVO note){
		if(note!=null){
			// 設定 註冊alarmManager時間為執行時間30分鐘之前
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(note.getExecTime());
			cal.add(Calendar.MINUTE, -30);
			Intent intent = new Intent(this, NoteReceiver.class);
			intent.putExtra(Constant.IT_ACTION, Constant.MODIFY);
			// 只要 action、data、type、class、category 這幾個屬性其中有一個不同，則系統就會視為不同的 Intent
			// 在此設定category 利用日期字串 來區別每一個intent為不同intents
			// 註解: "%tc"格式為 :星期六 十月 27 14:21:20 CST 2007 精準度到秒
			intent.addCategory(String.format(Locale.getDefault(), "%tc", new Date(cal.getTimeInMillis())));
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.NOTEVO, note);
			intent.putExtras(bundle);
			PendingIntent pi = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	         
		    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		    am.cancel(pi);
		}
	}
	
	public boolean checkExecTimeCorrectPeriod(){
		boolean checkExecTimeValid = true;
		Calendar curCal = Calendar.getInstance();
		curCal.add(Calendar.MINUTE, 29);
		if(exec_time.getTimeInMillis() < curCal.getTimeInMillis()){
			showHintCheckDialog();
			checkExecTimeValid = false;
			curCal.add(Calendar.MINUTE, 1);
			exec_time = curCal;
		}
		return checkExecTimeValid;
		
	}
	
	/**
	 * confirm execute_time setting
	 */
	public void showHintCheckDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivityNote.this);
 	    alertDialog.setMessage(getResources().getString(R.string.execute_time_after_current_time_30_minutes));
	     
 	    alertDialog.setPositiveButton(R.string.confirm_btn_str, new DialogInterface.OnClickListener() {
				
 	    		@Override
 	    		public void onClick(DialogInterface dialog, int which) {
 	    			    txv_executeTime.setText(formatDateTimeString(exec_time));
 	    				dialog.dismiss();
 	    				
 	    			}
				}
 	     );
	     alertDialog.show();
    }
	
	/**
	 * confirm leave MainActivityNote
	 */
	public void showLeaveHintCheckDialog(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivityNote.this);
 	    alertDialog.setMessage(getResources().getString(R.string.beforeLeaveEditView));
	     
 	    alertDialog.setPositiveButton(R.string.confirm_btn_str, new DialogInterface.OnClickListener() {
				
 	    		@Override
 	    		public void onClick(DialogInterface dialog, int which) {
 	    			    MainActivityNote.this.finish();
 	    				dialog.dismiss();
 	    				
 	    			}
				}
 	     );
		 alertDialog.setNegativeButton(R.string.cancelBtnStr, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();    				
					}
				}
		 );
	     alertDialog.show();
    }
}
