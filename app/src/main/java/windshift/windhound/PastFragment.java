package windshift.windhound;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class PastFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_past, container, false);

        String[] values = new String[] {"First Past Event", "Past Event 2", "Past Event 3",
                "Past Event 4", "Past Event 5", "Past Event 6", "Past Event 7", "Past Event 8",
                "Past Event 9", "Past Event 10", "Past Event 11", "Past Event 12", "Past Event 13",
                "Past Event 14", "Past Event 15", "Past Event 16", "Past Event 17", "Past Event 18",
                "Past Event 19", "Past Event 20", "Past Event 21", "Past Event 22", "Past Event 23",
                "Past Event 24", "Last Past Event"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast toast = Toast.makeText(getActivity(), "Past Event: " + (position + 1), Toast.LENGTH_SHORT);
        toast.show();
    }

}

