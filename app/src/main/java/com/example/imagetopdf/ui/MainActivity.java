package com.example.imagetopdf.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.imagetopdf.Fragments.AddFileFragment;
import com.example.imagetopdf.Fragments.ListFragments;
import com.example.imagetopdf.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AddFileFragment.RefreshList {
    TabLayout tabLayout;
    ViewPager viewPager;
    AddFileFragment fileFragment;
    ListFragments listFragments;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.view_pager);
        toolbar=findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
         fileFragment=new AddFileFragment(MainActivity.this);
         listFragments=new ListFragments(MainActivity.this);

        adapter.addFragment(fileFragment,"Add File");
        adapter.addFragment(listFragments,"Your Pdfs");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.bottom_nav_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_rate:
                try {

                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + "com.android.chrome")));
                }
                catch (ActivityNotFoundException e)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" +"com.android.chrome")));
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void sendName(String name,String uri) {
        String tag = "android:switcher:" + R.id.view_pager   + ":" + 1;
        ListFragments f=(ListFragments)getSupportFragmentManager().findFragmentByTag(tag);
        f.addReceivedName(name,uri);
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {
        ArrayList<String> fragmentTitleList=new ArrayList<>();
        List<Fragment> fragmentList=new ArrayList<>();
        private Map<Integer, String> mFragmentTags;
        private FragmentManager mFragmentManager;
        public void addFragment(Fragment fragment,String title)
        {
            fragmentTitleList.add(title);
            fragmentList.add(fragment);
        }
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
            mFragmentTags = new HashMap<Integer, String>();
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }


    }
}
