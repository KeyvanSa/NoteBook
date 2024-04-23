package ebookline.notepad.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Dialogs.FilePickerDialog;
import ebookline.notepad.Dialogs.MenuDialog;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Menu;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityNoteViewBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NoteViewActivity extends AppCompatActivity
{
    ActivityNoteViewBinding noteViewBinding;

    DBHelper db;
    SharedHelper shared;
    HelperClass helper;

    Note note;
    String strTitle , strText , strColor , strATime , strCTime;
    int id=0 , iCategory=0 , iPin=0;

    private boolean isSearchMode = false;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        noteViewBinding=ActivityNoteViewBinding.inflate(getLayoutInflater());
        setContentView(noteViewBinding.getRoot());

        db=new DBHelper(this);
        shared=new SharedHelper(this);
        helper=new HelperClass(this);

        try{
            getNote();
        }catch (Exception e){
            helper.exceptionHandler(e);
        }

        noteViewBinding.textViewText.setTextColor(Color.parseColor( (shared.getString(Constants.TEXT_COLOR)==null) ?
                Constants.TextColorsList.get(0) : shared.getString(Constants.TEXT_COLOR)));

        noteViewBinding.imageViewSearch.setOnClickListener(view -> {
            if(isSearchMode)
                return;
            isSearchMode = true;
            noteViewBinding.relativeToolbarSearch.setVisibility(View.VISIBLE);
        });

        noteViewBinding.imageViewPin.setOnClickListener(view -> {
            if(iPin==0)
                iPin=1;
            else iPin=0;

            long lPin = db.pinNote(id,iPin);

            if(iPin==1)
                noteViewBinding.imageViewPin.setImageResource(R.drawable.bookmark);
            else noteViewBinding.imageViewPin.setImageResource(R.drawable.unbookmark);

            if (lPin==1&&iPin==0)
                helper.showToast(getResources().getString(R.string.unpined),3);
            else if (lPin==1&&iPin==1)
                helper.showToast(getResources().getString(R.string.pined),3);

        });

        noteViewBinding.imageViewEdit.setOnClickListener(view -> {
            Intent toNoteActivity = new Intent(this,NoteActivity.class);
            toNoteActivity.putExtra("id",id);
            startActivity(toNoteActivity);
        });

        noteViewBinding.imageViewDelete.setOnClickListener(view -> {
            CustomDialog dialog = new CustomDialog(this);
            dialog.setTitle(String.format(getResources().getString(R.string.note_delete),""));
            dialog.setText(strTitle +"\n"+String.format(getResources().getString(R.string.note_delete),"?"));
            dialog.setButtonOkText(String.format(getResources().getString(R.string.note_delete),""));
            dialog.setButtonNoText(getResources().getString(R.string.no));
            dialog.setClickListener(new CustomDialog.ItemClickListener() {
                @Override
                public void onPositiveItemClick(View view) {
                    int iDelete = (int)db.addNoteToTrash(note);
                    if(iDelete>0)
                        helper.showToast(getResources().getString(R.string.note_delete_success),3);
                    else helper.showToast(getResources().getString(R.string.note_delete_denied),2);
                    finish();
                }
                @Override
                public void onNegativeItemClick(View view) {}
            });
            dialog.showDialog();
        });

        noteViewBinding.imageViewMenu.setOnClickListener(view -> {

            List<Menu> list = new ArrayList<>();
            list.add(new Menu(1,getResources().getString(R.string.share),R.drawable.share));
            list.add(new Menu(2,getResources().getString(R.string.save),R.drawable.floppy));
            list.add(new Menu(3,getResources().getString(R.string.copy),R.drawable.widget));

            MenuDialog dialog = new MenuDialog(this);
            dialog.setList(list);
            dialog.setOnClickButtonListener(menu -> {
                if(menu.getId()==1){
                    helper.shareText(strText);
                }else if(menu.getId()==2){

                    try {
                        Dexter.withContext(NoteViewActivity.this)
                                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener()
                                {
                                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                                    {
                                        FilePickerDialog dialog1=new FilePickerDialog(NoteViewActivity.this);
                                        dialog1.setFileSelection(false);
                                        dialog1.setStrTitle(getResources().getString(R.string.choose_folder));
                                        dialog1.setBackButtonText(getResources().getString(R.string.choose_folder));
                                        dialog1.setOnClickButtonListener(new FilePickerDialog.OnClickButtonListener() {
                                            @Override
                                            public void chooseFolder(File folder) {

                                                File file = new File(folder,strTitle+".txt");

                                                helper.saveTextToFile(file,strText);
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
                                            CustomDialog dialog1 =new CustomDialog(NoteViewActivity.this);
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
                                        CustomDialog dialog1 =new CustomDialog(NoteViewActivity.this);
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

                }else if(menu.getId()==3){
                    helper.copyToClipboard(strText);
                }
            });
            dialog.show(this.getSupportFragmentManager(),dialog.getTag());
        });

        noteViewBinding.edittextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().length()==0) {
                    noteViewBinding.textViewText.setText(strText);
                    return;
                }

                s = s.toString().replaceAll("\\*","");
                s = s.toString().replaceAll("\\%","");
                s = s.toString().replaceAll("\\&","");

               try {
                   helper.searchText(noteViewBinding.textViewText,s.toString());
               }catch (Exception e){helper.showToast(e.toString(),2);}
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }

    private void getNote(){
        Bundle bundle = getIntent().getExtras();
        if(bundle==null||!bundle.containsKey("id")){
            return;
        }

        id = bundle.getInt("id");

        note = db.getNote(id);
        strTitle = note.getTitle();
        strText = note.getText();
        strColor = note.getColor();
        strATime = note.getaTime();
        strCTime = note.getcTime();
        iCategory = note.getCategory();
        iPin = note.getPin();

        noteViewBinding.textViewTitle.setText(strTitle);
        noteViewBinding.textViewText.setText(strText);

        noteViewBinding.textViewDate.setText(String.format("%s(%s)",helper.getDate(note.getaTime()),helper.getDisplayableTime(Long.parseLong(note.getaTime()))));

        Category category = new Category();
        if(note.getCategory()!=0)
            category=db.getCategory(note.getCategory());
        else category.setTitle(getResources().getString(R.string.no_category));

        noteViewBinding.textViewCategory.setText(category.getTitle());

        if(iPin==1)
            noteViewBinding.imageViewPin.setImageResource(R.drawable.bookmark);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shape.setColor(Color.parseColor(strColor));

        try{
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.textColor , typedValue, true);
            @ColorInt
            int color = typedValue.data;
            shape.setStroke(5,color);
        }catch (Exception ignored){}

        noteViewBinding.linearLayoutCategory.setBackground(shape);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            getNote();
        }catch (Exception e){
            helper.exceptionHandler(e);
        }
    }

    @Override
    public void onBackPressed() {
        if(isSearchMode){
            noteViewBinding.relativeToolbarSearch.setVisibility(View.GONE);
            noteViewBinding.textViewText.setText(strText);
            noteViewBinding.edittextText.setText("");
        }else{
            super.onBackPressed();
        }
        isSearchMode = !isSearchMode;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}