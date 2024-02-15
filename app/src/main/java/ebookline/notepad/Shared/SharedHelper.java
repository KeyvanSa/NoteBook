package ebookline.notepad.Shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import ebookline.notepad.Util.EncryptingData;

public class SharedHelper
{
	Context context;

	public boolean removeValue(String key){
		if(getSharedPreferences().contains(key))
			return getSharedPreferencesEditor().remove(key).commit();
		else return false;
	}

	public boolean getBoolean(String key){
		return getSharedPreferences().getBoolean(key,false);
	}

	public boolean saveBoolean(String key,boolean value){
		return getSharedPreferencesEditor().putBoolean(key,value).commit();
	}

	public String getString(String key){
		return getSharedPreferences().getString(key,null);
	}

	public String getString(String key,String def){
		return getSharedPreferences().getString(key,def);
	}
	
	public boolean saveString(String key,String value){
		return getSharedPreferencesEditor().putString(key,value).commit();
	}
	
	public int getInt(String key){
		return getSharedPreferences().getInt(key,0);
	}
	
	public int getInt(String key,int def){
		return getSharedPreferences().getInt(key,def);
	}

	public boolean saveInt(String key,int value){
		return getSharedPreferencesEditor().putInt(key,value).commit();
	}

	private SharedPreferences.Editor getSharedPreferencesEditor(){
		return getSharedPreferences().edit();
	}

	private SharedPreferences getSharedPreferences(){
		return context.getSharedPreferences("shp",Context.MODE_PRIVATE);
	}
	
	public SharedHelper(Context context){
		this.context=context;
	}
}
