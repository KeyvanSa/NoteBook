package ebookline.notepad.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import ebookline.notepad.Service.SmsListener;
import ebookline.notepad.Util.HelperClass;

public class SmsReceiver extends BroadcastReceiver
{
    HelperClass helper;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        helper=new HelperClass(context);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, SmsListener.class);
            if(!helper.isServiceRunning(SmsListener.class)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                }else context.startService(serviceIntent);
            }
        }

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"))
        {
            Bundle bundle = intent.getExtras();
            if (bundle != null)
            {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null)
                {
                    StringBuilder sender= new StringBuilder();
                    StringBuilder messageBody= new StringBuilder();

                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        sender.append(smsMessage.getDisplayOriginatingAddress());
                        messageBody.append(smsMessage.getMessageBody());
                    }

                    Intent serviceIntent = new Intent(context, SmsListener.class);
                    serviceIntent.putExtra("sender", sender.toString());
                    serviceIntent.putExtra("message", messageBody.toString());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        context.startForegroundService(serviceIntent);
                    else context.startService(serviceIntent);

                }
            }
        }

    }
}
