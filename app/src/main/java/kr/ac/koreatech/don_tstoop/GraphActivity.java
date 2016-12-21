package kr.ac.koreatech.don_tstoop;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

public class GraphActivity extends AppCompatActivity {

    private static final String TAG = "Graph";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] mDayResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        // Adding Toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initializing the TabLayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("일간"));
        tabLayout.addTab(tabLayout.newTab().setText("주간"));
        tabLayout.addTab(tabLayout.newTab().setText("월간"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Initializing ViewPager
        viewPager = (ViewPager) findViewById(R.id.pager);

        // Creating TabPagerAdapter adapter
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        try {
            Intent intent = getIntent();
            mDayResult = intent.getIntArrayExtra("TODAY_RESULT");
        } catch(Exception e) {
            Toast.makeText(this, "방금 결과를 받아오는데 실패했습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }

        for(int i = 0; i < 24; i++) {
            Log.d(TAG, "" + mDayResult[i]);
        }

        // Set TabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
}