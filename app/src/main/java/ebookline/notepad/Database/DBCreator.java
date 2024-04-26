package ebookline.notepad.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import ebookline.notepad.Util.Constants;

public class DBCreator extends SQLiteOpenHelper
{
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS "
                +Constants.TBL_NOTE_NAME+"("
                +Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +Constants.TITLE+" TEXT ,"
                +Constants.TEXT+" TEXT ,"
                +Constants.CATEGORY+" INTEGER DEFAULT 0,"
                +Constants.COLOR+" TEXT ,"
                +Constants.ATIME+" TEXT ,"
                +Constants.ETIME+" TEXT ,"
                +Constants.PIN+" INTEGER DEFAULT 0,"
                +Constants.DELETED+ " INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                +Constants.TBL_CATEGORY_NAME+"("
                +Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +Constants.TITLE+" TEXT ,"
                +Constants.COLOR+" TEXT ,"
                +Constants.PARENT+" INTEGER DEFAULT 0);");

        db.execSQL("CREATE TABLE IF NOT EXISTS "
                +Constants.TBL_TASK_NAME+"("
                +Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                +Constants.TITLE+" TEXT ,"
                +Constants.COLOR+" TEXT ,"
                +Constants.IS_CHECKED+" INTEGER DEFAULT 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion<newVersion){
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +Constants.TBL_TASK_NAME+"("
                    +Constants.ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                    +Constants.TITLE+" TEXT ,"
                    +Constants.COLOR+" TEXT ,"
                    +Constants.IS_CHECKED+" INTEGER DEFAULT 0);");
        }
    }

    public DBCreator(@Nullable Context context) {
        super(context, Constants.DB_NAME,null,Constants.DB_VERSION);
    }
}
