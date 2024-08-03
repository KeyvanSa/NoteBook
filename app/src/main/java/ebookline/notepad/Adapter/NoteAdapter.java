package ebookline.notepad.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ebookline.notepad.Activity.TrashActivity;
import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Shared.SharedHelper;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
{
    private final List<Note> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    private final HelperClass helper;
    private final SharedHelper shared;
    private final DBHelper db;

    private final Context context;

    public boolean selectMode=false;
    public ArrayList<Boolean>selectedItems;

    public NoteAdapter(Context context, List<Note> data) {
        this.context = context;
        helper = new HelperClass(context);
        shared=new SharedHelper(context);
        db = new DBHelper(context);
        selectedItems=new ArrayList<>();
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        for(int i=0;i<data.size();i++)
            selectedItems.add(false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Note note = mData.get(position);

        Category category = new Category();
        if(note.getCategory()!=0)
            category=db.getCategory(note.getCategory());
        else category.setTitle(context.getResources().getString(R.string.no_category));

        try{
            holder.textViewTitle.setText(note.getTitle());
            holder.textViewText.setText(note.getText());
            holder.textViewDate.setText(helper.getDate(note.getaTime()));
            holder.textViewCategory.setText(category.getTitle());

            holder.itemCardView.setCardBackgroundColor(Color.parseColor(helper.getMaterialColorCode(note.getColor(),0)));

            if(context.getClass()==TrashActivity.class && shared.getBoolean(Constants.USE_EXPIRED_NOTE))
                setExpiredDay(position,holder.textViewExpiredDate);

            if(note.getPin()==1 && context.getClass()!=TrashActivity.class)
                holder.imageViewPin.setVisibility(View.VISIBLE);
            else holder.imageViewPin.setVisibility(View.GONE);

            if(selectMode && selectedItems.get(position))
                holder.checkboxChooseItem.setVisibility(View.VISIBLE);
            else holder.checkboxChooseItem.setVisibility(View.GONE);
            holder.checkboxChooseItem.setChecked(selectedItems.get(position));
        }catch (Exception e){
            holder.textViewText.setText(e.toString());
            holder.textViewTitle.setText(context.getResources().getString(R.string.error));
        }
    }

    private void setExpiredDay(int position, TextView textViewExpiredDate)
    {
        long day = (java.lang.System.currentTimeMillis() - Long.parseLong(getItem(position).getaTime()) )/1000;
        int lastDays = 29 - (int)(day/(24*60*60));

        if(lastDays<0)
            lastDays = 30 + (lastDays * -1);

        textViewExpiredDate.setText(String.format(Locale.ROOT,
                context.getResources().getString(
                        lastDays>30 ?
                                R.string.days_ago :
                                R.string.left_days),lastDays));

        if(lastDays <= 10)
           textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.message_error));
        else if (lastDays <= 20)
            textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.color_accent));
        else textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.message_success_dark));

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView itemCardView;
        ImageView imageViewPin;
        TextView textViewTitle;
        TextView textViewText;
        TextView textViewDate;
        TextView textViewExpiredDate;
        TextView textViewCategory;
        CheckBox checkboxChooseItem;
        ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            imageViewPin = itemView.findViewById(R.id.imageViewPin);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewExpiredDate = itemView.findViewById(R.id.textViewExpiredDate);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            checkboxChooseItem = itemView.findViewById(R.id.checkboxChooseItem);

            itemView.setOnClickListener(view -> {
                if (mClickListener == null)
                    return;

                mClickListener.onItemClick(view, getAdapterPosition());
            });

            itemView.setOnLongClickListener(view -> {

                if (mLongClickListener == null)
                    return false;

                mLongClickListener.onItemLongClick(getAdapterPosition());
                return true;
            });
        }
    }

    public Note getItem(int position) {
        return mData.get(position);
    }

    public int checkNumberSelectedItems(){
        int number=0;
        if(selectedItems==null) return number;
        for(int i=0;i<selectedItems.size();i++){
            if(selectedItems.get(i))
                number++;
        }
        return number;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onItemLongClick(int position);
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