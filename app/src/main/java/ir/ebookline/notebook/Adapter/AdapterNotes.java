package ir.ebookline.notebook.Adapter;

import android.app.Activity;
import android.content.*;
import android.support.annotation.*;
import android.support.v7.widget.*;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.*;

import ebookline.notebooksrc.R;
import ir.ebookline.notebook.Activities.MainActivity;
import ir.ebookline.notebook.Activities.ViewNoteActivity;
import ir.ebookline.notebook.JalaliCalendar;
import ir.ebookline.notebook.Model.ModelNotes;

public class AdapterNotes extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    public ArrayList<ModelNotes> Items;

    public Context con;

    public AdapterNotes(Context c, ArrayList<ModelNotes> itemList)
    {
        con = c;
        Items = itemList;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);

        return new AdapterNotes.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position)
    {
        int viewType=getItemViewType(position);
       populateItem((AdapterNotes.ItemViewHolder) viewHolder, position);
    }

    @Override
    public int getItemCount()
    {
        return Items == null ? 0 : Items.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return 1;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder
    {
        public TextView TextViewTitle , TextViewText , TextViewOther ;
        public ImageView ImageViewPin ;
        public ItemViewHolder(View ItemView)
        {
            super(ItemView);
            TextViewTitle = (TextView) ItemView.findViewById(R.id.Title);
            TextViewText = (TextView) ItemView.findViewById(R.id.Text);
            TextViewOther = (TextView) ItemView.findViewById(R.id.Other);
            ImageViewPin = ItemView.findViewById(R.id.Pin);
        }
    }


    private void populateItem(final ItemViewHolder viewHolder, int position)
    {
        ModelNotes mn = Items.get(position);
        viewHolder.TextViewTitle.setText(mn.getTitle());
        viewHolder.TextViewText.setText(mn.getNote());


        viewHolder.TextViewOther.setText(""+new JalaliCalendar().getJalaliDate(new Date(Long.parseLong(mn.getDate())))
                +"-"+new SimpleDateFormat("HH:mm:ss").format(new Date(Long.parseLong(mn.getDate()))));

        Animation anim;
        if(position%2==0){
            anim = AnimationUtils.loadAnimation( con , R.anim.right_to_left);
        }else{
            anim = AnimationUtils.loadAnimation( con , R.anim.left_to_right);
        }
        viewHolder.itemView.setAnimation(anim);

        if(mn.getPin().equals("1")) {
            viewHolder.ImageViewPin.setVisibility(View.VISIBLE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toViewNote = new Intent(con , ViewNoteActivity.class);
                toViewNote.putExtra("id",mn.getId());
                con.startActivity(toViewNote);
                ((Activity)con).finish();
            }
        });

    }

}

