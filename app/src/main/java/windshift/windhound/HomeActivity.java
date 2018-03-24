package windshift.windhound;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import windshift.windhound.adapters.TabsAdapter;
import windshift.windhound.fragments.ChampionshipFragment;
import windshift.windhound.fragments.EventFragment;
import windshift.windhound.fragments.RaceFragment;
import windshift.windhound.objects.Event;
import windshift.windhound.objects.Race;

public class HomeActivity extends AppCompatActivity {

    TabsAdapter tabsAdapter;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static final String EXTRA_UPCOMING_BOOL = "windshift.windhound.UPCOMING_BOOL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Enables the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enables the tab layout
        viewPager = findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Tab layout configuration
        tabLayout.getTabAt(0).setText(R.string.title_championship);
        tabLayout.getTabAt(1).setText(R.string.title_event);
        tabLayout.getTabAt(2).setText(R.string.title_race);
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setOffscreenPageLimit(2);
        tabsAdapter = new TabsAdapter(getSupportFragmentManager());
        tabsAdapter.addFragment(new ChampionshipFragment());
        tabsAdapter.addFragment(new EventFragment());
        tabsAdapter.addFragment(new RaceFragment());
        viewPager.setAdapter(tabsAdapter);
    }

    public void display(Long id, int type) {
        switch (type) {
            case 0: // ongoing championship
            case 1: // past championship
                break;
            case 2: // ongoing event
            case 3: // past event
                EventFragment ef = (EventFragment) tabsAdapter.getItem(1);
                Event currentEvent = ef.getEvent(id);
                // Launch display event activity
                break;
            case 4: // ongoing race
            case 5: // past race
                RaceFragment rf = (RaceFragment) tabsAdapter.getItem(2);
                Race currentRace = rf.getRace(id);
                Intent intent = new Intent(this, RaceActivity.class);
                intent.putExtra("Race", currentRace);
                if (type == 4) {
                    intent.putExtra(EXTRA_UPCOMING_BOOL, "true");
                } else {
                    intent.putExtra(EXTRA_UPCOMING_BOOL, "false");
                }
                startActivity(intent);
                break;
        }
    }

    // Called when the floating add button is pressed in the race tab
    public void addRace(View view) {
        Intent intent = new Intent(this, AddRaceActivity.class);
        startActivity(intent);
    }

}
