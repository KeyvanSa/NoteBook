package ebookline.notepad.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import ebookline.notepad.R;

public class CustomDialog extends Dialog implements View.OnClickListener
{
    private static final int TYPE_INFORMATION =1;
    private static final int TYPE_SUCCESS     =2;
    private static final int TYPE_ERROR       =3;

    Context context;
    private int type = TYPE_INFORMATION;
    private String title;
    private String text;
    private String buttonOkText;
    private String buttonNoText;

    private ItemClickListener mClickListener;

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_dialog);

        TextView textViewTitle = this.findViewById(R.id.textViewTitle);
        TextView textViewText = this.findViewById(R.id.textViewText);
        Button buttonOk = this.findViewById(R.id.buttonOK);
        Button buttonNo = this.findViewById(R.id.buttonNo);

        CardView cardView=this.findViewById(R.id.cardView);

        if(getType()==TYPE_SUCCESS){
            cardView.setCardBackgroundColor(Color.parseColor("#3B7CB342"));
            textViewTitle.setTextColor(Color.parseColor("#FF43A047"));
            textViewText.setTextColor(Color.parseColor("#FF7CB342"));
        }else if(getType()==TYPE_ERROR){
            cardView.setCardBackgroundColor(Color.parseColor("#28E53935"));
            textViewTitle.setTextColor(Color.parseColor("#FFE53935"));
            textViewText.setTextColor(Color.parseColor("#FFF8827F"));
        }

        textViewTitle.setText( (getTitle()==null) ? context.getResources().getString(R.string.title) : getTitle());
        textViewText.setText( (getText()==null) ? context.getResources().getString(R.string.text) : getText());

        buttonOk.setText((getButtonOkText()==null) ? context.getResources().getString(R.string.ok) : getButtonOkText());
        buttonNo.setText((getButtonNoText()==null) ? context.getResources().getString(R.string.no) : getButtonNoText());

        buttonOk.setOnClickListener(this);
        buttonNo.setOnClickListener(this);

        if(mClickListener==null){
            buttonOk.setVisibility(View.GONE);
            buttonNo.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View view) {
        if(mClickListener==null) return;

        Button button= (Button) view;
        if(button.getText().toString().equals( (getButtonOkText()==null) ? context.getResources().getString(R.string.ok) : getButtonOkText() ))
            mClickListener.onPositiveItemClick(view);
        else mClickListener.onNegativeItemClick(view);
        dismiss();
    }

    public void showDialog(){
        init();
        if (!isShowing())
            show();
    }

    public CustomDialog(Context context) {
        super(context);
        this.context=context;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onPositiveItemClick(View view);
        void onNegativeItemClick(View view);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getButtonOkText() {
        return buttonOkText;
    }

    public void setButtonOkText(String buttonOkText) {
        this.buttonOkText = buttonOkText;
    }

    public String getButtonNoText() {
        return buttonNoText;
    }

    public void setButtonNoText(String buttonNoText) {
        this.buttonNoText = buttonNoText;
    }

}
