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
import windshift.windhound.objects.Championship;
import windshift.windhound.objects.Event;

public class ChampionshipFragment extends Fragment {

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private TabLayout tabLayout;

    private Championship[] championships;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_championship, container, false);

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
        tabsAdapter.addFragment(new OngoingChampionshipFragment());
        tabsAdapter.addFragment(new PastChampionshipFragment());
        viewPager.setAdapter(tabsAdapter);
    }

    public Championship getChampionship(Long id) {
        for (int i = 0; i < championships.length; i++) {
            if (championships[i].getID() == id) {
                return championships[i];
            }
        }
        return null;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Championship[]> {

        @Override
        protected Championship[] doInBackground(Void... params) {
            try {
                // Requests all championship ids, then each championship object by championship id
                final String url = getResources().getString((R.string.server_address)) +
                        "/structure/championship/all";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Long[] championship_ids = restTemplate.getForObject(url, Long[].class);
                championships = new Championship[championship_ids.length];
                for (int i = 0; i < championship_ids.length; i++) {
                    final String championshipURL = getResources().getString((R.string.server_address)) +
                            "/structure/championship/get/" + championship_ids[i].toString();
                    championships[i] = restTemplate.getForObject(championshipURL, Championship.class);
                }
                return championships;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Championship[] championships) {
            if (championships != null) {
                List<Championship> ongoing = new ArrayList<>();
                List<Championship> past = new ArrayList<>();
                Calendar now = Calendar.getInstance();
                for (int i = 0; i < championships.length; i++) {
                    if (championships[i].getEndDate().after(now)) {
                        ongoing.add(championships[i]);
                    } else {
                        past.add(championships[i]);
                    }
                }
                OngoingChampionshipFragment oef = (OngoingChampionshipFragment)
                        tabsAdapter.getItem(0);
                oef.updateList(ongoing);
                PastChampionshipFragment pef = (PastChampionshipFragment)
                        tabsAdapter.getItem(1);
                pef.updateList(past);
            }
        }

    }

}

