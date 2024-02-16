package ebookline.notepad.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ebookline.notepad.Dialogs.ColorPickerDialog;
import ebookline.notepad.Prefs;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivitySettingsBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingsActivity extends AppCompatActivity
{
    ActivitySettingsBinding settings;

    HelperClass helper;
    SharedHelper shared;

    String textColor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        settings=ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(settings.getRoot());

        helper=new HelperClass(this);
        shared=new SharedHelper(this);

        init();

    }

    private void init()
    {
        textColor = shared.getString(Constants.TEXT_COLOR,Constants.TextColorsList.get(0));

        if(shared.getInt(Constants.THEME,1)==1)
            settings.radioButtonLightTheme.setChecked(true);
        else if(shared.getInt(Constants.THEME,1)==2)
            settings.radioButtonDarkTheme.setChecked(true);

        if(!helper.hasFingerPrint())
            settings.checkboxUseFingerPrint.setVisibility(View.GONE);

        settings.checkboxUsePassword.setChecked(Prefs.getBoolean(Constants.USE_PASSWORD));
        settings.checkboxUseFingerPrint.setChecked(Prefs.getBoolean(Constants.USE_FINGERPRINT));

        if(settings.checkboxUsePassword.isChecked()){
            settings.linearUsePassword.setVisibility(View.VISIBLE);
        }

        settings.checkboxUsePassword.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                settings.linearUsePassword.setVisibility(View.VISIBLE);
            else settings.linearUsePassword.setVisibility(View.GONE);
        });

        settings.buttonTextColor.setTextColor(Color.parseColor(textColor));
        settings.buttonTextColor.setOnClickListener(view -> {
            ColorPickerDialog dialog = new ColorPickerDialog(this);
            dialog.setColorsList(Constants.TextColorsList);
            dialog.setOnClickButtonListener(new ColorPickerDialog.OnClickButtonListener() {
                @Override
                public void chooseColor(String colorCode) {
                    textColor = colorCode;
                    settings.buttonTextColor.setTextColor(Color.parseColor(colorCode));
                }
                @Override
                public void chooseBack() {}
            });
            dialog.showDialog();
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            settings.textViewVersion.setText(String.format("%s(%s)",pInfo.versionCode,pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            settings.textViewVersion.setText("10.0.0");
        }

    }

    @Override
    public void onBackPressed() {

        shared.saveString(Constants.TEXT_COLOR,textColor);

        if(settings.radioButtonLightTheme.isChecked())
            shared.saveInt(Constants.THEME,1);
        else if(settings.radioButtonDarkTheme.isChecked())
            shared.saveInt(Constants.THEME,2);

        if(settings.checkboxUsePassword.isChecked()){
            if(Objects.requireNonNull(settings.edittextPassword.getText()).toString().length()>0&&
                    Objects.requireNonNull(settings.edittextSecurityQuestion.getText()).toString().length()>0){

                Prefs.putBoolean(Constants.USE_PASSWORD,settings.checkboxUsePassword.isChecked());
                Prefs.putString(Constants.PASSWORD, Objects.requireNonNull(settings.edittextPassword.getText()).toString());
                Prefs.putString(Constants.SECURITY_TEXT, Objects.requireNonNull(settings.edittextSecurityQuestion.getText()).toString());
            }
            Prefs.putBoolean(Constants.USE_FINGERPRINT,settings.checkboxUseFingerPrint.isChecked());
        }else Prefs.remove(Constants.USE_PASSWORD);

        Intent toMainActivity = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}