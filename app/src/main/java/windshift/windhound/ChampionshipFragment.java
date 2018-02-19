package windshift.windhound;

//import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
/*
import android.widget.ListView;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import windshift.windhound.race.Event;
*/

public class ChampionshipFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_championship, container, false);
        //new HttpRequestTask().execute();
        String[] values = new String[] {"First Championship", "Championship 2", "Championship 3",
                "Championship 4", "Championship 5", "Championship 6", "Championship 7",
                "Championship 8", "Championship 9", "Championship 10", "Championship 11",
                "Championship 12", "Championship 13", "Championship 14", "Championship 15",
                "Championship 16", "Championship 17", "Championship 18", "Championship 19",
                "Championship 20", "Championship 21", "Championship 22", "Championship 23",
                "Championship 24", "Last Championship"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
        return rootView;
    }
    /*
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
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
            }
        }
    }
    */
}
