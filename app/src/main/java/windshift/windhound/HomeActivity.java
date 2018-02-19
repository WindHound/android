package windshift.windhound;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabsPagerAdapter tabsPagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static final String EXTRA_EVENT_ID = "windshift.windhound.EVENT_ID";

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
    public void displayEvent(View view, int event) {
        Intent intent = new Intent(this, DisplayActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, String.valueOf(event + 1));
        startActivity(intent);
    }
}
