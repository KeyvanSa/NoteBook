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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ebookline.notebooksrc.R;
import ir.ebookline.notebook.DataBase.DataBaseHelper;
import ir.ebookline.notebook.JalaliCalendar;
import ir.ebookline.notebook.PublicFunction;

public class MainActivity extends Activity
{
	DataBaseHelper db;
	PublicFunction pFun;

	ListView lv;
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


		lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> p1, View p2,int position, long p4)
			{
				Intent toViewNote = new Intent(MainActivity.this , ViewNoteActivity.class);
				toViewNote.putExtra("id",Ids.get(position));
				startActivity(toViewNote);

				/*
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View DialogView = inflater.inflate(R.layout.add_update,null);

				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.setContentView(DialogView);
				final EditText etTitle=(EditText) DialogView.findViewById(R.id.etTitle);
				final EditText etText =(EditText) DialogView.findViewById(R.id.etText);
				final Spinner spCats =(Spinner) DialogView.findViewById(R.id.Cats);
				Button b      =(Button) DialogView.findViewById(R.id.add_update_button);

				b.setText("Update Note");
				etTitle.setText(Titles.get(p3));
				etText.setText(Texts.get(p3));
				setSpan(etText);

				IdsCat     =db.SHOW_CATEGORIES_LIST(0);
				TitlesCat  =db.SHOW_CATEGORIES_LIST(1);

				spCats.setAdapter(new AdapterSpinner());

				for(int i=0;i<IdsCat.size();i++) {
					if(Integer.parseInt(IdsCat.get(i))==Integer.parseInt(Categories.get(p3))) {
						spCats.setSelection(i);
					}
				}

				b.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View p1)
					{
						String strTitle=etTitle.getText().toString();
						String strText=etText.getText().toString();

						if(strTitle.equals("")){
							pFun.SHOW_TOAST("Enter Title",2);
							return;
						}

						if(strText.equals("")){
							pFun.SHOW_TOAST("Enter Text",2);
							return;
						}

						db.UPDATE_NOTE(Integer.parseInt(Ids.get(p3)),strTitle,strText,""+IdsCat.get(spCats.getSelectedItemPosition()),""+ System.currentTimeMillis());
						dialog.dismiss();
						setList();
						pFun.SHOW_TOAST(getResources().getString(R.string.update_note),1);
					}
				});
				dialog.show();*/
			}
		});


		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> p1, View p2, final int p3, long p4)
			{
				LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View DialogView = inflater.inflate(R.layout.dialog,null);

				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.setContentView(DialogView);

				TextView tvTitle = DialogView.findViewById(R.id.Title);
				TextView tvText = DialogView.findViewById(R.id.Text);
				Button bDelete =(Button) DialogView.findViewById(R.id.Button_Delete_Note);
				Button bPin =(Button) DialogView.findViewById(R.id.Button_Pin_Note);

				tvText.append("\n"+Titles.get(p3));

				if(Pins.get(p3).equals("1")) {
					bPin.setText("Remove the PIN?");
				}

				bDelete.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						db.DELETE_NOTE(Integer.parseInt(Ids.get(p3)));
						pFun.showTOAST(getResources().getString(R.string.delete_note),1);
						setList();
						dialog.dismiss();
					}
				});

				bPin.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{

						if(Pins.get(p3).equals("0")) {
							db.SET_PIN(Integer.parseInt(Ids.get(p3)),"1");
						}else {
							db.SET_PIN(Integer.parseInt(Ids.get(p3)),"0");
						}

						setList();
						dialog.dismiss();
					}
				});

				dialog.show();

				return true;
			}
		});
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
				setList();
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

							setList();

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

					setList();
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

					lv.setAdapter(new AdapterListView());
					isSearchList=true;
					dialog.dismiss();
				}
			}
		});
		dialog.show();
	}





	public class AdapterListView extends BaseAdapter {
		Context context            = MainActivity.this ;
		LayoutInflater inflater    =null;
		public AdapterListView() {
			inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {return Titles.size();}
		@Override
		public Object getItem(int position) {return position;}
		@Override
		public long getItemId(int position) {return position;}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)  {

			View rowView = inflater.inflate(R.layout.item_note,null);
			TextView tvTitle=(TextView) rowView.findViewById(R.id.Title);
			TextView tvText=(TextView) rowView.findViewById(R.id.Text);
			TextView tvOther=(TextView) rowView.findViewById(R.id.Other);
			ImageView ivPin=(ImageView) rowView.findViewById(R.id.Pin);

			tvTitle.setText(Titles.get(position));
			tvText.setText(Texts.get(position));


			IdsCat     =db.SHOW_CATEGORIES_LIST(0);
			TitlesCat  =db.SHOW_CATEGORIES_LIST(1);

			for(int i=0;i<IdsCat.size();i++) {
				if(Integer.parseInt(IdsCat.get(i))==Integer.parseInt(Categories.get(position))) {
					tvOther.setText("("+TitlesCat.get(i)+"),");
					break;
				}
			}

			tvOther.append(""+new JalaliCalendar().getJalaliDate(new Date(Long.parseLong(Dates.get(position))))+"-"+new SimpleDateFormat("HH:mm:ss").format(new Date(Long.parseLong(Dates.get(position)))));

			Animation anim;
			if(position%2==0){
				anim = AnimationUtils.loadAnimation( context , R.anim.right_to_left);
			}else{
				anim = AnimationUtils.loadAnimation( context , R.anim.left_to_right);
			}
			rowView.setAnimation(anim);

			if(Pins.get(position).equals("1")) {
				ivPin.setVisibility(View.VISIBLE);
			}

			return rowView;
		}
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
			setList();
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
		Ids         =null;
		Titles      =null;
		Texts       =null;
		Categories  =null;
		Dates       =null;
		Pins        =null;

		Ids         =db.SHOW_NOTES_LIST(0);
		Titles      =db.SHOW_NOTES_LIST(1);
		Texts       =db.SHOW_NOTES_LIST(2);
		Categories  =db.SHOW_NOTES_LIST(3);
		Dates       =db.SHOW_NOTES_LIST(4);
		Pins        =db.SHOW_NOTES_LIST(5);

		lv.setAdapter(new AdapterListView());

		if ( lv.getAdapter() != null)
		{
			if (lv.getAdapter().getCount() == 0 )
			{
				LinearLayout_Empty.setVisibility(View.VISIBLE);
			}else {
				LinearLayout_Empty.setVisibility(View.GONE);
			}
		}else {
			LinearLayout_Empty.setVisibility(View.VISIBLE);
		}
	}


	private void init() {

		db=new DataBaseHelper(this);
		pFun = new PublicFunction(this);

		LinearLayout_Empty = findViewById(R.id.LinearLayout_Empty);
		lv = (ListView) findViewById(R.id.mainListView1);

		materialDesignFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
		materialDesignFAM.setClosedOnTouchOutside(false);
		materialDesignFAM.setAnimationDelayPerItem(90);

		setList();
	}


}
