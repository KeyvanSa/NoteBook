package ebookline.notepad.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.channels.ClosedByInterruptException;
import java.util.List;

import ebookline.notepad.R;
import ebookline.notepad.Util.HelperClass;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>
{
    private final List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final int selectedItem;

    private final Context context;
    private HelperClass helper;

    public ColorAdapter(Context context, List<String> data,int selectedItem) {
        this.context = context;
        this.selectedItem=selectedItem;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.helper=new HelperClass(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        GradientDrawable shape = helper.setBackgroundShape(mData.get(position),0,100,0);
        shape.setShape(GradientDrawable.OVAL);

        if(position==selectedItem){
            float[]hsv=new float[3];
            Color.colorToHSV(Color.parseColor(mData.get(position)),hsv);
            hsv[2] *= 0.8f;
            shape.setStroke(5,Color.HSVToColor(hsv));

            TextView textView=new TextView(context);
            textView.setWidth(55);
            textView.setHeight(55);
            textView.setText("√");
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(18f);
            textView.setTextColor(Color.parseColor(helper.getMaterialColorCode(mData.get(position),50)));
            holder.linearLayout.addView(textView);
        }

        holder.linearLayout.setBackground(shape);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LinearLayout linearLayout;
        ViewHolder(View itemView) {
            super(itemView);
          linearLayout = itemView.findViewById(R.id.linearColor);
          itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public String getItem(int position) {
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