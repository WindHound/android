package windshift.windhound;

import android.content.Intent;
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
    private Race[] races;

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
        upcoming.add(getResources().getString(R.string.connection_loading));
        past.add(getResources().getString(R.string.connection_loading));
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
                    ((HomeActivity)getActivity()).displayRace(v, races[childPosition], true);
                } else if (groupPosition == 1) {
                    ((HomeActivity)getActivity()).displayRace(v, races[childPosition], false);
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

        return rootView;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Race[]> {

        @Override
        protected Race[] doInBackground(Void... params) {
            try {
                // Requests all race ids, then each race object by race id
                final String url = getResources().getString((R.string.server_address)) +
                        "/structure/all/race/";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Long[] race_ids = restTemplate.getForObject(url, Long[].class);
                races = new Race[race_ids.length];
                for (int i = 0; i < race_ids.length; i++) {
                    final String raceURL = getResources().getString((R.string.server_address)) +
                            "/structure/get/race/" + race_ids[i].toString();
                    races[i] = restTemplate.getForObject(raceURL, Race.class);
                }
                return races;
            } catch (Exception e) {
                upcoming.add(getResources().getString(R.string.connection_error));
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Race[] races) {
            // Updates the expandable list view with received races
            if (races != null) {
                if (races.length != 0) {
                    for (int i = 0; i < races.length; i++) {
                        upcoming.add(races[i].getName());
                    }
                } else {
                    upcoming.add("No races exist.");
                }
            }
            upcoming.remove(0);
            listChildData.put(listHeaderData.get(0), upcoming);
            adapter.notifyDataSetChanged();
        }

    }

}
