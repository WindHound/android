package windshift.windhound;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch(index) {
            case 0:
                return new UpcomingFragment();
            case 1:
                return new PastFragment();
            case 2:
                return new ProfileFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    //TODO get strings from strings.xml
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Upcoming";
            case 1:
                return "Past";
            case 2:
                return "Profile";
            default:
                return null;
        }
    }
}
