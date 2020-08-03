package com.example.imagetopdf.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.ViewGroup;

import com.example.imagetopdf.Fragments.AddFileFragment;
import com.example.imagetopdf.Fragments.ListFragments;
import com.example.imagetopdf.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    AddFileFragment fileFragment;
    ListFragments listFragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
         fileFragment=new AddFileFragment(MainActivity.this);
         listFragments=new ListFragments(MainActivity.this);
        adapter.addFragment(fileFragment,"Add File");
        adapter.addFragment(listFragments,"Your Pdfs");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
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
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object object= super.instantiateItem(container, position);
            if (object instanceof Fragment) {
                Fragment fragment = (Fragment) object;
                String tag = fragment.getTag();
                mFragmentTags.put(position, tag);
            }
            return object;
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
