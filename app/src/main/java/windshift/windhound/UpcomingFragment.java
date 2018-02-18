package windshift.windhound;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import windshift.windhound.race.Event;

public class UpcomingFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_upcoming, container, false);
        new HttpRequestTask().execute();
        /*
        String[] values = new String[] {"First Event", "Event 2", "Event 3", "Event 4", "Event 5",
                "Event 6", "Event 7", "Event 8", "Event 9", "Event 10", "Event 11", "Event 12",
                "Event 13", "Event 14", "Event 15", "Event 16", "Event 17", "Event 18", "Event 19",
                "Event 20", "Event 21", "Event 22", "Event 23", "Event 24", "Last Event"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        */
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((HomeActivity)getActivity()).displayEvent(v, position);
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, Event> {

        @Override
        protected Event doInBackground(Void... params) {
            try {
                final String url = "http://192.168.0.51:8080/structure/event/0";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Event event = restTemplate.getForObject(url, Event.class);
                return event;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                String[] values = new String[]{"first event"};
                values[0] = event.getName();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            }
        }
    }

}
