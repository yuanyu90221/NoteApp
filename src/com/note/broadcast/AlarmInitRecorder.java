package com.note.broadcast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.note.constant.Constant;
import com.note.db.NoteDAO;
import com.note.vo.NoteVO;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @description 用來當重新開機重新註冊的BroadcastReceiver
 * 
 * @author json
 *
 * @reference: https://examples.javacodegeeks.com/android/core/activity/android-start-service-boot-example/
 */
public class AlarmInitRecorder extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Calendar current = Calendar.getInstance();
		current.add(Calendar.MINUTE, 29);
		NoteDAO noteDAO = new NoteDAO(context);
		List<NoteVO> listToNote = noteDAO.getAllByExecDate(current.getTimeInMillis());
		for(NoteVO note: listToNote){
			pushToAlartManager(note, context);
		}
	}

	/**
	 * 把note資料設定於 execute_time 30分鐘之前提醒
	 * 
	 * @param note
	 */
	public void pushToAlartManager(NoteVO note, Context context){
		if(note!=null){
			// 設定 註冊alarmManager時間為執行時間30分鐘之前
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(note.getExecTime());
			cal.add(Calendar.MINUTE, -30);
			Intent intent = new Intent(context, NoteReceiver.class);
			intent.putExtra(Constant.IT_ACTION, Constant.MODIFY);
			// 只要 action、data、type、class、category 這幾個屬性其中有一個不同，則系統就會視為不同的 Intent
			// 在此設定category 利用日期字串 來區別每一個intent為不同intents
			// 註解: "%tc"格式為 :星期六 十月 27 14:21:20 CST 2007 精準度到秒
			intent.addCategory(String.format(Locale.getDefault(), "%tc", new Date(cal.getTimeInMillis())));
			Bundle bundle = new Bundle();
			bundle.putSerializable(Constant.NOTEVO, note);
			intent.putExtras(bundle);
			PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	         
		    AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		    am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
		}
	}
}
