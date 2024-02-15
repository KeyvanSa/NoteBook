package ebookline.notepad.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class DBHelper
{
    private final DBCreator dbCreator;
    private SQLiteDatabase db;

    private HelperClass helper;

    public void autoDeleteExpiredNotes(){

        List<Note> list = getTrashNotes();

        for(Note note : list){
            if((Long.parseLong(note.getaTime()) +
                    (Constants.DAYS_PAST_TO_DELETE_TRASH_NOTES*24*60*60*1000))
                    > java.lang.System.currentTimeMillis())

                deleteTrashNote(note);
        }

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

        SQLiteDatabase db = null;
        String TBL_NOTE_NAME = null;

        if(file.getName().equals("notebook"))
            TBL_NOTE_NAME = "tblnote";
        else  TBL_NOTE_NAME = Constants.TBL_NOTE_NAME;

        try{
            db = SQLiteDatabase.openOrCreateDatabase(file,null);
        }catch (Exception ignored){return false;}

        if (db==null || !db.isOpen())
            return false;

        @SuppressLint("Recycle")
        Cursor cursor=null;

        /// cursor for Notes
        cursor=db.query(TBL_NOTE_NAME,null,null,null,null,null,null);
        if(cursor==null)
            return false;

        while (cursor.moveToNext()){
            Note note = new Note();
            note.setTitle(cursor.getString(1));
            note.setText(cursor.getString(2));
            note.setCategory(0);

            if(cursor.getCount()<8){ // db is notebook database file
                note.setColor(Constants.categoryColorsList.get(0));
                note.setaTime(cursor.getString(4));
                note.setcTime(cursor.getString(4));
                note.setPin(0);
                note.setDeleted(0);
            }else{
                note.setColor(cursor.getString(4));
                note.setaTime(cursor.getString(5));
                note.setcTime(cursor.getString(6));
                note.setPin(cursor.getInt(7));
                note.setDeleted(cursor.getInt(8));
            }

            addNote(note);
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

    ///////////// Category Start //////////////////
    public List<Category> getCategories(){
        List<Category> list = new ArrayList<>();
        list.add(new Category(0,0,context.getResources().getString(R.string.no_category),"#ffffff"));

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
