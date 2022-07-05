package ir.ebookline.notebook.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataBaseCreater extends SQLiteOpenHelper
{
    public static final String DB_NOTE_NAME    = "notebook";
    public static final String TBL_NOTE_NAME   = "tbl_note";
	
    public static final String ID        = "id";
    public static final String TITLE     = "title";
    public static final String NOTE      = "note";
	public static final String CATEGORY  = "category";
	public static final String DATE      = "date";
	public static final String PIN       = "pin";
	public static final String Position  = "position";
	public static final String Type      = "type";
	public static final String Picture   = "picture";
	public static final String Sound     = "sound";
	public static final String extCol1   = "extCol1";
	public static final String extCol2   = "extCol2";
	public static final String extCol3   = "extCol3";


	public static final String TBL_CATEGOTY_NAME     = "tbl_category";
    public static final String ID_CATEGOTY           = "id";
    public static final String NAME                  = "name";
	
    public DataBaseCreater(Context context) {
        super(context, DB_NOTE_NAME , null , 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
	{
        db.execSQL(            "CREATE TABLE IF NOT EXISTS "
				   + TBL_NOTE_NAME
				   +	           "("
				   + ID          + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				   + TITLE       + " TEXT,"
				   + NOTE        + " TEXT,"
				   + CATEGORY    + " TEXT,"
				   + DATE        + " TEXT,"
				   + PIN         + " TEXT DEFAULT 0,"
				   + Position    + " TEXT DEFAULT 1,"
				   + Type        + " TEXT DEFAULT 1,"
				   + Picture     + " TEXT,"
				   + Sound       + " TEXT,"
				   + extCol1     + " TEXT,"
				   + extCol2     + " TEXT,"
				   + extCol3     + " TEXT);"  );

		db.execSQL(                             "CREATE TABLE IF NOT EXISTS "
				   +  TBL_CATEGOTY_NAME     +   "("
				   +  ID_CATEGOTY           +   " INTEGER PRIMARY KEY AUTOINCREMENT,"
				   +  NAME                  +   " TEXT);"  );
		
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {}
}
