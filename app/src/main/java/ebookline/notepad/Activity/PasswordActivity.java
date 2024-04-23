package ebookline.notepad.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

import ebookline.notepad.Prefs;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityPasswordBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PasswordActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher
{
    ActivityPasswordBinding password;

    HelperClass helper;
    SharedHelper shared;

    EditText[] editTexts;
    EditText editTextPosition;
    int      position=0;

    String strPassword , strSecurityText , strEntryPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);

        password=ActivityPasswordBinding.inflate(getLayoutInflater());
        setContentView(password.getRoot());

        helper=new HelperClass(this);
        shared=new SharedHelper(this);

        if(!Prefs.getBoolean(Constants.USE_PASSWORD)){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        strPassword = Prefs.getString(Constants.PASSWORD,"");
        strSecurityText = Prefs.getString(Constants.SECURITY_TEXT,"");

        password.button1.setOnClickListener(this);
        password.button2.setOnClickListener(this);
        password.button3.setOnClickListener(this);
        password.button4.setOnClickListener(this);
        password.button5.setOnClickListener(this);
        password.button6.setOnClickListener(this);
        password.button7.setOnClickListener(this);
        password.button8.setOnClickListener(this);
        password.button9.setOnClickListener(this);
        password.button0.setOnClickListener(this);
        password.buttonClear.setOnClickListener(this);

        password.editText1.addTextChangedListener(this);
        password.editText2.addTextChangedListener(this);
        password.editText3.addTextChangedListener(this);
        password.editText4.addTextChangedListener(this);

        editTexts=new EditText[]{password.editText1,password.editText2,password.editText3,password.editText4};
        editTextPosition=editTexts[position];

        password.textViewForgetPassword.setOnClickListener(view -> {
            password.textViewForgetPassword.setVisibility(View.GONE);
            password.textLayoutSecurityQuestion.setVisibility(View.VISIBLE);
            password.buttonCheckPassword.setVisibility(View.VISIBLE);
            password.linearLayoutEditTexts.setVisibility(View.GONE);
            password.textViewTitle.setVisibility(View.GONE);
            password.linearLayoutKeyboard.setVisibility(View.INVISIBLE);
        });

        password.buttonCheckPassword.setOnClickListener(view ->
        {
            if(password.edittextSecurityQuestion.getText().toString().equals(strSecurityText)){

                Prefs.remove(Constants.USE_PASSWORD);
                Prefs.remove(Constants.PASSWORD);
                Prefs.remove(Constants.SECURITY_TEXT);

                helper.showToast(getString(R.string.delete_password),1);
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }else{
                helper.showToast(getString(R.string.security_text_wrong),2);
                helper.shakeAnimation(password.relativeMain);
            }
        });

        if(!helper.hasFingerPrint() || !Prefs.getBoolean(Constants.USE_FINGERPRINT) )
            password.imageViewFingerPrint.setVisibility(View.INVISIBLE);
        else{

            Executor executor = ContextCompat.getMainExecutor(this);
            BiometricPrompt biometricPrompt = new BiometricPrompt(PasswordActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {super.onAuthenticationError(errorCode, errString);}
                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    startActivity(new Intent(PasswordActivity.this,MainActivity.class));
                    finish();
                }
            });

            final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.use_fingerprint))
                    .setNegativeButtonText(getString(R.string.back)).build();

            biometricPrompt.authenticate(promptInfo);

            password.imageViewFingerPrint.setOnClickListener(view ->
                    biometricPrompt.authenticate(promptInfo));
        }

    }

    @Override
    public void onClick(View view) {
        Button button= (Button) view;

        if(button.getId()==R.id.buttonClear){
            position=0;
            editTextPosition=editTexts[0];
            password.editText1.setText("");
            password.editText2.setText("");
            password.editText3.setText("");
            password.editText4.setText("");
            return;
        }

        editTextPosition.setText(button.getText().toString());
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(charSequence.toString().length()==0)
            return;
        if(position==editTexts.length-1){

            strEntryPassword="";
            for (EditText et:editTexts)
                strEntryPassword+=et.getText().toString();

            if(strEntryPassword.equals(strPassword)){
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }else{
                helper.showToast(getResources().getString(R.string.wrong_password),2);
                helper.shakeAnimation(password.relativeMain);
                onClick(password.buttonClear);
            }
            return;
        }

        position++;
        editTextPosition = editTexts[position];
    }
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
    @Override
    public void afterTextChanged(Editable editable) {}

    @Override
    public void onBackPressed() {
        if(password.textLayoutSecurityQuestion.getVisibility()==View.VISIBLE){
            password.textLayoutSecurityQuestion.setVisibility(View.GONE);
            password.buttonCheckPassword.setVisibility(View.GONE);
            password.textViewForgetPassword.setVisibility(View.VISIBLE);
            password.linearLayoutEditTexts.setVisibility(View.VISIBLE);
            password.textViewTitle.setVisibility(View.VISIBLE);
            password.linearLayoutKeyboard.setVisibility(View.VISIBLE);
            onClick(password.buttonClear);
        }else super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}