package ebookline.notepad.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

import ebookline.notepad.Model.Category;
import ebookline.notepad.R;

public class CategoryAdapter extends ArrayAdapter<Category>
{
    Context context;
    int resource, textViewResourceId;
    List<Category> items, tempItems, suggestions;

    public CategoryAdapter(Context context, int resource, int textViewResourceId, List<Category> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<>(items);
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_category, parent, false);
        }
        Category category = items.get(position);

        TextView textViewTitle = view.findViewById(R.id.textViewTitle);
        LinearLayout linearLayoutColor=view.findViewById(R.id.linearLayoutColor);

        textViewTitle.setText(category.getTitle());

        try{
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
            shape.setColor(Color.parseColor(category.getColor()));

            float[]hsv=new float[3];
            Color.colorToHSV(Color.parseColor(category.getColor()),hsv);
            hsv[2] *= 0.8f;
            shape.setStroke(5,Color.HSVToColor(hsv));

            linearLayoutColor.setBackground(shape);

        }catch (Exception e){
            textViewTitle.setText(e.toString());
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    final Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Category) resultValue).getTitle();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Category category : tempItems) {
                    if (category.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(category);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            assert results.values != null;
            List<Category> filterList = (List<Category>) results.values;
            if(results.count>0){
                clear();
                for (Category category : filterList){
                    add(category);
                    notifyDataSetChanged();
                }
            }

        }

    };

}
