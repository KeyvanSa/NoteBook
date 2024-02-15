package ebookline.notepad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import ebookline.notepad.Model.FileModel;
import ebookline.notepad.R;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder>
{
    private final List<FileModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private final Context context;

    public FilePickerAdapter(Context context, List<FileModel> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_file, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        FileModel file = mData.get(position);

        holder.textViewTitle.setText(file.getName());


        if(position==0)
            holder.imageViewIcon.setImageResource(R.drawable.back);
        else if(new File(file.getPath()).isDirectory())
            holder.imageViewIcon.setImageResource(R.drawable.folder);
        else holder.imageViewIcon.setImageResource(R.drawable.file);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewTitle;
        ImageView imageViewIcon;
        ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            imageViewIcon = itemView.findViewById(R.id.imageViewIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public FileModel getItem(int position) {
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
