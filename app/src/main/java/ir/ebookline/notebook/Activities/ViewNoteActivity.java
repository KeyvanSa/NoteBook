package ir.ebookline.notebook.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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

    ImageView imageviewPin , imageviewEdit , imageviewShare ;
    TextView textviewTitle , textviewDate , textviewCategory , textviewText ;

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
        imageviewPin = findViewById(R.id.ImageViewPin);
        imageviewEdit = findViewById(R.id.ImageViewEdit);
        imageviewShare = findViewById(R.id.ImageViewShare);
        textviewTitle = findViewById(R.id.TextViewTitle);
        textviewCategory = findViewById(R.id.TextViewCategory);
        textviewDate = findViewById(R.id.TextViewDate);
        textviewText = findViewById(R.id.TextViewText);

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

        if ( strPin.equals("1")){
            imageviewPin.setImageResource(R.drawable.ic_baseline_pin_24);
        }

        textviewTitle.setText(strTitle);
        textviewCategory.setText(strCategory);
        textviewDate.setText(strDate);
        textviewText.setText(strText);

        imageviewPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( strPin.equals("0")){
                    strPin ="1";
                    pFun.showTOAST("Pinned...!",3);
                    imageviewPin.setImageResource(R.drawable.ic_baseline_pin_24);
                } else {
                    strPin = "0";
                    pFun.showTOAST("Unpinned...!",3);
                    imageviewPin.setImageResource(R.drawable.ic_outline_unpin_24);
                }

                db.SET_PIN(Integer.parseInt(strId),strPin);

            }
        });

        imageviewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pFun.showTOAST("Soon ...",1);
            }
        });

        imageviewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pFun.showTOAST("Soon ...",1);
            }
        });

    }
}