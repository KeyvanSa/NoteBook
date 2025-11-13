package ebookline.notepad.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;
import java.util.List;

import ebookline.notepad.Adapter.ReceiverAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Dialogs.BottomSheetReceiver;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Model.Receiver;
import ebookline.notepad.R;
import ebookline.notepad.Receiver.SmsReceiver;
import ebookline.notepad.Service.NotificationListener;
import ebookline.notepad.Service.SmsListener;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityReceiverBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ReceiverActivity extends AppCompatActivity
{
    ActivityReceiverBinding binding;
    HelperClass helper;
    SharedHelper shared;
    DBHelper db;

    HashMap<String,Boolean> permissions = new HashMap<>();

    List<Receiver> receivers;

    Intent serviceIntent,notificationServiceIntent;
    SmsReceiver smsReceiver;
    IntentFilter intentFilter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        binding=ActivityReceiverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        String hexCode = "#58A9FB";
        try {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            int color = 0;
            if(theme.resolveAttribute(R.attr.colorAccent,typedValue,true))
                if(typedValue.resourceId != 0 )
                    color = ContextCompat.getColor(this,typedValue.resourceId);
                else color = typedValue.data;
            if(color!=0){
                hexCode = String.format("#%06X",color);
            }
        }catch (Exception ignored){}

        binding.relativePermissions.setBackground(helper.setBackgroundShape(
                hexCode,5,10,30 ));

        binding.radioButtonSmsRead.setOnClickListener(view ->
        {
            Dexter.withContext(ReceiverActivity.this)
                    .withPermission(Manifest.permission.READ_SMS)
                    .withListener(new PermissionListener()
                    {
                        @Override public void onPermissionGranted(PermissionGrantedResponse response)
                        {
                            init();
                            Dexter.withContext(ReceiverActivity.this)
                                    .withPermission(Manifest.permission.RECEIVE_SMS)
                                    .withListener(new PermissionListener()
                                    {
                                        @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                                            helper.showToast("ok",2);
                                        }
                                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                                        }
                                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                            token.continuePermissionRequest();
                                        }
                                    })
                                    .withErrorListener(dexterError -> helper.showToast(dexterError.toString(),1))
                                    .check();
                        }
                        @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                            if(response.isPermanentlyDenied()){
                                CustomDialog dialog1 =new CustomDialog(ReceiverActivity.this);
                                dialog1.setTitle(getString(R.string.program_settings));
                                dialog1.setText(getString(R.string.settings_app));
                                dialog1.setButtonOkText(getString(R.string.go_to_settings_page));
                                dialog1.setButtonNoText(getString(R.string.reject));
                                dialog1.setClickListener(new CustomDialog.ItemClickListener() {
                                    @Override
                                    public void onPositiveItemClick(View view1) {}
                                    @Override
                                    public void onNegativeItemClick(View view1) {
                                        helper.showToast(getString(R.string.permission_rejected),1);
                                    }
                                });
                                dialog1.setOnDismissListener(dialogInterface -> helper.showToast(getString(R.string.permission_rejected),1));
                                dialog1.showDialog();
                            }else helper.showToast(getString(R.string.permission_rejected),1);
                        }
                        @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(dexterError -> helper.showToast(dexterError.toString(),1))
                    .check();
        });

        binding.radioButtonBatteryOptimization.setOnClickListener(view ->
        {
            if(helper.isIgnoringBatteryOptimizations())
                init();
            else helper.goToBatterySettings();
        });

        binding.switchEnableMessages.setOnCheckedChangeListener((compoundButton, b) ->
        {
           if(b){
               registerReceiver(smsReceiver,intentFilter);
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                   startForegroundService(serviceIntent);
               else startService(serviceIntent);
           }else {
               stopService(serviceIntent);
               LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
           }
           shared.saveBoolean(Constants.RECEIVER_MESSAGE_ENABLE,b);
        });

        binding.switchEnableNotifications.setOnCheckedChangeListener((compoundButton, b) ->
        {
            if(helper.isNotificationServiceEnable())
            {
                if(b){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        startForegroundService(notificationServiceIntent);
                    else startService(notificationServiceIntent);
                }else {
                    stopService(notificationServiceIntent);
                }
            }else helper.goToNotificationsSettings();
            shared.saveBoolean(Constants.RECEIVER_NOTIFICATION_ENABLE,b);
        });

        binding.menuItemAddReceiver.setOnClickListener(view -> {
            binding.menu.close(true);
            showReceiverBottomSheet(null);
        });

    }

    private void init()
    {
        helper = new HelperClass(this);
        shared = new SharedHelper(this);
        db = new DBHelper(this);

        smsReceiver = new SmsReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        serviceIntent = new Intent(ReceiverActivity.this, SmsListener.class);
        notificationServiceIntent = new Intent(ReceiverActivity.this, NotificationListener.class);

        permissions.put("smsRead",false);
        permissions.put("batteryLimit",false);

        if(helper.hasSmsReadPermission()){
            binding.radioButtonSmsRead.setEnabled(false);
            binding.radioButtonSmsRead.setClickable(false);
            binding.radioButtonSmsRead.setChecked(true);
            permissions.put("smsRead",true);
        }

        if(helper.isIgnoringBatteryOptimizations()){
            binding.radioButtonBatteryOptimization.setEnabled(false);
            binding.radioButtonBatteryOptimization.setClickable(false);
            binding.radioButtonBatteryOptimization.setChecked(true);
            permissions.put("batteryLimit",true);
        }

        boolean b=true;
        for(boolean value : permissions.values()){
            if (!value) {
                b = false;
                break;
            }
        }

        binding.menu.setVisibility(View.VISIBLE);
        if(b) {
            binding.relativePermissions.setVisibility(View.GONE);
            binding.relativeMain.setVisibility(View.VISIBLE);

            binding.switchEnableMessages.setVisibility(View.VISIBLE);
            binding.switchEnableNotifications.setVisibility(View.VISIBLE);
        } else {
            binding.menu.setVisibility(View.GONE);
            return;
        }

        if(shared.getBoolean(Constants.RECEIVER_MESSAGE_ENABLE)){
            binding.switchEnableMessages.setChecked(true);
        }

        if(shared.getBoolean(Constants.RECEIVER_NOTIFICATION_ENABLE)) {
            if (helper.isServiceRunning(NotificationListener.class))
                binding.switchEnableNotifications.setChecked(true);
        }

        if(!helper.isNotificationServiceEnable() && binding.switchEnableNotifications.isChecked() && notificationServiceIntent!=null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                startForegroundService(notificationServiceIntent);
            else startService(notificationServiceIntent);
        }else stopService(notificationServiceIntent);

        setList();
    }

    private void setList()
    {
        receivers = db.getReceivers(null,null);
        ReceiverAdapter adapter = new ReceiverAdapter(this,receivers);
        adapter.setClickListener((view, position) ->
        {
            showReceiverBottomSheet(receivers.get(position));
        });
        binding.recyclerReceivers.setAdapter(adapter);

        binding.recyclerReceivers.setVisibility(View.VISIBLE);
        binding.relativeEmptyList.setVisibility(View.GONE);
        if(receivers.size()==0){
            binding.relativeEmptyList.setVisibility(View.VISIBLE);
            binding.recyclerReceivers.setVisibility(View.GONE);
        }

    }

    private void showReceiverBottomSheet(Receiver receiver)
    {
        BottomSheetReceiver bottomSheet = new BottomSheetReceiver(this);
        bottomSheet.setReceiver(receiver);
        bottomSheet.setClickListener(new BottomSheetReceiver.ItemClickListener() {
            @Override
            public void onSave(Receiver receiver, long state) {
                setList();
            }
            @Override
            public void onDelete(Receiver receiver, long id) {
                setList();
            }
        });
        bottomSheet.show(this.getSupportFragmentManager(),bottomSheet.getTag());
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
}