package ebookline.notepad.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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

    private final HelperClass helper;
    private final SharedHelper shared;
    private final DBHelper db;

    private final Context context;

    public NoteAdapter(Context context, List<Note> data) {
        this.context = context;
        helper = new HelperClass(context);
        shared=new SharedHelper(context);
        db = new DBHelper(context);
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
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
            holder.itemCardView.setCardBackgroundColor(Color.parseColor(note.getColor()));

            //String[] sDate = holder.textViewDate.getText().toString().split("-")[1].split("/");

            if(context.getClass()==TrashActivity.class && shared.getBoolean(Constants.USE_EXPIRED_NOTE))
                setExpiredDay(position,holder.textViewExpiredDate);

        }catch (Exception e){
            holder.textViewText.setText(e.toString());
            holder.textViewTitle.setText(context.getResources().getString(R.string.error));
        }
    }

    private void setExpiredDay(int position, TextView textViewExpiredDate)
    {
        long day = (java.lang.System.currentTimeMillis() - Long.parseLong(getItem(position).getaTime()) )/1000;
        int lastDays = 29 - (int)(day/(24*60*60));

        textViewExpiredDate.setText(
                String.format(
                        Locale.ROOT,
                        context.getResources().getString(R.string.left_days)
                        ,lastDays));

        if(lastDays <= 10)
           textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.message_error));
        else if (lastDays <= 20)
            textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.color_accent));
        else textViewExpiredDate.setTextColor(context.getResources().getColor(R.color.message_success_dark));

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView itemCardView;
        TextView textViewTitle;
        TextView textViewText;
        TextView textViewDate;
        TextView textViewExpiredDate;
        TextView textViewCategory;
        ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewExpiredDate = itemView.findViewById(R.id.textViewExpiredDate);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public Note getItem(int position) {
        return mData.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
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