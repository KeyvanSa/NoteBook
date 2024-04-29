package ebookline.notepad.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ebookline.notepad.Adapter.ColorAdapter;
import ebookline.notepad.R;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class ColorPickerDialog extends Dialog implements ColorAdapter.ItemClickListener , View.OnClickListener
{
    Context context;
    HelperClass helper;

    RecyclerView recyclerView;
    Button buttonPositive , buttonNegative;
    TextView textViewTitle;
    ColorAdapter adapter;

    ArrayList<String> colorsList;
    String colorCode;

    OnClickButtonListener onClickButtonListener;

    private void init()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_colorpicker_dialog);

        buttonPositive = this.findViewById(R.id.buttonPositive);
        buttonNegative = this.findViewById(R.id.buttonNegative);
        textViewTitle = this.findViewById(R.id.textViewTitle);
        buttonPositive.setOnClickListener(this);
        buttonNegative.setOnClickListener(this);
        recyclerView = this.findViewById(R.id.recycler);
        
        GridLayoutManager layoutManager=new GridLayoutManager(context,3);

        if(getColorsList()==null)
            setColorsList(Constants.categoryColorsList);

        adapter = new ColorAdapter(context, getColorsList() ,0);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onItemClick(View view, int position) {
        colorCode=getColorsList().get(position);
        adapter = new ColorAdapter(context,getColorsList(),position);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view)
    {
        Button button = (Button) view;

        if(button.getId()==R.id.buttonPositive)
            this.onClickButtonListener.chooseColor( (colorCode==null) ? (
                    (getColorsList()==null) ? "#000000" : getColorsList().get(0)
            ) : colorCode);
        else this.onClickButtonListener.chooseBack();

        dismiss();
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener){
        this.onClickButtonListener = onClickButtonListener;
    }

    public void showDialog(){
        init();
        show();
    }

    public ColorPickerDialog(Context context){
        super(context);
        this.context=context;
        helper = new HelperClass(context);
    }

    public interface OnClickButtonListener {
        void chooseColor(String colorCode);
        void chooseBack();
    }

    public ArrayList<String> getColorsList() {
        return colorsList;
    }

    public void setColorsList(ArrayList<String> colorsList) {
        this.colorsList = colorsList;
    }

}