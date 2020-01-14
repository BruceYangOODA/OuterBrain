package nor.zero.outer_brain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import nor.zero.outer_brain.fragments.GPSLocationFragment;
import nor.zero.outer_brain.fragments.ShoppingListFragment;

public class GPSLocation extends AppCompatActivity {

    private ViewPager viewPager;
    private Fragment[] fragments = new Fragment[2];
 //   private String[] titles = {getBaseContext().getString(R.string.fragment_title_shopping_list),
  //          getBaseContext().getString(R.string.fragment_title_gps_location)};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_location);

        viewPager = findViewById(R.id.viewPager);
        fragments[0] = new ShoppingListFragment();
        fragments[1] = new GPSLocationFragment();
        initViewPager();


    }

    private void initViewPager(){
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

            }
        });
        viewPager.setCurrentItem(0);
    }






    private class MyPagerAdapter extends FragmentStatePagerAdapter{

        private String[] titles = {getString(R.string.fragment_title_shopping_list),
                getString(R.string.fragment_title_gps_location)};

        public MyPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String title ="";
            title = titles[position];

            return title;
        }
    }

}
