package windshift.windhound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import windshift.windhound.adapters.RecyclerAdapter;

public class ChampionshipFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_championship, container, false);

        /*
        String[] values = new String[] {"First Championship", "Championship 2", "Championship 3",
                "Championship 4", "Championship 5", "Championship 6", "Championship 7",
                "Championship 8", "Championship 9", "Championship 10", "Championship 11",
                "Championship 12", "Championship 13", "Championship 14", "Championship 15",
                "Championship 16", "Championship 17", "Championship 18", "Championship 19",
                "Championship 20", "Championship 21", "Championship 22", "Championship 23",
                "Championship 24", "Last Championship"};

        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(values);
        recyclerView.setAdapter(adapter);
        */

        return rootView;
    }

}
