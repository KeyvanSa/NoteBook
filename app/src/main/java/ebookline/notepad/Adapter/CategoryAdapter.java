package ebookline.notepad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

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
        textViewTitle.setText(category.getTitle());

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
