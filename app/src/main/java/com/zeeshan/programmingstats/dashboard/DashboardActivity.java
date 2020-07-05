package com.zeeshan.programmingstats.dashboard;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.zeeshan.programmingstats.R;

public class DashboardActivity extends AppCompatActivity {

    private ShimmerFrameLayout mShimmerViewContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        DashboardCreator dashboardCreator = new DashboardCreator();
        dashboardCreator.createDashboard(this, mShimmerViewContainer);
    }

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
    }

    @Override
    protected void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}


