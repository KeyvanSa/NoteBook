package ir.ebookline.notebook.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ebookline.notebooksrc.R;
import ir.ebookline.notebook.DataBase.DataBaseHelper;
import ir.ebookline.notebook.JalaliCalendar;
import ir.ebookline.notebook.PublicFunction;

public class ViewNoteActivity extends Activity
{
    DataBaseHelper db;
    PublicFunction pFun;

    ImageView ImageViewPin , ImageViewEdit ;
    TextView TextViewTitle , TextViewDate , TextViewCategory , TextViewText ;

    String strId;
    String strTitle;
    String strText;
    String strCategory;
    String strDate;
    String strPin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        init();




    }





    private void init()
    {
        ImageViewPin = findViewById(R.id.ImageViewPin);
        ImageViewEdit = findViewById(R.id.ImageViewEdit);
        TextViewTitle = findViewById(R.id.TextViewTitle);
        TextViewCategory = findViewById(R.id.TextViewCategory);
        TextViewDate = findViewById(R.id.TextViewDate);
        TextViewText = findViewById(R.id.TextViewText);

        db=new DataBaseHelper(this);
        pFun = new PublicFunction(this);

        Bundle b = getIntent().getExtras();

        strId       = db.ShowInformationNote(b.getString("id"),0);

        if (strId.equals("Error"))
            return;

        strTitle    = db.ShowInformationNote(strId,1);
        strText     = db.ShowInformationNote(strId,2);
        strCategory = db.ShowInformationNote(strId,3);
        strDate     = db.ShowInformationNote(strId,4);
        strPin      = db.ShowInformationNote(strId,5);

        if (!strCategory.equals("0"))
            strCategory = db.ShowInformationCategory(strCategory,1);
        else
            strCategory = "No Category";

        strDate = new JalaliCalendar().getJalaliDate(new Date(Long.parseLong(strDate)))+
                "-"+new SimpleDateFormat("HH:mm:ss").format(new Date(Long.parseLong(strDate)));

        TextViewTitle.setText(strTitle);
        TextViewCategory.setText(strCategory);
        TextViewDate.setText(strDate);
        TextViewText.setText(strText);





    }
}