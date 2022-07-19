package ir.ebookline.notebook.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ebookline.notebooksrc.R;
import ir.ebookline.notebook.Adapter.AdapterNotes;
import ir.ebookline.notebook.DataBase.DataBaseHelper;
import ir.ebookline.notebook.JalaliCalendar;
import ir.ebookline.notebook.Model.ModelNotes;
import ir.ebookline.notebook.PublicFunction;

public class MainActivity extends Activity
{
	DataBaseHelper db;
	PublicFunction pFun;

	RecyclerView RecyclerViewNotes;
	LinearLayout LinearLayout_Empty ;
	FloatingActionMenu materialDesignFAM;

	ArrayList<String> Ids;
	ArrayList<String>Titles;
	ArrayList<String>Texts;
	ArrayList<String>Categories;
	ArrayList<String>Dates;
	ArrayList<String>Pins;

	ArrayList<String>IdsCat;
	ArrayList<String>TitlesCat;

	boolean isSearchList=false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		init();

	}



	public void addNote(View v)
	{
		materialDesignFAM.close(true);


		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View DialogView = inflater.inflate(R.layout.add_update,null);

		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(DialogView);
		final EditText etTitle=(EditText) DialogView.findViewById(R.id.etTitle);
		final EditText etText =(EditText) DialogView.findViewById(R.id.etText);
		final Spinner  spCats =(Spinner) DialogView.findViewById(R.id.Cats);
		Button         b      =(Button) DialogView.findViewById(R.id.add_update_button);

		IdsCat     =db.SHOW_CATEGORIES_LIST(0);
		TitlesCat  =db.SHOW_CATEGORIES_LIST(1);

		spCats.setAdapter(new AdapterSpinner());

		b.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View p1)
			{
				String strTitle=etTitle.getText().toString();
				String strText=etText.getText().toString();

				if(strTitle.equals("")){
					pFun.showTOAST("Enter Title",2);
					return;
				}

				if(strText.equals("")){
					pFun.showTOAST("Enter Text",2);
					return;
				}

				db.INSERT_NOTE (strTitle,strText,""+IdsCat.get(spCats.getSelectedItemPosition()),""+ System.currentTimeMillis());
				dialog.dismiss();
				init();
				pFun.showTOAST(getResources().getString(R.string.save_note),3);
			}
		});
		dialog.show();
	}

	public void addCat(View v)
	{
		materialDesignFAM.close(true);


		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View DialogView = inflater.inflate(R.layout.add_update_cat,null);

		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(DialogView);
		final EditText etTitle=(EditText) DialogView.findViewById(R.id.etTitle_cat);
		Button         b      =(Button) DialogView.findViewById(R.id.add_update_button_cat);
		b.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View p1)
			{
				String strTitle=etTitle.getText().toString();

				if(strTitle.equals("")){
					pFun.showTOAST("Enter Title",2);
					return;
				}

				db.INSERT_CATEGORY(strTitle);
				dialog.dismiss();
				pFun.showTOAST(getResources().getString(R.string.save_category),3);
			}
		});
		dialog.show();
	}

	public void showCats(View v)
	{
		materialDesignFAM.close(true);

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View DialogView = inflater.inflate(R.layout.show_cats,null);

		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(DialogView);
		ListView listCat=(ListView) DialogView.findViewById(R.id.ListView);

		IdsCat     =db.SHOW_CATEGORIES_LIST(0);
		TitlesCat  =db.SHOW_CATEGORIES_LIST(1);


		for(int i=0;i<IdsCat.size();i++) {
			TitlesCat.set(i,""+TitlesCat.get(i)+"--->"+db.COUNT_CAT_NOTES(Integer.parseInt(IdsCat.get(i))));
		}

		listCat.setAdapter(new AdapterSpinner());

		listCat.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> p1, View p2, final int position, long p4)
			{
				if(position!=0)
				{
					LayoutInflater inflater2= (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View DialogView2 = inflater2.inflate(R.layout.add_update_cat,null);

					final Dialog dialog2 = new Dialog(MainActivity.this);
					dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
					dialog.setContentView(DialogView2);
					final EditText etTitle=(EditText) DialogView2.findViewById(R.id.etTitle_cat);
					Button         b      =(Button) DialogView2.findViewById(R.id.add_update_button_cat);
					b.setText("Update");

					String s=TitlesCat.get(position).substring(0,TitlesCat.get(position).indexOf("-"));

					etTitle.setText(s);
					b.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View p1)
						{
							String strTitle=etTitle.getText().toString();

							if(strTitle.equals("")){
								pFun.showTOAST("Enter Title",2);
								return;
							}

							db.UPDATE_CATEGORY(Integer.parseInt(IdsCat.get(position)),strTitle);

							dialog2.dismiss();
							dialog.dismiss();

							init();

							pFun.showTOAST(getResources().getString(R.string.update_category),3);
						}
					});

					dialog.show();

				}
			}
		});
		listCat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> p1, View p2, int p3, long p4)
			{
				if(p3!=0)
				{

					db.UPDATE_CATEGORY_NOTES(Integer.parseInt(IdsCat.get(p3)));
					db.DELETE_CATERGORY(Integer.parseInt(IdsCat.get(p3)));

					init();
					dialog.dismiss();
					pFun.showTOAST(getResources().getString(R.string.delete_category),1);
				}
				return true;
			}
		});
		dialog.show();
	}


	public void searchDialog(View v)
	{
		materialDesignFAM.close(true);

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View DialogView = inflater.inflate(R.layout.search_dialog,null);

		final Dialog dialog = new Dialog(MainActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.setContentView(DialogView);
		final EditText etTitle=(EditText) DialogView.findViewById(R.id.searchdialogEditText);
		Button         b      =(Button) DialogView.findViewById(R.id.searchdialogButton);

		b.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View p1)
			{
				String s=etTitle.getText().toString().trim();
				if(s.length()>=3) {
					Ids         =null;
					Titles      =null;
					Texts       =null;
					Categories  =null;
					Dates       =null;

					Ids         =db.SEARCH_LIST(0,s);
					Titles      =db.SEARCH_LIST(1,s);
					Texts       =db.SEARCH_LIST(2,s);
					Categories  =db.SEARCH_LIST(3,s);
					Dates       =db.SEARCH_LIST(4,s);

					setList();

					isSearchList=true;
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}




	public class AdapterSpinner extends BaseAdapter {
		Context context            = MainActivity.this ;
		LayoutInflater inflater    =null;
		public AdapterSpinner() {
			inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {return TitlesCat.size();}
		@Override
		public Object getItem(int position) {return position;}
		@Override
		public long getItemId(int position) {return position;}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)  {
			View rowView = inflater.inflate(R.layout.item_category,null);
			TextView tvTitle=(TextView) rowView.findViewById(R.id.TitleCategory);
			tvTitle.setText(TitlesCat.get(position));
			return rowView;
		}
	}


	@Override
	public void onBackPressed()
	{
		if(!isSearchList) {
			super.onBackPressed();
		}else{
			init();
			isSearchList=false;
		}
	}


	public void setSpan (EditText et) {
		SpannableString ssSpannable = new SpannableString(et.getText().toString().trim());
		Pattern PATTERN= Pattern.compile("#(\\w+|\\W+)");
		Matcher mat = PATTERN.matcher(ssSpannable);
		while (mat.find()) {
			final String tag = mat.group(0);
			ssSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4500")) , mat.start() , mat.end() , 0);
			ssSpannable.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					pFun.showTOAST(tag,1);
				}
				@Override
				public void updateDrawState(TextPaint ds) {
					ds.setUnderlineText(false);
				}
			}, mat.start(),mat.end() ,0);
		}
		et.setMovementMethod(LinkMovementMethod.getInstance());
		et.setText(ssSpannable);

	}


	public void setList()
	{
		Ids         =db.SHOW_NOTES_LIST(0);
		Titles      =db.SHOW_NOTES_LIST(1);
		Texts       =db.SHOW_NOTES_LIST(2);
		Categories  =db.SHOW_NOTES_LIST(3);
		Dates       =db.SHOW_NOTES_LIST(4);
		Pins        =db.SHOW_NOTES_LIST(5);

		ArrayList<ModelNotes> array = new ArrayList<ModelNotes>();
		for ( int i=0 ; i < Ids.size() ; i++)
		{
			ModelNotes mn = new ModelNotes();
			mn.setId(Ids.get(i));
			mn.setTitle(Titles.get(i));
			mn.setNote(Texts.get(i));
			mn.setCategory(Categories.get(i));
			mn.setDate(Dates.get(i));
			mn.setPin(Pins.get(i));
			array.add(mn);
		}

		AdapterNotes an = new AdapterNotes(MainActivity.this , array);

		if ( an != null)
		{
			if ( an.getItemCount() == 0 )
			{
				LinearLayout_Empty.setVisibility(View.VISIBLE);
			}else {
				LinearLayout_Empty.setVisibility(View.GONE);
				RecyclerViewNotes.setAdapter(an);
			}
		}
	}


	private void init() {

		db=new DataBaseHelper(this);
		pFun = new PublicFunction(this);

		LinearLayout_Empty = findViewById(R.id.LinearLayout_Empty);
		RecyclerViewNotes = findViewById(R.id.RecyclerViewNotes);

		RecyclerView.LayoutManager mLayout=new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
		RecyclerViewNotes.setLayoutManager(mLayout);

		materialDesignFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
		materialDesignFAM.setClosedOnTouchOutside(false);
		materialDesignFAM.setAnimationDelayPerItem(90);

		setList();
	}

}
