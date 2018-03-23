package windshift.windhound.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import windshift.windhound.R;
import windshift.windhound.adapters.TabsAdapter;
import windshift.windhound.objects.Race;

public class RaceFragment extends Fragment {

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private TabLayout tabLayout;

    private Race[] races;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_race, container, false);

        viewPager = rootView.findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText(getResources().getString(R.string.title_ongoing));
        tabLayout.getTabAt(1).setText(getResources().getString(R.string.title_past));

        new HttpRequestTask().execute();

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        tabsAdapter = new TabsAdapter(getChildFragmentManager());
        tabsAdapter.addFragment(new OngoingRaceFragment());
        tabsAdapter.addFragment(new PastRaceFragment());
        viewPager.setAdapter(tabsAdapter);
    }

    public Race getRace(Long id) {
        for (int i = 0; i < races.length; i++) {
            if (races[i].getID() == id) {
                return races[i];
            }
        }
        return null;
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
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Race[] races) {
            if (races != null) {
                List<Race> ongoing = new ArrayList<>();
                List<Race> past = new ArrayList<>();
                Calendar now = Calendar.getInstance();
                for (int i = 0; i < races.length; i++) {
                    if (races[i].getEndDate().after(now)) {
                        ongoing.add(races[i]);
                    } else {
                        past.add(races[i]);
                    }
                }
                OngoingRaceFragment oef = (OngoingRaceFragment) tabsAdapter.getItem(0);
                oef.updateList(ongoing);
                PastRaceFragment pef = (PastRaceFragment) tabsAdapter.getItem(1);
                pef.updateList(past);
            }
        }

    }

}

