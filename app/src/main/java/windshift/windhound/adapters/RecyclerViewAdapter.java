package windshift.windhound.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import windshift.windhound.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Integer> boatColours = new ArrayList<>();
    private List<Long> boatIDs = new ArrayList<>();
    private LayoutInflater rowInflater;
    private ItemClickListener boatClickListener;

    // data is passed into the constructor
    public RecyclerViewAdapter(Context context, List<Integer> colours, List<Long> ids) {
        this.rowInflater = LayoutInflater.from(context);
        this.boatColours = colours;
        this.boatIDs = ids;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = rowInflater.inflate(R.layout.recyclerview_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int color = boatColours.get(position);
        holder.myView.setBackgroundColor(color);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return boatIDs.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View myView;
        public TextView myTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView.findViewById(R.id.colorView);
            myTextView = itemView.findViewById(R.id.boatID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (boatClickListener != null) boatClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Long getItem(int id) {
        return boatIDs.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.boatClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
