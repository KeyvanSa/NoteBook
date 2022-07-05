package ir.ebookline.notebook.DataBase;

import android.content.*;
import android.database.sqlite.*;
import java.util.*;
import android.database.*;

public class DataBaseHelper
{
	private DataBaseCreater dbhelper;
	private SQLiteDatabase database;

	public DataBaseHelper(Context context)
	{
		dbhelper=new DataBaseCreater(context);
	}

	public void OPEN ()
	{
		database=dbhelper.getWritableDatabase();
	}

	public void CLOSE ()
	{
		dbhelper.close();
	}







	/////////////////////////////// Insert Note Method /////////////////////////////////
	public void INSERT_NOTE (String title,String text,String Category,String Date)
	{
		ContentValues cv=new ContentValues();
		cv.put(dbhelper.TITLE , title);
		cv.put(dbhelper.NOTE  , text);
		cv.put(dbhelper.CATEGORY  , Category);
		cv.put(dbhelper.DATE , Date);
		OPEN();
		database.insert(dbhelper.TBL_NOTE_NAME,dbhelper.TITLE,cv);
		CLOSE();
	}
	/////////////////////////////// Insert Update Method /////////////////////////////////
	public void UPDATE_NOTE (int id,String title,String text,String Category,String Date)
	{
		ContentValues cv=new ContentValues();
		cv.put(dbhelper.TITLE , title);
		cv.put(dbhelper.NOTE  , text);
		cv.put(dbhelper.CATEGORY  , Category);
		cv.put(dbhelper.DATE , Date);
		OPEN();
		database.update(dbhelper.TBL_NOTE_NAME,cv,dbhelper.ID+"="+id,null);
		CLOSE();
	}

	/////////////////////////////// Delete Note Method /////////////////////////////////
	public void DELETE_NOTE (int id)
	{
		OPEN();
		database.delete(dbhelper.TBL_NOTE_NAME,dbhelper.ID+"="+id, null );
		CLOSE();
	}












	/////////////////////////////// Insert Category Method /////////////////////////////////
	public void INSERT_CATEGORY (String name)
	{
		ContentValues cv=new ContentValues();
		cv.put(dbhelper.NAME , name);
		OPEN();
		database.insert(dbhelper.TBL_CATEGOTY_NAME,dbhelper.NAME,cv);
		CLOSE();
	}


	/////////////////////////////// Update Category Method /////////////////////////////////
	public void UPDATE_CATEGORY (int id,String name)
	{
		ContentValues cv=new ContentValues();
		cv.put(dbhelper.NAME,name);
		OPEN();
		database.update(dbhelper.TBL_CATEGOTY_NAME,cv,dbhelper.ID_CATEGOTY+"="+id,null);
		CLOSE();
	}

	/////////////////////////////// Delete Category Method /////////////////////////////////
	public void DELETE_CATERGORY (int id)
	{
		OPEN();
		database.delete(dbhelper.TBL_CATEGOTY_NAME,dbhelper.ID_CATEGOTY+"="+id,null);
		CLOSE();
	}







	/////////////////////////////// Set Pin Note Method /////////////////////////////////
	public void SET_PIN (int id,String Pin)
	{
		ContentValues cv=new ContentValues();
		cv.put(dbhelper.PIN,Pin);
		OPEN();
		database.update(dbhelper.TBL_NOTE_NAME,cv,dbhelper.ID+"="+id,null);
		CLOSE();
	}








	/////////////////////////////// Show Notes list Method /////////////////////////////////
	public ArrayList<String> SHOW_NOTES_LIST (int fild)
	{
		ArrayList<String>list=new ArrayList<String>();
		OPEN();
		Cursor cu1 = database.query(dbhelper.TBL_NOTE_NAME,null,"pin !=0",null,null,null,null);
		while(cu1.moveToNext()) {
			list.add(cu1.getString(fild));
		}
		Cursor cu2 = database.query(dbhelper.TBL_NOTE_NAME,null,"pin=0",null,null,null,null);
		while(cu2.moveToNext()) {
			list.add(cu2.getString(fild));
		}
		CLOSE();
		return list;
	}







	/////////////////////////////// Show Category Method /////////////////////////////////
	public ArrayList<String> SHOW_CATEGORIES_LIST (int fild)
	{
		ArrayList<String>list=new ArrayList<String>();
		OPEN();
		Cursor cu= database.query(dbhelper.TBL_CATEGOTY_NAME, null, null, null, null,null,null);
		if(fild==0)
			list.add("0");
		if(fild==1)
			list.add("No category");
		while(cu.moveToNext()) {
			list.add(cu.getString(fild));
		}
		CLOSE();
		return list;
	}






	/////////////////////////////// Search Note Method /////////////////////////////////
	public ArrayList<String> SEARCH_LIST (int fild,String txt)
	{
		ArrayList<String>list=new ArrayList<String>();
		OPEN();
		Cursor cu = database.query(dbhelper.TBL_NOTE_NAME,null,dbhelper.TITLE+" LIKE '%"+txt+"%'",null,null,null,null);
		while(cu.moveToNext()) {
			list.add(cu.getString(fild));
		}
		CLOSE();
		return list;
	}








	/////////////////////////////// Update Notes Category Method /////////////////////////////////
	public void UPDATE_CATEGORY_NOTES (int CatId)
	{
		ArrayList<Integer>Ids=new ArrayList<Integer>();
		OPEN();
		Cursor cu = database.query(dbhelper.TBL_NOTE_NAME,null,dbhelper.CATEGORY+"="+CatId,null,null,null,null);
		while(cu.moveToNext()){
			Ids.add(Integer.parseInt(cu.getString(0)));
		}
		for(int i=0;i<Ids.size();i++) {
			ContentValues cv=new ContentValues();
			cv.put(dbhelper.CATEGORY,"0");
			database.update(dbhelper.TBL_NOTE_NAME,cv,dbhelper.ID+"="+Ids.get(i),null);
		}
		CLOSE();
	}


	/////////////////////////////// Number of notes in each category /////////////////////////////////
	public Integer COUNT_CAT_NOTES (int CatId)
	{
		OPEN();
		Cursor cu = database.query(dbhelper.TBL_NOTE_NAME,null,dbhelper.CATEGORY+"="+CatId,null,null,null,null);
		int i=0;
		while(cu.moveToNext()){
			i++;
		}
		CLOSE();
		return i;
	}

	public String ShowInformationNote (String id,int field)
	{
		OPEN();
		Cursor cu= database.rawQuery("select * from "+dbhelper.TBL_NOTE_NAME+" where "+dbhelper.ID+"="+id, null );
		if(cu!=null&&cu.getCount()!=0) {
			cu.moveToFirst();
			String name=cu.getString(field);
			CLOSE();
			return name;
		}else return "Error";
	}

	public String ShowInformationCategory (String id,int field)
	{
		OPEN();
		Cursor cu= database.rawQuery("select * from "+dbhelper.TBL_CATEGOTY_NAME+" where "+dbhelper.ID_CATEGOTY+"="+id, null );
		if(cu!=null&&cu.getCount()!=0) {
			cu.moveToFirst();
			String name=cu.getString(field);
			CLOSE();
			return name;
		}else return "Error";
	}

}
