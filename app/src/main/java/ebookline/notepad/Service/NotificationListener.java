package ebookline.notepad.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Receiver;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class NotificationListener extends NotificationListenerService
{
    private static final int NOTIFICATION_ID = 2;
    private static final String CHANNEL_ID = "NotificationServiceChannel";

    HelperClass helper;
    DBHelper db;
    SharedHelper shared;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn)
    {
        String packageName = sbn.getPackageName();
        Notification notification = sbn.getNotification();
        CharSequence title = notification.extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence text = notification.extras.getCharSequence(Notification.EXTRA_TEXT);

        Receiver receiver = db.getReceiver(
                Constants.SENDER+" LIKE '%"+packageName+"%'");

        if(receiver==null || !receiver.isEnable()) {
            stopForeground(true);
            stopSelf();
            return;
        }

        if(receiver.getContain()!=null&&!TextUtils.isEmpty(receiver.getContain())){
            String[] str=receiver.getContain().split(",");
            boolean con = false;
            for(String s:str){
                if (text.toString().contains(s)) {
                    con = true;
                    break;
                }
            }

            if(!con) {
                stopForeground(true);
                stopSelf();
                return;
            }
        }

        // برای جلوگیری از ذخیره نوتیف های تکراری
        if(receiver.getInformation()!=null &&
                receiver.getInformation().contains(text)){
            return;
        }

        String stringBuilder =
                "title:"+title+"\n"+
                "text:"+text+"\n"+
                helper.getDate(String.valueOf(System.currentTimeMillis()))+"\n"+
               // "Id:"+sbn.getId()+"\n"+
              //  "Key:"+sbn.getKey()+"\n"+
                "PackageName:"+sbn.getPackageName()+"\n"
              //  "PostTime:"+sbn.getPostTime()+"\n"+
              //  "Tag:"+sbn.getTag()+"\n"+
              //  "GroupKey:"+sbn.getGroupKey()+"\n"+

               // "category:"+sbn.getNotification().category
                ;

        receiver.setInformation(
                (receiver.getInformation()==null?"":receiver.getInformation()+"\n==================\n")+
                stringBuilder);

        db.updateReceiver(receiver);

        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

    @Override
    public void onCreate()
    {
        super.onCreate();

        helper=new HelperClass(this);
        db=new DBHelper(this);
        shared=new SharedHelper(this);

        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.receiver_notification_text))
                .build();

        startForeground(NOTIFICATION_ID, notification);

        if(!shared.getBoolean(Constants.RECEIVER_NOTIFICATION_ENABLE)) {
            stopSelf();
            onDestroy();
        }

        new Handler()
                .postDelayed(this::stopSelf,1000);

        return START_NOT_STICKY;
    }

    private void createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, getResources().getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
