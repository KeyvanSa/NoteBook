package ebookline.notepad.Util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.R;
import xyz.hasnat.sweettoast.SweetToast;

public class HelperClass
{
    private final Context context;

    public void shakeAnimation(View view){
        Animation shakeAnimation = AnimationUtils.loadAnimation(context,R.anim.shake);
        shakeAnimation.setRepeatCount(2);
        view.startAnimation(shakeAnimation);

        Vibrator vibrator= (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(400);
    }

    public boolean copyFile(File sourceFile, File destFile)
    {
        try
        {
            if (!destFile.exists())
                destFile.createNewFile();

            try(FileInputStream in = new FileInputStream(sourceFile);
                FileOutputStream out = new FileOutputStream(destFile))
            {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                return true;
            }
        }catch (Exception e){

            if(e.toString().contains("Operation not permitted")){
                showToast(context.getString(R.string.operation_not_permitted),2);
                if (Build.VERSION.SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        context.startActivity(getpermission);
                    }else exceptionHandler(e);
                }else exceptionHandler(e);
            }else exceptionHandler(e);

            return false;
        }
    }

    public void searchText(TextView textView, String text)
    {
        if(textView==null || text==null)
            return;

        int sensitivityValue =  Pattern.CASE_INSENSITIVE;
        int foregroundColor  = context.getResources().getColor(R.color.text_color_find_search);
        int backgroundColor  = context.getResources().getColor(R.color.text_background_color_find_search);

        String fullText = textView.getText().toString();
        String findText = text.trim();

        SpannableStringBuilder sb = new SpannableStringBuilder(fullText);
        Pattern p = Pattern.compile(findText,sensitivityValue);
        Matcher m = p.matcher(fullText);

        int counter =0;
        while (m.find()){
            sb.setSpan(new ForegroundColorSpan(foregroundColor),
                    m.start(),
                    m.end(),
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);

           if(counter==0)
               sb.setSpan(new BackgroundColorSpan(backgroundColor),
                   m.start(),
                   m.end(),
                   Spannable.SPAN_INCLUSIVE_INCLUSIVE);

           counter++;
        }

        textView.setText(sb);
    }

    public void saveTextToFile(File file, String text) {
        try {
            if(!file.exists())
                file.createNewFile();

            if(!file.isFile())
                return;

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(text);
            fileWriter.close();

            showToast(context.getString(R.string.save),3);

        } catch (Exception e) {

            if(e.toString().contains("Operation not permitted")){
                showToast(context.getString(R.string.operation_not_permitted),2);
                if (Build.VERSION.SDK_INT >= 30){
                    if (!Environment.isExternalStorageManager()){
                        Intent getpermission = new Intent();
                        getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        context.startActivity(getpermission);
                    }else exceptionHandler(e);
                }else exceptionHandler(e);
            }else exceptionHandler(e);
        }
    }

    public void copyToClipboard(String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getResources().getString(R.string.copy_text), textToCopy);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            showToast(context.getResources().getString(R.string.copy_text),1);
        }
    }

    public void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(shareIntent, context.getResources().getString(R.string.copy_text)));
    }

    public boolean hasReadPermission(){
        return ContextCompat
                .checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasWritePermission(){
        return ContextCompat
                .checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public boolean hasWriteManagePermission(){
        return ContextCompat
                .checkSelfPermission(context,Manifest.permission.MANAGE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasFingerPrint(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ((FingerprintManager)context.getSystemService(Context.FINGERPRINT_SERVICE)).isHardwareDetected()
                && ((FingerprintManager)context.getSystemService(Context.FINGERPRINT_SERVICE)).hasEnrolledFingerprints();
    }

    public String getDisplayableTime(long delta) {
        long difference = 0;
        long mDate = System.currentTimeMillis();
        if (mDate > delta) {
            difference = mDate - delta;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 60) {
                return context.getResources().getString(R.string.few_second_ago);
            } else if (seconds < 120) {
                return context.getResources().getString(R.string.minute_ago);
            } else if (seconds < 2700) // 45 * 60
            {
                return String.format(context.getResources().getString(R.string.minutes_ago), minutes);
            } else if (seconds < 5400) // 90 * 60
            {
                return context.getResources().getString(R.string.hour_ago);
            } else if (seconds < 86400) // 24 * 60 * 60
            {
                return String.format(context.getResources().getString(R.string.hours_ago), hours);
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                return context.getResources().getString(R.string.yesterday);
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return String.format(context.getResources().getString(R.string.days_ago), days);
            } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {
                return months <= 1 ? context.getResources().getString(R.string.a_month_ago) :
                        String.format(context.getResources().getString(R.string.month_ago), months);
            } else {
                return years <= 1 ? context.getResources().getString(R.string.a_year_ago) :
                        String.format(context.getResources().getString(R.string.year_ago), years);
            }
        }
        return null;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDate(String time) {
        long l = Long.parseLong(time);
        return String.format("%s-%s",new SimpleDateFormat("HH:mm")
                        .format(new Date(l)),new JalaliCalendarClass().getJalaliDate(new Date(l)));
    }

    public void exceptionHandler(Exception e){
        showToast(e.toString(),2);
    }

    public void showToast(String message , int type){
        if(type==1)
            SweetToast.info(context,message);
        else if(type==2)
            SweetToast.error(context,message);
        else SweetToast.success(context,message);
    }

    public void showAlert(String message,int type) {
        CustomDialog dialog = new CustomDialog(context);
        dialog.setText(message);
        dialog.setType(type);
        dialog.showDialog();
    }

    public HelperClass(Context context){
        this.context=context;
    }
}
