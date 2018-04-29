package windshift.windhound.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import windshift.windhound.R;
import windshift.windhound.adapters.RecyclerAdapter;
import windshift.windhound.objects.EventDTO;

public class PastEventFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_past_event, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    public void updateList(List<EventDTO> events) {
        Long[] ids = new Long[events.size()];
        String[] names = new String[events.size()];
        String[] dates = new String[events.size()];
        DateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
        for (int i = 0; i < events.size(); i++) {
            ids[i] = events.get(i).getId();
            names[i] = events.get(i).getName();
            String start = dateFormat.format(events.get(i).getStartDate());
            String end = dateFormat.format(events.get(i).getEndDate());
            if (start.equals(end)) {
                dates[i] = start;
            } else {
                dates[i] = start + " - " + end;
            }
        }
        adapter = new RecyclerAdapter(ids, 3, names, dates);
        rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
        recyclerView.setAdapter(adapter);
    }

}
