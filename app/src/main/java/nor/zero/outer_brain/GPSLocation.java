package nor.zero.outer_brain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;

import nor.zero.outer_brain.fragments.GPSLocationFragment;
import nor.zero.outer_brain.fragments.GroceryListFragment;
import nor.zero.outer_brain.fragments.ShopListFragment;
import nor.zero.outer_brain.fragments.ShoppingListFragment;

public class GPSLocation extends AppCompatActivity {

    private ViewPager viewPager;
    private String[] titles;
    private Fragment[] fragments;
    private MyPagerAdapter adapter;
    static int before ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_location);
        viewPager = findViewById(R.id.viewPager);
        titles= new String[]{getString(R.string.fragment_title_shopping_list),
                getString(R.string.fragment_title_gps_location),
                getString(R.string.fragment_title_shop_location),
                getString(R.string.fragment_title_grocery_list)};
        fragments = new Fragment[titles.length];
        fragments[0] = new ShoppingListFragment();
        fragments[1] = new GPSLocationFragment();
        fragments[2] = new ShopListFragment();
        fragments[3] = new GroceryListFragment();
        initViewPager();
    }


    private void initViewPager(){
        adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        before = 0;
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
               // if(position==1)
                //    Log.v("aaa","MAP");
                if(before==0 && position==1){
                    ShoppingListFragment from = (ShoppingListFragment) fragments[0];
                    GPSLocationFragment to = (GPSLocationFragment) fragments[1];
                    from.sendData();
                    //to.getData();
                }
                before = position;
            }
        });
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter{

    //    private String[] titles = {getString(R.string.fragment_title_shopping_list),
     //           getString(R.string.fragment_title_gps_location),
    //            getString(R.string.fragment_title_shop_location),getString(R.string.fragment_title_grocery_list)};

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
