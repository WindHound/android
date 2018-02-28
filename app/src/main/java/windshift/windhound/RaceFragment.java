package windshift.windhound;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RaceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_race, container, false);

        // Place holder data for the expandable list view
        List<String> listHeaderData = new ArrayList<>();
        HashMap<String, List<String>> listChildData = new HashMap<>();
        listHeaderData.add("Upcoming");
        listHeaderData.add("Past");
        List<String> upcoming = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            upcoming.add("Upcoming Race " + Integer.toString(i));
        }
        List<String> past = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            past.add("Past Race " + Integer.toString(i));
        }
        listChildData.put(listHeaderData.get(0), upcoming);
        listChildData.put(listHeaderData.get(1), past);

        // Expandable list view configuration
        ExpandableListView expandableListView = rootView.findViewById(R.id.expandableListView);
        CustomExpandableListAdapter adapter = new CustomExpandableListAdapter(rootView.getContext(),
                listHeaderData, listChildData);
        expandableListView.setAdapter(adapter);
        expandableListView.expandGroup(0);
        expandableListView.expandGroup(1);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if (groupPosition == 0) {
                    ((HomeActivity)getActivity()).displayRace(v, childPosition, true);
                } else if (groupPosition == 1) {
                    ((HomeActivity)getActivity()).displayRace(v, childPosition, false);
                }
                return false;
            }
        });
        // Disable collapse of expandable list view
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                return true;
            }
        });

        return rootView;
    }

}

