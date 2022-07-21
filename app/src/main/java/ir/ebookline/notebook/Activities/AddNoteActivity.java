package ir.ebookline.notebook.Activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import ebookline.notebooksrc.R;
import ir.ebookline.notebook.DataBase.DataBaseHelper;
import ir.ebookline.notebook.PublicFunction;

public class AddNoteActivity extends AppCompatActivity {

    EditText etTitle;
    EditText etText ;
    Spinner spCats  ;
    Button bSave    ;

    ArrayList<String> IdsCat;
    ArrayList<String>TitlesCat;

    DataBaseHelper db;
    PublicFunction pFun;

    Bundle b;

    String strTitle ="" , strText ="" , strId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        init();

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                db.UPDATE_NOTE (Integer.parseInt(strId),strTitle,strText,""+IdsCat.get(spCats.getSelectedItemPosition()),""+ System.currentTimeMillis());
                pFun.showTOAST(getResources().getString(R.string.save_note),3);
                finish();
            }
        });

    }

    private void init() {
        db=new DataBaseHelper(this);
        pFun = new PublicFunction(this);

        b = getIntent().getExtras();
        if(b != null){
            strId = b.getString("id","");
            strTitle = db.ShowInformationNote(strId,1);
            strText = db.ShowInformationNote(strId,2);
        }

        etTitle=(EditText)findViewById(R.id.etTitle);
        etText =(EditText)findViewById(R.id.etText);
        spCats =(Spinner)findViewById(R.id.Cats);
        bSave =(Button)findViewById(R.id.add_update_button);


        if(!strTitle.equals("") && !strText.equals("")){
            etTitle.setText(strTitle);
            etText.setText(strText);
            bSave.setText("Edit Note");
        }

        IdsCat     =db.SHOW_CATEGORIES_LIST(0);
        TitlesCat  =db.SHOW_CATEGORIES_LIST(1);

        spCats.setAdapter(new AddNoteActivity.AdapterSpinner());
    }

    public class AdapterSpinner extends BaseAdapter {
        Context context            = AddNoteActivity.this ;
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

}