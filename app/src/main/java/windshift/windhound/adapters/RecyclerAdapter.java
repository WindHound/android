package windshift.windhound.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import windshift.windhound.HomeActivity;
import windshift.windhound.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private Long[] ids;
    private int object;
    // 0 = ongoing championship, 1 = past championship
    // 2 = ongoing event, 3 = past event
    // 4 = ongoing race, 5 = past race
    private String[] dataset;
    private String[] dateset;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private long id;
        private int object;
        private final TextView textViewName;
        private final TextView textViewDate;
        private View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            textViewName = v.findViewById(R.id.textView_name);
            textViewDate = v.findViewById(R.id.textView_date);
        }

        public void setOnClick(final Long id, final int object) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity ha = (HomeActivity) view.getContext();
                    ha.display(id, object);
                }
            });
        }

        public TextView getTextViewName() {
            return textViewName;
        }

        public TextView getTextViewDate() {
            return textViewDate;
        }

    }

    public RecyclerAdapter(Long[] ids, int object, String[] dataset, String[] dateset) {
        this.ids = ids;
        this.object = object;
        this.dataset = dataset;
        this.dateset = dateset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.setOnClick(ids[position], object);
        viewHolder.getTextViewName().setText(dataset[position]);
        viewHolder.getTextViewDate().setText(dateset[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataset.length;
    }

}
