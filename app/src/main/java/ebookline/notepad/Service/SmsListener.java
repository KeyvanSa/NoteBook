package ebookline.notepad.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Receiver;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class SmsListener extends Service
{
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "SmsServiceChannel";

    HelperClass helper;
    DBHelper db;
    SharedHelper shared;

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

        if(!shared.getBoolean(Constants.RECEIVER_MESSAGE_ENABLE))
            stopSelf();

        if (intent != null)
            processSms(intent);

        stopForeground(true);
        stopSelf();

        return START_NOT_STICKY;
    }

    private void processSms(Intent intent)
    {
        String sender = intent.getStringExtra("sender");
        String message = intent.getStringExtra("message");

        if(sender==null || message==null)
            return;

        sender=sender.replace("+98","");

        Receiver receiver = db.getReceiver(
                Constants.SENDER+" LIKE '%"+sender+"%' OR "+Constants.SENDER+" LIKE '%0"+sender+"%'");

        if(receiver==null || !receiver.isEnable())
            return;

        if(receiver.getContain()!=null&&!TextUtils.isEmpty(receiver.getContain())){
            String[] str=receiver.getContain().split(",");
            boolean con = false;
            for(String s:str){
                if (message.contains(s)) {
                    con = true;
                    break;
                }
            }

            if(!con)
                return;
        }

        String stringBuilder = "در:" +
                helper.getDate(String.valueOf(System.currentTimeMillis())) +
                "\n" +
                "متن:\n" +
                message;

        receiver.setInformation(
                (receiver.getInformation()==null?"":receiver.getInformation()+"\n==================\n")+
                        stringBuilder);

        db.updateReceiver(receiver);
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
        return null;
    }
}