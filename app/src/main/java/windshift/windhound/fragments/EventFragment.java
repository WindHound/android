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
import windshift.windhound.objects.EventDTO;

public class EventFragment extends Fragment {

    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private TabLayout tabLayout;

    private EventDTO[] events;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

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
        tabsAdapter.addFragment(new OngoingEventFragment());
        tabsAdapter.addFragment(new PastEventFragment());
        viewPager.setAdapter(tabsAdapter);
    }

    public EventDTO getEvent(Long id) {
        for (int i = 0; i < events.length; i++) {
            if (events[i].getId() == id) {
                return events[i];
            }
        }
        return null;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, EventDTO[]> {

        @Override
        protected EventDTO[] doInBackground(Void... params) {
            try {
                // Requests all event ids, then each race object by event id
                final String url = getResources().getString((R.string.server_address)) +
                        "/structure/event/all";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Long[] event_ids = restTemplate.getForObject(url, Long[].class);
                events = new EventDTO[event_ids.length];
                for (int i = 0; i < event_ids.length; i++) {
                    final String eventURL = getResources().getString((R.string.server_address)) +
                            "/structure/event/get/" + event_ids[i].toString();
                    events[i] = restTemplate.getForObject(eventURL, EventDTO.class);
                }
                return events;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(EventDTO[] events) {
            if (events != null) {
                List<EventDTO> ongoing = new ArrayList<>();
                List<EventDTO> past = new ArrayList<>();
                for (int i = 0; i < events.length; i++) {
                    if (events[i].getEndDate() > Calendar.getInstance().getTimeInMillis()) {
                        ongoing.add(events[i]);
                    } else {
                        past.add(events[i]);
                    }
                }
                OngoingEventFragment oef = (OngoingEventFragment) tabsAdapter.getItem(0);
                oef.updateList(ongoing);
                PastEventFragment pef = (PastEventFragment) tabsAdapter.getItem(1);
                pef.updateList(past);
            }
        }

    }

}

