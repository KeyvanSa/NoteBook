package ebookline.notepad.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import ebookline.notepad.Adapter.CategoryAdapter;
import ebookline.notepad.Adapter.ColorAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityNoteBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NoteActivity extends AppCompatActivity implements ColorAdapter.ItemClickListener
{
    ActivityNoteBinding noteBinding;
    DBHelper db;
    HelperClass helper;

    List<Category> categoriesList;

    ColorAdapter colorAdapter;

    String strTitle , strText , strColor , strATime , strCTime;
    int id=0 , iCategory=0 , iPin=0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        noteBinding = ActivityNoteBinding.inflate(getLayoutInflater());
        setContentView(noteBinding.getRoot());

        db=new DBHelper(this);
        helper=new HelperClass(this);

        try{
            getCategories();
            setColors(0);
            getNote();
        }catch (Exception e){
            helper.exceptionHandler(e);
        }

        noteBinding.buttonAddNote.setOnClickListener(view -> addNote());

    }

    private void addNote() {
        strTitle = Objects.requireNonNull(noteBinding.edittextTitle.getText()).toString();
        strText = Objects.requireNonNull(noteBinding.edittextText.getText()).toString();

        if(id==0)
            strATime = ""+java.lang.System.currentTimeMillis();

        strCTime = ""+java.lang.System.currentTimeMillis();

        if(strTitle==null||TextUtils.isEmpty(strTitle)){
            helper.showToast(getResources().getString(R.string.enter_title),1);
            return;
        }

        if(strText==null|| TextUtils.isEmpty(strText)){
            helper.showToast(getResources().getString(R.string.enter_text),1);
            return;
        }

        if(strColor==null|| TextUtils.isEmpty(strColor))
            strColor = Constants.categoryColorsList.get(0);

        Note note = new Note();
        note.setId(id);
        note.setTitle(strTitle);
        note.setText(strText);
        note.setCategory(iCategory);
        note.setColor(strColor);
        note.setaTime(strATime);
        note.setcTime(strCTime);
        note.setPin(iPin);

        if(id==0)
        {
            if(db.addNote(note)>0) {
                helper.showToast(getResources().getString(R.string.note_add_successfully), 3);
                finish();
            } else helper.showToast(getResources().getString(R.string.note_add_denied),2);
        }else{
            if(db.updateNote(note)>0) {
                helper.showToast(getResources().getString(R.string.note_edit_successfully), 3);
                finish();
            } else helper.showToast(getResources().getString(R.string.note_edit_denied),2);
        }
    }

    private void getNote(){
        Bundle bundle = getIntent().getExtras();
        if(bundle==null||!bundle.containsKey("id")){
            getSharedIntent();
            return;
        }

        id = bundle.getInt("id");

        Note note = db.getNote(id);
        strTitle = note.getTitle();
        strText = note.getText();
        strColor = note.getColor();
        strATime = note.getaTime();
        strCTime = note.getcTime();
        iCategory = note.getCategory();
        iPin = note.getPin();

        noteBinding.edittextTitle.setText(strTitle);
        noteBinding.edittextText.setText(strText);

        noteBinding.buttonAddNote.setText(getResources().getString(R.string.note_edit));

        for(int i=0;i<Constants.categoryColorsList.size();i++){
            if(strColor.equals(Constants.categoryColorsList.get(i)))
                setColors(i);
        }

        if(iCategory==0)
            noteBinding.spinnerCategory.setText(categoriesList.get(0).getTitle());
        else
            for(int i=0;i<categoriesList.size();i++){
                if(iCategory==categoriesList.get(i).getId()){
                    noteBinding.spinnerCategory.setText(categoriesList.get(i).getTitle());
                }
            }

    }

    private void getSharedIntent() {
        Intent receivedIntent=getIntent();
        if(receivedIntent==null)return;
        String receivedAction = receivedIntent.getAction();
        if (receivedAction==null ||receivedAction.equals(""))return;
        String receivedType = receivedIntent.getType();
        if (receivedType==null ||receivedType.equals(""))return;

        if(!receivedAction.equals(Intent.ACTION_SEND)) return;

        if(!receivedType.startsWith("text/")) return;

        String strText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (strText==null ||strText.equals(""))return;

        noteBinding.edittextText.setText(strText);
    }

    private void getCategories(){
        categoriesList = db.getCategories();

        CategoryAdapter categoryAdapter=
                new CategoryAdapter(this, R.layout.item_category , R.id.textViewTitle,categoriesList);
        noteBinding.spinnerCategory.setAdapter(categoryAdapter);

        noteBinding.spinnerCategory.setOnItemClickListener((adapterView, view, i, l) ->
                iCategory = categoriesList.get(i).getId());
    }

    private void setColors(int selectedItem)
    {
        colorAdapter = new ColorAdapter(this,Constants.categoryColorsList,selectedItem);
        colorAdapter.setClickListener(this);
        noteBinding.recyclerViewChooseColor.setAdapter(colorAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        strColor = colorAdapter.getItem(position);
        setColors(position);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}