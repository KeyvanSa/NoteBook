package ebookline.notepad.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import ebookline.notepad.Dialogs.ColorPickerDialog;
import ebookline.notepad.Prefs;
import ebookline.notepad.R;
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
    String language;

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

        settings.checkboxExpiredNotes.setChecked(shared.getBoolean(Constants.USE_EXPIRED_NOTE));
        if(settings.checkboxExpiredNotes.isChecked()){
            settings.linearExpiredNotes.setVisibility(View.VISIBLE);
        }

        settings.checkboxExpiredNotes.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                settings.linearExpiredNotes.setVisibility(View.VISIBLE);
            else settings.linearExpiredNotes.setVisibility(View.GONE);
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

        language = shared.getString(Constants.LANGUAGE,Constants.PERSIAN);

        if(helper.getCurrentLanguage().equals(Constants.ENGLISH))
            settings.radioButtonEnglishLanguage.setChecked(true);
        else settings.radioButtonPersianLanguage.setChecked(true);

        settings.radioButtonEnglishLanguage.setOnClickListener(view -> language = Constants.ENGLISH);

        settings.radioButtonPersianLanguage.setOnClickListener(view -> language = Constants.PERSIAN);

        ////////////////////Special Characters Start///////////////////////
        settings.checkboxFindHashtags.setChecked(shared.getBoolean(Constants.FIND_HASHTAG));
        settings.checkboxFindMentions.setChecked(shared.getBoolean(Constants.FIND_MENTION));
        settings.checkboxFindUrls.setChecked(shared.getBoolean(Constants.FIND_URL));
        settings.checkboxFindMails.setChecked(shared.getBoolean(Constants.FIND_MAIL));
        settings.checkboxFindPhones.setChecked(shared.getBoolean(Constants.FIND_PHONE));

        settings.checkboxClickableHashtags.setChecked(shared.getBoolean(Constants.CLICK_HASHTAG));
        settings.checkboxClickableMentions.setChecked(shared.getBoolean(Constants.CLICK_MENTION));
        settings.checkboxClickableUrls.setChecked(shared.getBoolean(Constants.CLICK_URL));
        settings.checkboxClickableMails.setChecked(shared.getBoolean(Constants.CLICK_MAIL));
        settings.checkboxClickablePhones.setChecked(shared.getBoolean(Constants.CLICK_PHONE));

        settings.linearLayoutHashtag.setVisibility( settings.checkboxFindHashtags.isChecked() ? View.VISIBLE : View.GONE );
        settings.linearLayoutMention.setVisibility( settings.checkboxFindMentions.isChecked() ? View.VISIBLE : View.GONE );
        settings.linearLayoutUrls.setVisibility( settings.checkboxFindUrls.isChecked() ? View.VISIBLE : View.GONE );
        settings.linearLayoutMail.setVisibility( settings.checkboxFindMails.isChecked() ? View.VISIBLE : View.GONE );
        settings.linearLayoutPhone.setVisibility( settings.checkboxFindPhones.isChecked() ? View.VISIBLE : View.GONE );

        setOnCheckedChangeListener(settings.checkboxFindHashtags,settings.linearLayoutHashtag);
        setOnCheckedChangeListener(settings.checkboxFindMentions,settings.linearLayoutMention);
        setOnCheckedChangeListener(settings.checkboxFindUrls,settings.linearLayoutUrls);
        setOnCheckedChangeListener(settings.checkboxFindMails,settings.linearLayoutMail);
        setOnCheckedChangeListener(settings.checkboxFindPhones,settings.linearLayoutPhone);

        setBackgroundColor(settings.linearLayoutColorHashtag,shared.getString(Constants.COLOR_HASHTAG),R.color.text_color_hashtag);
        setBackgroundColor(settings.linearLayoutColorMention,shared.getString(Constants.COLOR_MENTION),R.color.text_color_mention);
        setBackgroundColor(settings.linearLayoutColorUrls,shared.getString(Constants.COLOR_URL),R.color.text_color_url);
        setBackgroundColor(settings.linearLayoutColorMail,shared.getString(Constants.COLOR_MAIL),R.color.text_color_mail);
        setBackgroundColor(settings.linearLayoutColorPhone,shared.getString(Constants.COLOR_PHONE),R.color.text_color_phone);

        chooseColor(settings.linearLayoutColorHashtag,Constants.COLOR_HASHTAG);
        chooseColor(settings.linearLayoutColorMention,Constants.COLOR_MENTION);
        chooseColor(settings.linearLayoutColorUrls,Constants.COLOR_URL);
        chooseColor(settings.linearLayoutColorMail,Constants.COLOR_MAIL);
        chooseColor(settings.linearLayoutColorPhone,Constants.COLOR_PHONE);

        ////////////////////Special Characters End///////////////////////

    }

    private void setBackgroundColor(LinearLayout linearLayoutColor, String string, int text_color) {

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        shape.setColor((string==null ? text_color : Color.parseColor(string)));

        float[]hsv=new float[3];
        Color.colorToHSV((string==null ? text_color : Color.parseColor(string)),hsv);
        hsv[2] *= 0.8f;

        shape.setStroke(5,Color.HSVToColor(hsv));

        linearLayoutColor.setBackground(shape);
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

        if(settings.checkboxExpiredNotes.isChecked())
            shared.saveBoolean(Constants.USE_EXPIRED_NOTE,settings.checkboxExpiredNotes.isChecked());
        else
            shared.removeValue(Constants.USE_EXPIRED_NOTE);

        shared.saveString(Constants.LANGUAGE,language);

        shared.saveBoolean(Constants.FIND_HASHTAG,settings.checkboxFindHashtags.isChecked());
        shared.saveBoolean(Constants.FIND_MENTION,settings.checkboxFindMentions.isChecked());
        shared.saveBoolean(Constants.FIND_URL,settings.checkboxFindUrls.isChecked());
        shared.saveBoolean(Constants.FIND_MAIL,settings.checkboxFindMails.isChecked());
        shared.saveBoolean(Constants.FIND_PHONE,settings.checkboxFindPhones.isChecked());

        shared.saveBoolean(Constants.CLICK_HASHTAG,settings.checkboxClickableHashtags.isChecked());
        shared.saveBoolean(Constants.CLICK_MENTION,settings.checkboxClickableMentions.isChecked());
        shared.saveBoolean(Constants.CLICK_URL,settings.checkboxClickableUrls.isChecked());
        shared.saveBoolean(Constants.CLICK_MAIL,settings.checkboxClickableMails.isChecked());
        shared.saveBoolean(Constants.CLICK_PHONE,settings.checkboxClickablePhones.isChecked());

        Intent toMainActivity = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(toMainActivity);
        finish();
    }

    private void chooseColor(LinearLayout linearLayoutColor, String sharedKey) {
        linearLayoutColor.setOnClickListener(view -> {
            ColorPickerDialog dialog = new ColorPickerDialog(this);
            dialog.setColorsList(Constants.SpecialCharactersColorsList);
            dialog.setOnClickButtonListener(new ColorPickerDialog.OnClickButtonListener() {
                @Override
                public void chooseColor(String colorCode) {

                    GradientDrawable shape = new GradientDrawable();
                    shape.setShape(GradientDrawable.RECTANGLE);
                    shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
                    shape.setColor(Color.parseColor(colorCode));

                    float[]hsv=new float[3];
                    Color.colorToHSV(Color.parseColor(colorCode),hsv);
                    hsv[2] *= 0.8f;
                    shape.setStroke(5,Color.HSVToColor(hsv));
                    linearLayoutColor.setBackground(shape);

                    shared.saveString(sharedKey,colorCode);
                }
                @Override
                public void chooseBack() {}
            });
            dialog.showDialog();
        });
    }

    private void setOnCheckedChangeListener(CheckBox checkBox, LinearLayout linearLayout){
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
                linearLayout.setVisibility(View.VISIBLE);
            else linearLayout.setVisibility(View.GONE);
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}