package ebookline.notepad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ebookline.notepad.Model.Menu;
import ebookline.notepad.R;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>
{
    private final List<Menu> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public MenuAdapter(Context context, List<Menu> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_menu , parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Menu menu =  mData.get(position);

        try{
            holder.textViewMenuTitle.setText(menu.getTitle());
            if(menu.getIcon()!=0)
                holder.imageViewMenuIcon.setImageResource(menu.getIcon());
        }catch (Exception e){
            holder.textViewMenuTitle.setText(e.toString());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewMenuTitle;
        ImageView imageViewMenuIcon;
        ViewHolder(View itemView) {
            super(itemView);
            textViewMenuTitle = itemView.findViewById(R.id.textViewMenuTitle);
            imageViewMenuIcon = itemView.findViewById(R.id.imageViewMenuIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onMenuItemClick(view, getAdapterPosition());
        }
    }

    public Menu getItem(int position) {
        return mData.get(position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onMenuItemClick(View view, int position);
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