package ebookline.notepad.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ebookline.notepad.Model.Task;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>
{
    private final List<Task> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemCheckListener mCheckListener;

    private final Context context;
    private HelperClass helper;
    private SharedHelper shared;

    public TaskAdapter(Context context, List<Task> data) {
        this.context = context;
        this.helper = new HelperClass(context);
        this.shared = new SharedHelper(context);
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Task task = mData.get(position);

        try{

            if(task.getCheck()==1){
                holder.checkBoxTitle.setText(Html.fromHtml(String.format("<del>%s</del>",task.getTitle())));
                holder.checkBoxTitle.setChecked(true);
            } else {
                holder.checkBoxTitle.setText(task.getTitle());
                holder.checkBoxTitle.setChecked(false);
            }

            holder.itemCardView.setCardBackgroundColor(Color.parseColor(helper.getMaterialColorCode(task.getColor(),0)));

        }catch (Exception e){
            holder.checkBoxTitle.setText(e.toString());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener {
        CardView itemCardView;
        CheckBox checkBoxTitle;
        ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            checkBoxTitle = itemView.findViewById(R.id.textViewTitle);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            checkBoxTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view instanceof CheckBox){
                if(mCheckListener != null)
                    mCheckListener.onItemCheck(view,getAdapterPosition(),((CheckBox)view).isChecked());
            }else{
                if (mClickListener != null)
                    mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mClickListener != null)
                return mClickListener.onItemLongClick(view, getAdapterPosition());
            return false;
        }
    }

    public Task getItem(int position) {
        return mData.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setCheckListener(ItemCheckListener itemCheckListener) {
        this.mCheckListener = itemCheckListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }

    public interface ItemCheckListener {
        void onItemCheck(View view,int position,boolean isChecked);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
