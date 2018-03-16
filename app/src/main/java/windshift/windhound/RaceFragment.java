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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import windshift.windhound.race.Race;

public class RaceFragment extends Fragment {

    private boolean loadedUpcoming;
    private boolean loadedPast;
    private CustomExpandableListAdapter adapter;
    private HashMap<String, List<String>> listChildData;
    private List<String> listHeaderData;
    private List<String> past;
    private List<String> upcoming;
    private Race[] races;
    private List<Race> upcomingRaces;
    private List<Race> pastRaces;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_race, container, false);

        loadedUpcoming = false;
        loadedPast = false;
        listChildData = new HashMap<>();
        listHeaderData = new ArrayList<>();
        past = new ArrayList<>();
        upcoming = new ArrayList<>();

        // Expandable list view configuration
        ExpandableListView expandableListView = rootView.findViewById(R.id.expandableListView);
        listHeaderData.add("Upcoming Races");
        listHeaderData.add("Past Races");
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
                if (groupPosition == 0 && loadedUpcoming) {
                    ((HomeActivity)getActivity()).displayRace(v, upcomingRaces.get(childPosition),
                            true);
                } else if (groupPosition == 1 & loadedPast) {
                    ((HomeActivity)getActivity()).displayRace(v, pastRaces.get(childPosition),
                            false);
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
                        "/structure/race/all/";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Long[] race_ids = restTemplate.getForObject(url, Long[].class);
                races = new Race[race_ids.length];
                for (int i = 0; i < race_ids.length; i++) {
                    final String raceURL = getResources().getString((R.string.server_address)) +
                            "/structure/race/get/" + race_ids[i].toString();
                    races[i] = restTemplate.getForObject(raceURL, Race.class);
                }
                return races;
            } catch (Exception e) {
                upcoming.add(getResources().getString(R.string.connection_error));
                past.add(getResources().getString(R.string.connection_error));
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Race[] races) {
            // Updates the expandable list view with received races
            if (races != null) {
                if (races.length != 0) {
                    upcomingRaces = new ArrayList<>();
                    pastRaces = new ArrayList<>();
                    for (int i = 0; i < races.length; i++) {
                        if (races[i].getEndDate().after(Calendar.getInstance().getTime())) {
                            upcomingRaces.add(races[i]);
                            upcoming.add(races[i].getName());
                        } else {
                            pastRaces.add(races[i]);
                            past.add(races[i].getName());
                        }
                    }
                }
                if (upcoming.size() == 1) {
                    loadedUpcoming = false;
                    upcoming.add(getResources().getString(R.string.upcoming_exist));
                } else {
                    loadedUpcoming = true;
                }
                if (past.size() == 1) {
                    loadedPast = false;
                    past.add(getResources().getString(R.string.past_exist));
                } else {
                    loadedPast = true;
                }
            }
            upcoming.remove(0);
            past.remove(0);
            listChildData.put(listHeaderData.get(0), upcoming);
            listChildData.put(listHeaderData.get(1), past);
            adapter.notifyDataSetChanged();
        }

    }

}
