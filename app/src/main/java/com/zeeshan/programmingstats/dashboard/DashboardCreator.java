package com.zeeshan.programmingstats.dashboard;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.ClientError;
import com.android.volley.VolleyError;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;
import com.zeeshan.programmingstats.R;
import com.zeeshan.programmingstats.loginpage.MainActivity;
import com.zeeshan.programmingstats.ratingchange.RatingChangeActivity;
import com.zeeshan.programmingstats.userstats.UserStatsActivity;
import com.zeeshan.programmingstats.utils.ApiCall;
import com.zeeshan.programmingstats.utils.ServerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class DashboardCreator {

    private ImageView profilePicture;
    private TextView name;
    private TextView handle;
    private TextView rank;
    private TextView rating;
    private TextView maxRating;
    private TextView friendOf;
    private TextView contribution;
    private TextView registration;
    private Button ratingChangeButton;
    private Button userStatsButton;
    private LinearLayout primaryLinearLayout;


    public void createDashboard(final Activity activity, final ShimmerFrameLayout mShimmerViewContainer) {
        final Intent intent = activity.getIntent();
        final String userName = intent.getStringExtra("userName");

        profilePicture = activity.findViewById(R.id.userProfileImageView);
        name = activity.findViewById(R.id.userProfileTextView);
        handle = activity.findViewById(R.id.handleTextView);
        rank = activity.findViewById(R.id.rankTextView);
        rating = activity.findViewById(R.id.ratingTextView);
        maxRating = activity.findViewById(R.id.maxRatingTextView);
        friendOf = activity.findViewById(R.id.friendTextView);
        contribution = activity.findViewById(R.id.contributionTextView);
        registration = activity.findViewById(R.id.registrationTextView);
        ratingChangeButton = activity.findViewById(R.id.ratingChangeButton);
        userStatsButton = activity.findViewById(R.id.userStatsButton);
        primaryLinearLayout = activity.findViewById(R.id.linearLayoutPrimary);


        String codeforcesUrl = activity.getResources().getString(R.string.codeforcesApiUrl)
                + "user.info?handles=" + userName;
        ApiCall.volleyRequestGET(activity, codeforcesUrl, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getString("status").equalsIgnoreCase("FAILED")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle("Wrong codeforces handle");
                        builder.setMessage("You have entered a handle which doesn't exist. Please try again");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent backIntent = new Intent(activity, MainActivity.class);
                                activity.startActivity(backIntent);
                            }
                        });
                        builder.create().show();
                    }
                    JSONArray ar = new JSONArray(response.getString("result"));
                    JSONObject object = ar.getJSONObject(0);

                    handle.setText(object.getString("handle"));
                    java.util.Date time = new java.util.Date(Long.parseLong(
                            object.getString("registrationTimeSeconds")) * (long) 1000);
                    String date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(time);
                    registration.setText(date);

                    String profilePictureURL = "http:" + object.getString("titlePhoto");
                    Log.v("url", profilePictureURL);

                    Picasso.get().load(profilePictureURL).resize(250, 250)
                            .centerCrop().into(profilePicture);

                    contribution.setText(object.getString("contribution"));
                    rating.setText(object.getString("rating"));
                    maxRating.setText(object.getString("maxRating"));
                    friendOf.setText(object.getString("friendOfCount"));

                    rank.setText(object.getString("rank"));
                    String fullName = object.getString("firstName") + " "
                            + object.getString("lastName");
                    name.setText(fullName);



                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    primaryLinearLayout.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    primaryLinearLayout.setVisibility(View.VISIBLE);
                    Log.e("Error",e.toString());
                }

            }
        });


        ratingChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, RatingChangeActivity.class);
                intent.putExtra("userName", userName);
                activity.startActivity(intent);
            }
        });

        userStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UserStatsActivity.class);
                intent.putExtra("userName", userName);
                activity.startActivity(intent);
            }
        });
    }
}
