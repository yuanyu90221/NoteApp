package com.note.broadcast;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.example.searchviewdm.MainActivityNote;
import com.example.searchviewdm.R;
import com.note.constant.Constant;
import com.note.db.NoteDAO;
import com.note.vo.NoteVO;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

/**
 * @description Receivier use to accept the alarmManager notify and to set notification before execute_time 30 minutes
 * 
 * @author json 
 *
 */
public class NoteReceiver extends BroadcastReceiver {
	//設定音效通知
	final Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null&& intent.getStringExtra(Constant.IT_ACTION).equals(Constant.MODIFY)){
		    NoteDAO noteDAO = new NoteDAO(context);
			NoteVO note = (NoteVO)intent.getSerializableExtra(Constant.NOTEVO);
			long _id = note.get_id();
			NotificationManager nm = (NotificationManager)
					context.getSystemService(Context.NOTIFICATION_SERVICE);
			// 判斷已刪除的note 部分 不發notification
			if(noteDAO.getById(_id)!=null){
				NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
				// 設定notification顯示圖示
				nb.setSmallIcon(R.drawable.notes);
				// 設定notification顯示標題
				nb.setContentText(note.getTitle());
				// 設定顯示時間為execute_time -30 分鐘
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(note.getExecTime());
				cal.add(Calendar.MINUTE, -30);
				// 設定顯示notification時間
				nb.setWhen(cal.getTimeInMillis());
				// 設定notification按一次就消失
				nb.setAutoCancel(true);
				// 設定notification發出音效
				nb.setSound(soundUri);
				// 設定notification 按下之後導向該note頁面
				Intent notifyIt = new Intent(context, MainActivityNote.class);
				// 只要 action、data、type、class、category 這幾個屬性其中有一個不同，則系統就會視為不同的 Intent
				// 利用日期字串 來區別每一個intent為不同intents
				notifyIt.addCategory(String.format(Locale.getDefault(), "%tc", new Date(cal.getTimeInMillis())));
				notifyIt.putExtra(Constant.IT_ACTION, Constant.MODIFY);
				Bundle bundle = new Bundle();
				bundle.putSerializable(Constant.NOTEVO, note);
				notifyIt.putExtras(bundle);
				// 設定pending intent
				PendingIntent pi = PendingIntent.getActivity(context, 1, notifyIt, PendingIntent.FLAG_UPDATE_CURRENT);
				nb.setContentIntent(pi);
				Notification notifyNote = nb.build();
				// 發出intent 並且設定每個notify的識別碼 可以用來做cancel使用
				nm.notify((int)_id, notifyNote);
			}
			else{
				// 若_id 不在資料庫 則取消				
				nm.cancel((int)_id);
			}
			
		}
        
	}

}
