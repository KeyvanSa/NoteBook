package ebookline.notepad.Dialogs;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

import ebookline.notepad.Activity.MainActivity;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class BottomSheetSelectedNotes extends BottomSheetDialogFragment
{
    Context context;
    HelperClass helper;

    private ArrayList<Note>selectedNotes;

    private TextView textViewTitle;
    private TextView textViewText;
    private EditText edittextFileName;
    private CheckBox checkBoxSaveTitle;
    private CheckBox checkBoxSaveText;
    private CheckBox checkBoxSaveDate;
    private CheckBox checkBoxSelectSeparator;
    private RadioGroup radioGroupSelectSeparator;
    private RadioGroup radioGroupSelectDateType;
    private RadioButton radioButtonGregorianDate;
    private RadioButton radioButtonJalaliDate;
    private RadioButton radioButtonLine;
    private RadioButton radioButtonDot;
    private RadioButton radioButtonDash;
    private RadioButton radioButtonUnderLine;
    private Button buttonOK;
    private Button buttonNo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_selected_notes,container,false);

        init(view);

        return view.getRootView();
    }

    private void init(View view) {
        textViewTitle = view.findViewById(R.id.textViewTitle);
        textViewText = view.findViewById(R.id.textViewText);
        edittextFileName = view.findViewById(R.id.edittextFileName);
        checkBoxSaveTitle = view.findViewById(R.id.checkBoxSaveTitle);
        checkBoxSaveText = view.findViewById(R.id.checkBoxSaveText);
        checkBoxSaveDate = view.findViewById(R.id.checkBoxSaveDate);
        checkBoxSelectSeparator = view.findViewById(R.id.checkBoxSelectSeparator);
        radioGroupSelectSeparator = view.findViewById(R.id.radioGroupSelectSeparator);
        radioGroupSelectDateType = view.findViewById(R.id.radioGroupSelectDateType);
        radioButtonGregorianDate = view.findViewById(R.id.radioButtonGregorianDate);
        radioButtonJalaliDate = view.findViewById(R.id.radioButtonJalaliDate);
        radioButtonLine = view.findViewById(R.id.radioButtonLine);
        radioButtonDot = view.findViewById(R.id.radioButtonDot);
        radioButtonDash = view.findViewById(R.id.radioButtonDash);
        radioButtonUnderLine = view.findViewById(R.id.radioButtonUnderLine);
        buttonOK = view.findViewById(R.id.buttonOK);
        buttonNo = view.findViewById(R.id.buttonNo);

        textViewTitle.setText(context.getResources().getString(R.string.save_selected_notes));

        StringBuilder str= new StringBuilder();
        if(getSelectedNotes()!=null){
            for(int i=0;i<getSelectedNotes().size();i++){
                str.append(getSelectedNotes().get(i).getTitle()).append("\n");
            }
        }

        textViewText.setText(getSelectedNotes()==null?context.getResources().getString(R.string.save_selected_notes):
                String.format(context.getResources().getString(R.string.selected_notes), str));

        checkBoxSaveDate.setOnClickListener(view1 -> {
            radioGroupSelectDateType.setVisibility(checkBoxSaveDate.isChecked()?View.VISIBLE:View.GONE);
        });

        checkBoxSelectSeparator.setOnClickListener(view1 -> {
            radioGroupSelectSeparator.setVisibility(checkBoxSelectSeparator.isChecked()?View.VISIBLE:View.GONE);
        });

        buttonOK.setText(context.getResources().getString(R.string.ok));
        buttonNo.setText(context.getResources().getString(R.string.no));

        buttonOK.setOnClickListener(view1 ->
        {
            if(edittextFileName.getText().toString().length()==0){
                helper.showToast(context.getResources().getString(R.string.enter_file_name),1);
                return;
            }

            try {

                Dexter.withContext(context)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener()
                        {
                            @Override public void onPermissionGranted(PermissionGrantedResponse response)
                            {
                                FilePickerDialog dialog1 =new FilePickerDialog(context);
                                dialog1.setFileSelection(false);
                                dialog1.setStrTitle(getResources().getString(R.string.choose_folder));
                                dialog1.setStrMainPath(Environment.getExternalStorageDirectory().toString());
                                dialog1.setBackButtonText(getResources().getString(R.string.choose_folder));
                                dialog1.setOnClickButtonListener(new FilePickerDialog.OnClickButtonListener() {
                                    @Override
                                    public void chooseFolder(File folder) {

                                        File file = new File(folder,edittextFileName.getText().toString()+".txt");

                                        String string ="";
                                        for(int i=0;i<getSelectedNotes().size();i++)
                                        {
                                            StringBuilder itemBuilder= new StringBuilder();

                                            if(checkBoxSaveTitle.isChecked())
                                                itemBuilder.append(getSelectedNotes().get(i).getTitle()).append("\n");

                                            if(checkBoxSaveText.isChecked())
                                                itemBuilder.append(getSelectedNotes().get(i).getText()).append("\n");

                                            if(checkBoxSaveDate.isChecked()){
                                                if(radioButtonGregorianDate.isChecked())
                                                    itemBuilder.append(helper.getGregorianDate(getSelectedNotes().get(i).getaTime())).append("\n");
                                                if(radioButtonJalaliDate.isChecked())
                                                    itemBuilder.append(helper.getDate(getSelectedNotes().get(i).getaTime())).append("\n");
                                            }

                                            if(checkBoxSelectSeparator.isChecked()){
                                                if(radioButtonLine.isChecked())
                                                    itemBuilder.append("\n\n");
                                                if(radioButtonDot.isChecked())
                                                    itemBuilder.append("..");
                                                if(radioButtonDash.isChecked())
                                                    itemBuilder.append("--");
                                                if(radioButtonUnderLine.isChecked())
                                                    itemBuilder.append("__");
                                            }else itemBuilder.append("\n\n");

                                            string += itemBuilder.toString();
                                        }

                                        helper.saveTextToFile(file,string);
                                        helper.showToast(context.getResources().getString(R.string.save_selected_notes),3);
                                    }
                                    @Override
                                    public boolean chooseBack() {
                                        return false;
                                    }
                                });
                                dialog1.showDialog();
                            }
                            @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                                if(response.isPermanentlyDenied()){
                                    CustomDialog dialog1 =new CustomDialog(context);
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
                                CustomDialog dialog1 =new CustomDialog(context);
                                dialog1.setTitle(getString(R.string.get_write_permission));
                                dialog1.setText(getString(R.string.get_write_permission_text));
                                dialog1.setButtonOkText(getString(R.string.ok));
                                dialog1.setButtonNoText(getString(R.string.reject));
                                dialog1.setClickListener(new CustomDialog.ItemClickListener() {
                                    @Override
                                    public void onPositiveItemClick(View view1) {
                                        token.continuePermissionRequest();
                                    }
                                    @Override
                                    public void onNegativeItemClick(View view1) {
                                        helper.showToast(getString(R.string.permission_rejected),1);
                                    }
                                });
                                dialog1.setOnDismissListener(dialogInterface -> helper.showToast(getString(R.string.permission_rejected),1));
                                dialog1.showDialog();
                            }
                        })
                        .withErrorListener(dexterError -> helper.showToast(dexterError.toString(),1))
                        .check();
            }catch (Exception e){
                helper.showAlert(e.toString(),2);
            }

            dismiss();
        });

        buttonNo.setOnClickListener(view1 -> dismiss());

    }

    public BottomSheetSelectedNotes(Context context){
        this.context=context;
        helper = new HelperClass(context);
    }

    public ArrayList<Note> getSelectedNotes() {return selectedNotes;}

    public void setSelectedNotes(ArrayList<Note> selectedNotes) {this.selectedNotes = selectedNotes;}

}

