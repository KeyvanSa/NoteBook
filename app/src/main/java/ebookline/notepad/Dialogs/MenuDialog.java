package ebookline.notepad.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Adapter.MenuAdapter;
import ebookline.notepad.Model.Menu;
import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class MenuDialog extends BottomSheetDialogFragment implements MenuAdapter.ItemClickListener
{
    Context context;
    HelperClass helper;

    private List<Menu> list;

    OnClickItemListener onClickButtonListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_menu,container,false);

        init(view);

        return view.getRootView();
    }

    private void init(View view)
    {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerMenu);

        MenuAdapter adapter = new MenuAdapter(context, ((getList() == null) ? new ArrayList<>() : getList()));
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    public void setOnClickButtonListener(OnClickItemListener onClickButtonListener){
        this.onClickButtonListener = onClickButtonListener;
    }

    @Override
    public void onMenuItemClick(View view, int position) {
        this.onClickButtonListener.onItemClick(getList().get(position));
        dismiss();
    }

    public MenuDialog(Context context){
        this.context=context;
        helper = new HelperClass(context);
    }

    public interface OnClickItemListener {
        void onItemClick(Menu menu);
    }

    public List<Menu> getList() {
        return list;
    }

    public void setList(List<Menu> list) {
        this.list = list;
    }

}
