package ebookline.notepad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import  ebookline.notepad.databinding.ItemAppBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ebookline.notepad.Model.Apps;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder>
{
    private final ArrayList<Apps> mData;
    private Context context;
    private ItemClickListener mClickListener;

    public AppAdapter(Context context, ArrayList<Apps> data) {
        this.context=context;
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemAppBinding binding = ItemAppBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemAppBinding binding;
        ViewHolder(ItemAppBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null)
                mClickListener.onItemClick(getItem(getAdapterPosition()),getAdapterPosition());
        }

        public void bind(Apps app)
        {
           binding.textViewName.setText(app.getName());
           binding.textViewPackageName.setText(app.getPackageName());
           binding.imageViewIcon.setImageDrawable(app.getIcon());
        }
    }

    public Apps getItem(int position) {
        return mData.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(Apps app,int position);
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

