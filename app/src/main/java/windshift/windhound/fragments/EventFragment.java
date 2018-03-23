package windshift.windhound.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import windshift.windhound.R;
import windshift.windhound.adapters.TabsAdapter;

public class EventFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;

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

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        TabsAdapter tabsAdapter = new TabsAdapter(getChildFragmentManager());
        tabsAdapter.addFragment(new OngoingEventFragment());
        tabsAdapter.addFragment(new PastEventFragment());
        viewPager.setAdapter(tabsAdapter);
    }

}

