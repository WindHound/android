package windshift.windhound;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import windshift.windhound.race.Race;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabsPagerAdapter tabsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static final String EXTRA_RACE_ID = "windshift.windhound.RACE_ID";
    public static final String EXTRA_UPCOMING_BOOL = "windshift.windhound.UPCOMING_BOOL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Enables the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enables the tab layout with swiping
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(tabsPagerAdapter);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        // Gives each tab an icon
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_championship);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_event);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_race);
    }

    // Called when an event is clicked
    public void displayRace(View view, Race race, boolean upcoming) {
        Intent intent = new Intent(this, RaceActivity.class);
        if (upcoming) {
            intent.putExtra(EXTRA_UPCOMING_BOOL, "true");
            intent.putExtra("Race", race);
        } else {
            intent.putExtra(EXTRA_UPCOMING_BOOL, "false");
            intent.putExtra("Race", race);
        }
        startActivity(intent);
    }

    public void addRace(View view) {
        Intent intent = new Intent(this, AddRaceActivity.class);
        startActivity(intent);
    }

}
