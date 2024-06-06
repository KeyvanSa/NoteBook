package ebookline.notepad.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ebookline.notepad.Database.DBHelper;
import ebookline.notepad.Model.Category;
import ebookline.notepad.Model.Note;
import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class CategoryAdapter2 extends RecyclerView.Adapter<CategoryAdapter2.ViewHolder>
{
    private final List<Category> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    private final HelperClass helper;
    private final DBHelper db;

    private final Context context;

    public CategoryAdapter2(Context context, List<Category> data) {
        this.context = context;
        helper = new HelperClass(context);
        db = new DBHelper(context);
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_category2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Category category = mData.get(position);

        try{
            holder.textViewTitle.setText(category.getTitle());

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
            shape.setColor(Color.parseColor(category.getColor()));

            float[]hsv=new float[3];
            Color.colorToHSV(Color.parseColor(category.getColor()),hsv);
            hsv[2] *= 0.8f;
            shape.setStroke(5,Color.HSVToColor(hsv));

            holder.linearLayoutColor.setBackground(shape);
        }catch (Exception e){
            holder.textViewTitle.setText(e.toString());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnLongClickListener{
        TextView textViewTitle;
        LinearLayout linearLayoutColor;
        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            linearLayoutColor = itemView.findViewById(R.id.linearLayoutColor);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onCategoryItemClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view)
        {
            if (mLongClickListener != null) {
                mLongClickListener.onCategoryItemLongClick(view, getAdapterPosition());
                return true;
            }
            return false;
        }
    }

    public Category getItem(int position) {
        return mData.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void setLongClickListener(ItemLongClickListener itemLongClickListener) {
        this.mLongClickListener = itemLongClickListener;
    }

    public interface ItemClickListener {
        void onCategoryItemClick(View view, int position);
    }

    public interface ItemLongClickListener {
        void onCategoryItemLongClick(View view, int position);
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