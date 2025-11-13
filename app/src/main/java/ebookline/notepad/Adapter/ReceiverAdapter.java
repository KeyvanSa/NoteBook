package ebookline.notepad.Adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ebookline.notepad.Model.Receiver;
import ebookline.notepad.R;
import ebookline.notepad.Util.Constants;
import ebookline.notepad.Util.HelperClass;

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ViewHolder>
{
    private final List<Receiver> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ItemLongClickListener mLongClickListener;

    private final Context context;
    private HelperClass helper;

    public ReceiverAdapter(Context context, List<Receiver> data) {
        this.context = context;
        helper = new HelperClass(context);
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_receiver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Receiver receiver = mData.get(position);

        try{

            holder.textViewTitle.setText(receiver.getTitle());
            holder.textViewText.setText(receiver.getText());
            holder.textViewSender.setText(receiver.getSender());
            holder.textViewDate.setText(helper.getDate(receiver.getTime()));

            if(receiver.getType().equals(Constants.SMS))
                holder.textViewType.setText(context.getResources().getString(R.string.receiver_type_sms));
            else {
                holder.textViewType.setText(context.getResources().getString(R.string.receiver_type_app));

                holder.imageViewApp.setVisibility(View.VISIBLE);

                PackageManager pm = context.getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(receiver.getSender(),0);
                if(appInfo!=null && pm.getApplicationIcon(appInfo)!=null) {
                    holder.imageViewApp.setImageDrawable(pm.getApplicationIcon(appInfo));
                    holder.textViewSender.setText(pm.getApplicationLabel(appInfo));
                }
            }

        }catch (Exception e){
            holder.textViewText.setText(e.toString());
            holder.textViewTitle.setText(context.getResources().getString(R.string.error));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPin;
        ImageView imageViewApp;
        TextView textViewTitle;
        TextView textViewText;
        TextView textViewDate;
        TextView textViewType;
        TextView textViewSender;
        ViewHolder(View itemView) {
            super(itemView);
            imageViewPin = itemView.findViewById(R.id.imageViewPin);
            imageViewApp = itemView.findViewById(R.id.imageViewApp);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewText = itemView.findViewById(R.id.textViewText);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewSender = itemView.findViewById(R.id.textViewSender);

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

    public Receiver getItem(int position) {
        return mData.get(position);
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