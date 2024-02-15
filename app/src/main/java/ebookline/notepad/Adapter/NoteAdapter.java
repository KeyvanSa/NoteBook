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

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>
{
    private final List<Note> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private final HelperClass helper;
    private final DBHelper db;

    private final Context context;

    public NoteAdapter(Context context, List<Note> data) {
        this.context = context;
        helper = new HelperClass(context);
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

            String[] sDate = holder.textViewDate.getText().toString().split("-")[1].split("/");
            if(Integer.parseInt(sDate[1])==1)
                holder.textViewDate.setText(String.format("%s فروردین",sDate[2]));
            else if(Integer.parseInt(sDate[1])==2)
                holder.textViewDate.setText(String.format("%s اردیبهشت",sDate[2]));
            else if(Integer.parseInt(sDate[1])==3)
                holder.textViewDate.setText(String.format("%s خرداد",sDate[2]));
            else if(Integer.parseInt(sDate[1])==4)
                holder.textViewDate.setText(String.format("%s تیر",sDate[2]));
            else if(Integer.parseInt(sDate[1])==5)
                holder.textViewDate.setText(String.format("%s مرداد",sDate[2]));
            else if(Integer.parseInt(sDate[1])==6)
                holder.textViewDate.setText(String.format("%s شهریور",sDate[2]));
            else if(Integer.parseInt(sDate[1])==7)
                holder.textViewDate.setText(String.format("%s مهر",sDate[2]));
            else if(Integer.parseInt(sDate[1])==8)
                holder.textViewDate.setText(String.format("%s آبان",sDate[2]));
            else if(Integer.parseInt(sDate[1])==9)
                holder.textViewDate.setText(String.format("%s آذر",sDate[2]));
            else if(Integer.parseInt(sDate[1])==10)
                holder.textViewDate.setText(String.format("%s دی",sDate[2]));
            else if(Integer.parseInt(sDate[1])==11)
                holder.textViewDate.setText(String.format("%s بهمن",sDate[2]));
            else if(Integer.parseInt(sDate[1])==12)
                holder.textViewDate.setText(String.format("%s اسفند",sDate[2]));

        }catch (Exception e){
            holder.textViewText.setText(e.toString());
            holder.textViewTitle.setText(context.getResources().getString(R.string.error));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView itemCardView;
        TextView textViewTitle;
        TextView textViewText;
        TextView textViewDate;
        TextView textViewCategory;
        ViewHolder(View itemView) {
            super(itemView);
            itemCardView = itemView.findViewById(R.id.itemCardView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewDate = itemView.findViewById(R.id.textViewDate);
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