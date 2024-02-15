package ebookline.notepad.Dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Adapter.MenuAdapter;
import ebookline.notepad.Model.Menu;
import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class MenuDialog extends BottomSheetDialog implements MenuAdapter.ItemClickListener
{
    Context context;
    HelperClass helper;

    private List<Menu> list;

    OnClickButtonListener onClickButtonListener;

    private void init()
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(null);

        View view=View.inflate(context, R.layout.layout_bottom_sheet_menu,null);
        view.setBackgroundColor(Color.TRANSPARENT);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerMenu);

        MenuAdapter adapter = new MenuAdapter(context, ((getList() == null) ? new ArrayList<>() : getList()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        setContentView(view);
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener){
        this.onClickButtonListener = onClickButtonListener;
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        this.onClickButtonListener.onItemClick(getList().get(position));
        dismiss();
    }

    public void showDialog(){
        init();
        show();
    }

    public MenuDialog(Context context){
        super(context);
        this.context=context;
        helper = new HelperClass(context);
    }

    public interface OnClickButtonListener {
        void onItemClick(Menu menu);
    }

    public List<Menu> getList() {
        return list;
    }

    public void setList(List<Menu> list) {
        this.list = list;
    }

}
