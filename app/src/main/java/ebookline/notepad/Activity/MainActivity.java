package ebookline.notepad.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ebookline.notepad.Adapter.CategoryAdapter2;
import ebookline.notepad.Adapter.NoteAdapter;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Dialogs.FilePickerDialog;
import ebookline.notepad.Dialogs.MenuDialog;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Menu;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.ThemeManager;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;
import ebookline.notepad.databinding.ActivityMainBinding;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements NoteAdapter.ItemClickListener,
        CategoryAdapter2.ItemClickListener, CategoryAdapter2.ItemLongClickListener {

    ActivityMainBinding main;

    private HelperClass helper;
    private DBHelper db;

    private List<Note> noteList;
    private List<Category> categoryList;

    private Note note;
    private Category category;

    NoteAdapter noteAdapter;
    CategoryAdapter2 categoryAdapter;

    private boolean isSearchMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.setTheme(this);
        super.onCreate(savedInstanceState);
        main = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(main.getRoot());

        helper = new HelperClass(this);
        db = new DBHelper(this);

        try {

            if(db.getNoteOldDatabase()){
                getNotesList(null,null);
                helper.showToast(getResources().getString(R.string.note_add_old_db),3);
            }else  getNotesList(null,null);

            getCategoriesList();

        }catch (Exception e){
            helper.exceptionHandler(e);
        }

        main.imageViewSearch.setOnClickListener(view -> {
            if(isSearchMode)
                return;
            isSearchMode = true;
            main.relativeToolbarSearch.setVisibility(View.VISIBLE);
            main.recyclerViewCategories.setVisibility(View.INVISIBLE);
        });

        main.imageViewSort.setOnClickListener(view ->
        {
            List<Menu> menuList=new ArrayList<>();

            menuList.add(new Menu(1,getResources().getString(R.string.sort_by_newest),R.drawable.sort));
            menuList.add(new Menu(2,getResources().getString(R.string.sort_by_oldest),R.drawable.sort));
            menuList.add(new Menu(3,getResources().getString(R.string.sort_by_title_asc),R.drawable.sort));
            menuList.add(new Menu(4,getResources().getString(R.string.sort_by_title_desc),R.drawable.sort));
            menuList.add(new Menu(5,getResources().getString(R.string.sort_by_text_acs),R.drawable.sort));
            menuList.add(new Menu(6,getResources().getString(R.string.sort_by_text_desc),R.drawable.sort));

            MenuDialog dialog = new MenuDialog(this);
            dialog.setList(menuList);
            dialog.setOnClickButtonListener(menu ->
            {
                isSearchMode = true;
                onBackPressed();

                if(menu.getId()==1)
                    getNotesList(null,Constants.ID+" desc");
                if(menu.getId()==2)
                    getNotesList(null,Constants.ID+" asc");

                if(menu.getId()==3)
                    getNotesList(null,Constants.TITLE+" desc");
                if(menu.getId()==4)
                    getNotesList(null,Constants.TITLE+" asc");

                if(menu.getId()==5)
                    getNotesList(null,Constants.TEXT+" desc");
                if(menu.getId()==6)
                    getNotesList(null,Constants.TEXT+" asc");
            });
            dialog.showDialog();

            if(main.menu.isOpened())
                main.menu.close(true);
        });

        main.imageViewMenu.setOnClickListener(view ->
        {
            List<Menu> menuList=new ArrayList<>();

            menuList.add(new Menu(1,getResources().getString(R.string.delete_notes),R.drawable.trash));
            menuList.add(new Menu(2,getResources().getString(R.string.settings),R.drawable.gear));
            menuList.add(new Menu(3,getResources().getString(R.string.about),R.drawable.exclamation));
            menuList.add(new Menu(4,getResources().getString(R.string.rate),R.drawable.star));
            menuList.add(new Menu(5,getResources().getString(R.string.backup),R.drawable.floppy));
            menuList.add(new Menu(6,getResources().getString(R.string.restore),R.drawable.refresh));

            MenuDialog dialog = new MenuDialog(this);
            dialog.setList(menuList);

            dialog.setOnClickButtonListener(menu ->
            {
                if(menu.getId()==1){
                    Intent toTrashActivity = new Intent(MainActivity.this,TrashActivity.class);
                    startActivity(toTrashActivity);

                }else if(menu.getId()==2){
                    Intent toSettingsActivity = new Intent(MainActivity.this,SettingsActivity.class);
                    startActivity(toSettingsActivity);
                    finish();
                }else if(menu.getId()==3){
                    Intent toAboutActivity = new Intent(MainActivity.this,AboutActivity.class);
                    startActivity(toAboutActivity);
                }else if(menu.getId()==4){
                    try{
                        Intent rate = new Intent();

                        if(Constants.bazzar_or_myket)
                            rate.setAction(Intent.ACTION_EDIT);
                        else rate.setAction(Intent.ACTION_VIEW);

                        if(Constants.bazzar_or_myket)
                            rate.setData(Uri.parse("bazaar://details?id=" + getPackageName()));
                        else rate.setData(Uri.parse("myket://comment?id="+getPackageName()));

                        if(Constants.bazzar_or_myket)
                            rate.setPackage("com.farsitel.bazaar");
                        else rate.setPackage("ir.mservices.market");

                        startActivity(rate);

                    }catch(Exception e){
                        helper.showToast(e.toString(),2);
                    }
                }else if(menu.getId()==5){

                    try {
                        Dexter.withContext(MainActivity.this)
                                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener()
                                {
                                    @Override public void onPermissionGranted(PermissionGrantedResponse response)
                                    {
                                        FilePickerDialog dialog1 =new FilePickerDialog(MainActivity.this);
                                        dialog1.setFileSelection(false);
                                        dialog1.setStrTitle(getResources().getString(R.string.choose_folder));
                                        dialog1.setStrMainPath(Environment.getExternalStorageDirectory().toString());
                                        dialog1.setBackButtonText(getResources().getString(R.string.choose_folder));
                                        dialog1.setOnClickButtonListener(new FilePickerDialog.OnClickButtonListener() {
                                            @Override
                                            public void chooseFolder(File folder) {

                                                File dbPath = new File(db.getPath());
                                                File dbDest = new File(folder,Constants.DB_NAME);

                                                if(helper.copyFile(dbPath,dbDest)){
                                                    helper.showToast(getResources().getString(R.string.copy_database),3);
                                                }
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
                                            CustomDialog dialog1 =new CustomDialog(MainActivity.this);
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
                                        CustomDialog dialog1 =new CustomDialog(MainActivity.this);
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

                }else if(menu.getId()==6) {
                    if (!helper.hasReadPermission() || !helper.hasWritePermission()) {
                        return;
                    }

                    FilePickerDialog dialog1 = new FilePickerDialog(MainActivity.this);
                    dialog1.setFileSelection(true);
                    dialog1.setTypeShowFiles(new String[]{"db"});
                    dialog1.setStrTitle(getResources().getString(R.string.choose_database));
                    dialog1.setBackButtonText(getResources().getString(R.string.restore));
                    dialog1.setOnClickButtonListener(new FilePickerDialog.OnClickButtonListener() {
                        @Override
                        public void chooseFolder(File folder) {
                            if (db.restoreDatabase(folder)) {
                                helper.showToast(getResources().getString(R.string.notes_restore), 3);
                                getNotesList(null, null);
                            } else helper.showToast(getResources().getString(R.string.notes_restore_error), 2);
                        }

                        @Override
                        public boolean chooseBack() {
                            return false;
                        }
                    });
                    dialog1.showDialog();
                }
            });

            dialog.showDialog();

            if(main.menu.isOpened())
                main.menu.close(true);
        });

        main.menuItemAddNote.setOnClickListener(view -> {
            main.menu.close(true);
            Intent toNoteActivity = new Intent(this,NoteActivity.class);
            startActivity(toNoteActivity);
        });

        main.menuItemAddCategory.setOnClickListener(view -> {

            addCategory(null);

            main.menu.close(true);
        });

        main.edittextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(Objects.requireNonNull(main.edittextText.getText()).toString().length()==0)
                    return;

                if(isSearchMode)
                    getNotesList(
                        Constants.TITLE+" LIKE '%"+charSequence.toString()+"%' OR "
                                +Constants.TEXT+" LIKE '%"+charSequence+"%'",null);

            }
        });

        main.recyclerNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(main.menu.isShown())
                        main.menu.hideMenu(true);
                }else
                    if(!main.menu.isShown())
                        main.menu.showMenu(true);
            }
        });

    }

    private void getNotesList(String selection , String sort) {

        noteList = db.getNotes(selection,sort);

        noteAdapter = new NoteAdapter(this,noteList);
        noteAdapter.setClickListener(this);
        main.recyclerNotes.setAdapter(noteAdapter);

        if(noteAdapter.getItemCount()==0)
            main.relativeEmptyList.setVisibility(View.VISIBLE);
        else main.relativeEmptyList.setVisibility(View.GONE);

    }

    private void getCategoriesList()
    {
        categoryList = new ArrayList<>();

        categoryList.add(new Category(-1 ,0,getResources().getString(R.string.all),"#ffffff"));

        categoryList.addAll(db.getCategories());

        for(Category category : categoryList){

            int count;
            if(category.getId()== -1)
                count = db.count(Constants.TBL_NOTE_NAME,Constants.DELETED+"=0");
            else count = db.count(Constants.TBL_NOTE_NAME,Constants.CATEGORY+"="+category.getId()+" AND "+Constants.DELETED+"=0");

            category.setTitle(String.format(Locale.ROOT,"%s(%d)", category.getTitle(),count));
        }

        categoryAdapter = new CategoryAdapter2(this,categoryList);
        categoryAdapter.setClickListener(this);
        categoryAdapter.setLongClickListener(this);
        main.recyclerViewCategories.setAdapter(categoryAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        note = noteAdapter.getItem(position);
        Intent toNoteActivity = new Intent(this,NoteViewActivity.class);
        toNoteActivity.putExtra("id",note.getId());
        startActivity(toNoteActivity);
    }

    @Override
    public void onCategoryItemClick(View view, int position) {
        if(position==0)
            getNotesList(null,null);
        else getNotesList(Constants.CATEGORY+"="+categoryAdapter.getItem(position).getId(),null);
    }

    @Override
    public void onCategoryItemLongClick(View view, int position)
    {
        if(position==0||position==1)
            return;

        category = categoryList.get(position);

        CustomDialog dialog = new CustomDialog(MainActivity.this);
        dialog.setTitle(getResources().getString(R.string.options));
        dialog.setText(getResources().getString(R.string.category)+" "+ category.getTitle().substring(0,category.getTitle().indexOf("(")));
        dialog.setButtonOkText(getResources().getString(R.string.category_delete));
        dialog.setButtonNoText(getResources().getString(R.string.category_edit));

        dialog.setClickListener(new CustomDialog.ItemClickListener() {
            @Override
            public void onPositiveItemClick(View view) {
                int iDelete = db.deleteCategory(category);
                if(iDelete>0) {
                    helper.showToast(getResources().getString(R.string.category_delete_success), 3);
                    getNotesList(null,null);
                    getCategoriesList();
                }
                else helper.showToast(getResources().getString(R.string.category_delete_denied),2);
            }
            @Override
            public void onNegativeItemClick(View view) {
                addCategory(category);
            }
        });

        dialog.showDialog();
    }

    private void addCategory(Category newCategory)
    {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View addCategoryView = View.inflate(this,R.layout.layout_bottom_sheet_add_category,null);

        EditText editTextCategoryTitle = addCategoryView.findViewById(R.id.edittextTitle);
        Button buttonAddCategory = addCategoryView.findViewById(R.id.buttonAddCategory);

        if(newCategory!=null) {
            editTextCategoryTitle.setText(newCategory.getTitle().substring(0, newCategory.getTitle().indexOf("(")));
            buttonAddCategory.setText(getString(R.string.category_edit));
        }

        buttonAddCategory.setOnClickListener(view1 -> {

            if(editTextCategoryTitle.getText().toString().length()==0){
                helper.showToast(getResources().getString(R.string.enter_title),2);
                return;
            }

            if(newCategory!=null)
                category=newCategory;
            else category = new Category();

            category.setTitle(editTextCategoryTitle.getText().toString());
            category.setColor(Constants.categoryColorsList.get(0));
            category.setParent(0);

            if(newCategory==null){
                if(db.addCategory(category)>0){
                    helper.showToast(getResources().getString(R.string.category_add_successfully),3);
                }else helper.showToast(getResources().getString(R.string.category_add_denied),2);
            }else{
                if(db.updateCategory(category)>0){
                    helper.showToast(getResources().getString(R.string.category_edit_successfully),3);
                }else helper.showToast(getResources().getString(R.string.category_edit_denied),2);
            }

            getCategoriesList();
            getNotesList(null,null);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(addCategoryView);
        bottomSheetDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotesList(null,null);
        getCategoriesList();

        if(main.menu.isOpened())
            main.menu.close(true);
    }

    @Override
    public void onBackPressed() {
        if(isSearchMode){
            isSearchMode = false;
            main.relativeToolbarSearch.setVisibility(View.GONE);
            main.edittextText.setText("");
            main.recyclerViewCategories.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


}