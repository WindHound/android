package windshift.windhound;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class UpcomingFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);

        String[] values = new String[] {"First Event", "Event 2", "Event 3", "Event 4", "Event 5",
                "Event 6", "Event 7", "Event 8", "Event 9", "Event 10", "Event 11", "Event 12",
                "Event 13", "Event 14", "Event 15", "Event 16", "Event 17", "Event 18", "Event 19",
                "Event 20", "Event 21", "Event 22", "Event 23", "Event 24", "Last Event"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Toast toast = Toast.makeText(getActivity(), "Item: " + position, Toast.LENGTH_SHORT);
        toast.show();
    }

}
