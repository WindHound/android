package windshift.windhound;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RaceFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_race, container, false);
        String[] values = new String[] {"First Race", "Race 2", "Race 3", "Race 4", "Race 5",
                "Race 6", "Race 7", "Race 8", "Race 9", "Race 10", "Race 11", "Race 12", "Race 13",
                "Race 14", "Race 15", "Race 16", "Race 17", "Race 18", "Race 19", "Race 20",
                "Race 21", "Race 22", "Race 23", "Race 24", "Last Race"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((HomeActivity)getActivity()).displayRace(v, position);
    }

}

