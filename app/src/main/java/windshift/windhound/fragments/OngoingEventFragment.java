package windshift.windhound.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import windshift.windhound.R;
import windshift.windhound.adapters.RecyclerAdapter;

public class OngoingEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ongoing_event, container,
                false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        String[] values = new String[] {"First Event", "Event 2", "Event 3", "Event 4", "Event 5",
                "Event 6", "Event 7", "Event 8", "Event 9", "Event 10", "Event 11", "Event 12",
                "Event 13", "Event 14", "Event 15", "Event 16", "Event 17", "Event 18", "Event 19",
                "Event 20", "Event 21", "Event 22", "Event 23", "Event 24", "Last Event"};

        adapter = new RecyclerAdapter(values);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

}
