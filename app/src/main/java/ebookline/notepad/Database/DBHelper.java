package ebookline.notepad.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Dialogs.CustomDialog;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.Model.Task;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class DBHelper
{
    private final DBCreator dbCreator;
    private SQLiteDatabase db;

    private HelperClass helper;

    public void autoDeleteExpiredNotes(Context context){
        List<Note> list = getTrashNotes();

        List<Note> deletedList = new ArrayList<>();

        for (Note note : list){

            int leftDays = 29 -
                    (int)(((java.lang.System.currentTimeMillis() -
                            Long.parseLong(note.getaTime()) )/1000)/(24*60*60));

            if(leftDays<1)
                deletedList.add(note);

        }

        if(deletedList.size()==0)
            return;

        CustomDialog dialog = new CustomDialog(context);
        dialog.setTitle(context.getResources().getString(R.string.delete_expired_notes));
        dialog.setText(String.format(context.getResources().getString(R.string.sure_delete_expired_notes),String.valueOf(deletedList.size())));
        dialog.setButtonOkText(context.getResources().getString(R.string.ok));
        dialog.setButtonNoText(context.getResources().getString(R.string.no));
        dialog.setClickListener(new CustomDialog.ItemClickListener() {
            @Override
            public void onPositiveItemClick(View view) {
                for (Note note:deletedList)
                    deleteTrashNote(note);
            }
            @Override
            public void onNegativeItemClick(View view) {
                SharedHelper shared = new SharedHelper(context);
                shared.removeValue(Constants.USE_EXPIRED_NOTE);
            }
        });
        dialog.showDialog();
    }

    public int count(String tblName,String selection){

        if (tblName==null||TextUtils.isEmpty(tblName)) return 0;

        dbOpen();
        Cursor cursor = db.query(tblName,null,selection,null,null,null,
                null);
        int count = cursor.getCount();

        dbClose();
        cursor.close();
        return count;
    }

    public String getPath(){
        dbOpen();
        String path = db.getPath();
        dbClose();
        return path;
    }

    public boolean restoreDatabase(File file)
    {
        if(file==null)
            return false;

        SQLiteDatabase database = null;
        String TBL_NOTE_NAME = null;

        if(file.getName().equals("notebook"))
            TBL_NOTE_NAME = "tblnote";
        else  TBL_NOTE_NAME = Constants.TBL_NOTE_NAME;

        try{
            database = SQLiteDatabase.openOrCreateDatabase(file,null);
        }catch (Exception ignored){return false;}

        if (database==null || !database.isOpen())
            return false;

        @SuppressLint("Recycle")
        Cursor cursor=null;

        /// cursor for Notes
        cursor=database.query(TBL_NOTE_NAME,null,null,null,null,null,null);
        if(cursor==null)
            return false;

        while (cursor.moveToNext()){
            Note note = new Note();
            note.setTitle(cursor.getString(1));
            note.setText(cursor.getString(2));
            note.setCategory(0);

            if(cursor.getColumnCount()<8){ // db is notebook database file
                note.setColor(Constants.categoryColorsList.get(0));
                note.setaTime(cursor.getString(4));
                note.setcTime(cursor.getString(4));
                note.setPin(0);
                note.setDeleted(0);
            }else{
                note.setCategory(cursor.getInt(3));
                note.setColor(cursor.getString(4));
                note.setaTime(cursor.getString(5));
                note.setcTime(cursor.getString(6));
                note.setPin(cursor.getInt(7));
                note.setDeleted(cursor.getInt(8));
            }

            addNote(note);
        }

        if(cursor.moveToFirst() && cursor.moveToNext() && cursor.getColumnCount()>=8){
            // add categories
            cursor=database.query(Constants.TBL_CATEGORY_NAME,null,null,null,null,null,null);

            if(cursor==null)
                return false;

            dbOpen();
            while (cursor.moveToNext()){

                ContentValues cv = new ContentValues();
                cv.put(Constants.ID,cursor.getInt(0));
                cv.put(Constants.TITLE,cursor.getString(1));
                cv.put(Constants.COLOR,cursor.getString(2));

                long result = db.insert(Constants.TBL_CATEGORY_NAME,null,cv);
            }
            dbClose();

            // add tasks
            cursor=database.query(Constants.TBL_TASK_NAME,null,null,null,null,null,null);

            if(cursor==null)
                return false;

            while (cursor.moveToNext()){
                Task task = new Task();

                task.setId(cursor.getInt(0));
                task.setTitle(cursor.getString(1));
                task.setColor(cursor.getString(2));
                task.setCheck(cursor.getInt(3));

                addTask(task);
            }
        }

        return true;
    }

    public boolean getNoteOldDatabase()
    {
        File dbPath = new File(getPath());
        File[] files = new File(dbPath.getParent()).listFiles();
        String strDBFile =null;
        for (File file : files) {
            if(file.getName().equals("notebook")){
                strDBFile = file.getPath();
            }
        }
        if(strDBFile==null)
            return false;

        SQLiteDatabase db = null;
        try{

            File dbFile = new File(strDBFile);
            if(!dbFile.isFile() || !dbFile.exists())
                return false;

            db = SQLiteDatabase.openOrCreateDatabase(dbFile,null);

            if (db==null || !db.isOpen())
                return false;

            @SuppressLint("Recycle")
            Cursor cursor = db.query("tblnote",null,null,null,null,null,null);
            if(cursor==null)
                return false;

            String str = "";
            while (cursor.moveToNext()){
                Note note = new Note();
                note.setTitle(cursor.getString(1));
                note.setText(cursor.getString(2));
                note.setaTime(cursor.getString(4));
                note.setcTime(cursor.getString(4));
                note.setCategory(0);
                note.setColor(Constants.categoryColorsList.get(0));
                addNote(note);
            }

            if(dbFile.exists())
                return dbFile.delete();
        }catch (Exception e){
            helper.exceptionHandler(e);
        }
        return false;
    }

    ///////////// Task Start /////////////////
    public List<Task> getTasks(){
        List<Task> list = new ArrayList<>();

        dbOpen();
        Cursor cursor=db.query(Constants.TBL_TASK_NAME,null,null,null,null,null,
                Constants.IS_CHECKED+" asc");

        while (cursor.moveToNext()){
            Task task = new Task();
            task.setId(cursor.getInt(0));
            task.setTitle(cursor.getString(1));
            task.setColor(cursor.getString(2));
            task.setCheck(cursor.getInt(3));

            list.add(task);
        }

        dbClose();
        cursor.close();
        return list;
    }

    public Task getTask(int id){
        dbOpen();
        Cursor cursor=db.query(Constants.TBL_TASK_NAME,null,Constants.ID+"=?",new String[]{String.valueOf(id)},null,null, null);

        Task task = new Task();

        if(!cursor.moveToFirst())
            return task;

        task.setId(cursor.getInt(0));
        task.setTitle(cursor.getString(1));
        task.setColor(cursor.getString(2));
        task.setCheck(cursor.getInt(3));

        dbClose();
        cursor.close();
        return task;
    }

    public long addTask(Task task){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,task.getTitle());
        cv.put(Constants.COLOR,task.getColor());
        cv.put(Constants.IS_CHECKED,task.getCheck());

        dbOpen();
        long result = db.insert(Constants.TBL_TASK_NAME,null,cv);
        dbClose();
        return result;
    }

    public long updateTask(Task task){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,task.getTitle());
        cv.put(Constants.COLOR,task.getColor());
        cv.put(Constants.IS_CHECKED,task.getCheck());

        dbOpen();
        long result = db.update(Constants.TBL_TASK_NAME,cv,Constants.ID+"=?",new String[]{String.valueOf(task.getId())});
        dbClose();
        return result;
    }

    public int deleteTask(Task task){

        dbOpen();
        int result = db.delete(Constants.TBL_TASK_NAME,Constants.ID+"=?",new String[]{String.valueOf(task.getId())});
        dbClose();

        return result;
    }
    ///////////// Task End //////////////////

    ///////////// Category Start //////////////////
    public List<Category> getCategories(){
        List<Category> list = new ArrayList<>();
        list.add(new Category(0,0,context.getResources().getString(R.string.no_category),Constants.categoryColorsList.get(0)));

        dbOpen();
        Cursor cursor=db.query(Constants.TBL_CATEGORY_NAME,null,null,null,null,null,
                null);

        while (cursor.moveToNext()){
            Category category = new Category();
            category.setId(cursor.getInt(0));
            category.setTitle(cursor.getString(1));
            category.setColor(cursor.getString(2));
            category.setParent(cursor.getInt(3));
            list.add(category);
        }

        dbClose();
        cursor.close();
        return list;
    }

    public long addCategory(Category category){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,category.getTitle());
        cv.put(Constants.COLOR,category.getColor());
        cv.put(Constants.PARENT,category.getParent());

        dbOpen();
        long result = db.insert(Constants.TBL_CATEGORY_NAME,null,cv);
        dbClose();
        return result;
    }

    public Category getCategory(int id){
        dbOpen();
        Cursor cursor=db.query(Constants.TBL_CATEGORY_NAME,null,Constants.ID+"=?",new String[]{String.valueOf(id)},null,null, null);

        Category category = new Category();

        if(!cursor.moveToFirst())
            return category;

        category.setId(cursor.getInt(0));
        category.setTitle(cursor.getString(1));
        category.setColor(cursor.getString(2));
        category.setParent(cursor.getInt(3));

        dbClose();
        cursor.close();
        return category;
    }

    public long updateCategory(Category category){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,category.getTitle());
        cv.put(Constants.COLOR,category.getColor());
        cv.put(Constants.PARENT,category.getParent());

        dbOpen();
        long result = db.update(Constants.TBL_CATEGORY_NAME,cv,Constants.ID+"=?",new String[]{String.valueOf(category.getId())});
        dbClose();
        return result;
    }

    public int deleteCategory(Category category){

        dbOpen();
        int result = db.delete(Constants.TBL_CATEGORY_NAME,Constants.ID+"=?",new String[]{String.valueOf(category.getId())});
        dbClose();

        if(result==1){
            List<Note> list = getNotes(null,null);
            for(Note note : list){
                if(note.getCategory()==category.getId()){
                    note.setCategory(0);
                    result=(int)updateNote(note);
                }
            }
        }
        return result;
    }
    ///////////// Category End //////////////////

    ///////////// Notes Start //////////////////
    public List<Note> getNotes(String selection,String sort){
        List<Note> list = new ArrayList<>();

        if(sort==null)
            sort = Constants.PIN+" desc";
        else sort += ","+Constants.PIN+" desc";

        if(selection==null)
            selection = Constants.DELETED+"=0";
        else selection += " AND "+ Constants.DELETED+"=0";

        dbOpen();
        Cursor cursor=db.query(Constants.TBL_NOTE_NAME,null,selection,null,null,null,
                sort);

        while (cursor.moveToNext()){
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setText(cursor.getString(2));
            note.setCategory(cursor.getInt(3));
            note.setColor(cursor.getString(4));
            note.setaTime(cursor.getString(5));
            note.setcTime(cursor.getString(6));
            note.setPin(cursor.getInt(7));
            note.setDeleted(cursor.getInt(8));
            /*
            To avoid displaying deleted notes
             because the SQL condition may not work correctly*/
            if(note.getDeleted()==0)
                list.add(note);
        }

        dbClose();
        cursor.close();
        return list;
    }

    public long addNote(Note note){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,note.getTitle());
        cv.put(Constants.TEXT,note.getText());
        cv.put(Constants.CATEGORY,note.getCategory());
        cv.put(Constants.COLOR,note.getColor());
        cv.put(Constants.ATIME,note.getaTime());
        cv.put(Constants.ETIME,note.getcTime());
        cv.put(Constants.PIN,note.getPin());
        cv.put(Constants.DELETED,note.getDeleted());

        dbOpen();
        long result = db.insert(Constants.TBL_NOTE_NAME,null,cv);
        dbClose();
        return result;
    }

    public Note getNote(int id){
        dbOpen();
        Cursor cursor=db.query(Constants.TBL_NOTE_NAME,null,Constants.ID+"=?",new String[]{String.valueOf(id)},null,null, null);

        Note note = new Note();

        if(!cursor.moveToFirst())
            return note;

        note.setId(cursor.getInt(0));
        note.setTitle(cursor.getString(1));
        note.setText(cursor.getString(2));
        note.setCategory(cursor.getInt(3));
        note.setColor(cursor.getString(4));
        note.setaTime(cursor.getString(5));
        note.setcTime(cursor.getString(6));
        note.setPin(cursor.getInt(7));
        note.setDeleted(cursor.getInt(8));

        dbClose();
        cursor.close();
        return note;
    }

    public long updateNote(Note note){
        ContentValues cv = new ContentValues();
        cv.put(Constants.TITLE,note.getTitle());
        cv.put(Constants.TEXT,note.getText());
        cv.put(Constants.CATEGORY,note.getCategory());
        cv.put(Constants.COLOR,note.getColor());
        cv.put(Constants.ATIME,note.getaTime());
        cv.put(Constants.ETIME,note.getcTime());
        cv.put(Constants.PIN,note.getPin());
        cv.put(Constants.DELETED,note.getDeleted());

        dbOpen();
        long result = db.update(Constants.TBL_NOTE_NAME,cv,Constants.ID+"=?",new String[]{String.valueOf(note.getId())});
        dbClose();
        return result;
    }

    public int deleteNote(Note note){
        dbOpen();
        int result = db.delete(Constants.TBL_NOTE_NAME,Constants.ID+"=?",new String[]{String.valueOf(note.getId())});
        dbClose();
        return result;
    }

    public long pinNote(int id,int pin){
        ContentValues cv = new ContentValues();
        cv.put(Constants.PIN,pin);

        dbOpen();
        long result = db.update(Constants.TBL_NOTE_NAME,cv,Constants.ID+"=?",new String[]{String.valueOf(id)});
        dbClose();
        return result;
    }
    ///////////// Notes End //////////////////

    ///////////// Deleted Notes Start //////////////////
    public List<Note> getTrashNotes(){

        List<Note> list = new ArrayList<>();

        dbOpen();
        Cursor cursor=db.query(Constants.TBL_NOTE_NAME,null,Constants.DELETED+"=?",new String[]{"1"},null,null,
                Constants.ID+" desc");

        while (cursor.moveToNext()){
            Note note = new Note();
            note.setId(cursor.getInt(0));
            note.setTitle(cursor.getString(1));
            note.setText(cursor.getString(2));
            note.setCategory(cursor.getInt(3));
            note.setColor(cursor.getString(4));
            note.setaTime(cursor.getString(5));
            note.setcTime(cursor.getString(6));
            note.setPin(cursor.getInt(7));
            note.setDeleted(cursor.getInt(8));
            list.add(note);
        }

        dbClose();
        cursor.close();
        return list;
    }

    public long addNoteToTrash(Note note){

        note.setaTime(String.valueOf(java.lang.System.currentTimeMillis()));
        note.setcTime(String.valueOf(java.lang.System.currentTimeMillis()));
        note.setCategory(0);
        note.setDeleted(1);

        dbOpen();
        long result = updateNote(note);
        dbClose();

        return result;
    }

    public long restoreTrashNote(Note note){

        note.setaTime(String.valueOf(java.lang.System.currentTimeMillis()));
        note.setcTime(String.valueOf(java.lang.System.currentTimeMillis()));
        note.setDeleted(0);

        dbOpen();
        long result =updateNote(note);
        dbClose();

        return result;
    }

    public int deleteTrashNote(Note note){
        dbOpen();
        int result = db.delete(Constants.TBL_NOTE_NAME,Constants.ID+"=?",new String[]{String.valueOf(note.getId())});
        dbClose();
        return result;
    }
    ///////////// Deleted Notes End //////////////////

    private void dbClose(){
        dbCreator.close();
    }

    private void dbOpen(){
        db = dbCreator.getWritableDatabase();
    }

    private Context context;
    public DBHelper(Context context){
        this.context = context;
        dbCreator=new DBCreator(this.context);
        helper = new HelperClass(this.context);
    }

}
