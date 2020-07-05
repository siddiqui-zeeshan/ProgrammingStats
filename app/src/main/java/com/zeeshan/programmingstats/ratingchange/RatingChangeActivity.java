package com.zeeshan.programmingstats.ratingchange;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.zeeshan.programmingstats.R;

public class RatingChangeActivity extends AppCompatActivity {

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_change);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmer();

        RatingChangeCreator ratingChangeCreator = new RatingChangeCreator();
        ratingChangeCreator.createRatingChangePage(this, mShimmerViewContainer);
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