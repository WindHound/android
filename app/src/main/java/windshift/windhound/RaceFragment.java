package windshift.windhound;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import windshift.windhound.race.Race;

public class RaceFragment extends Fragment {

    private CustomExpandableListAdapter adapter;
    private HashMap<String, List<String>> listChildData;
    private List<String> listHeaderData;
    private List<String> past;
    private List<String> upcoming;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_race, container, false);

        listChildData = new HashMap<>();
        listHeaderData = new ArrayList<>();
        past = new ArrayList<>();
        upcoming = new ArrayList<>();

        // Expandable list view configuration
        ExpandableListView expandableListView = rootView.findViewById(R.id.expandableListView);
        listHeaderData.add("Upcoming");
        listHeaderData.add("Past");
        upcoming.add("Unable to reach server.");
        past.add("Unable to reach server.");
        listChildData.put(listHeaderData.get(0), upcoming);
        listChildData.put(listHeaderData.get(1), past);
        adapter = new CustomExpandableListAdapter(rootView.getContext(), listHeaderData,
                listChildData);
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
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition,
                                        long id) {
                // Disables collapse of expandable list view groups
                return true;
            }
        });

        new HttpRequestTask().execute();

        // Placeholder past races
        past.remove(0);
        for (int i = 0; i < 10; i++) {
            past.add("Past Race " + Integer.toString(i + 1));
        }
        listChildData.put(listHeaderData.get(1), past);
        adapter.notifyDataSetChanged();

        return rootView;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Race[]> {

        @Override
        protected Race[] doInBackground(Void... params) {
            try {
                // Requests all race ids, then each race object by race id
                final String url = "http://192.168.0.51:8080/structure/all/race/";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Long[] race_ids = restTemplate.getForObject(url, Long[].class);
                Race[] races = new Race[race_ids.length];
                for (int i = 0; i < race_ids.length; i++) {
                    final String raceURL = "http://192.168.0.51:8080/structure/get/race/" +
                            race_ids[i].toString();
                    races[i] = restTemplate.getForObject(raceURL, Race.class);
                }
                return races;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Race[] races) {
            // Updates the expandable list view with received races
            if (races != null) {
                if (races.length != 0) {
                    upcoming.remove(0);
                    for (int i = 0; i < races.length; i++) {
                        upcoming.add("Upcoming Race " + races[i].getID());
                    }
                    listChildData.put(listHeaderData.get(0), upcoming);
                    adapter.notifyDataSetChanged();
                } else {
                    upcoming.remove(0);
                    upcoming.add("No races exist.");
                    listChildData.put(listHeaderData.get(0), upcoming);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }

}
