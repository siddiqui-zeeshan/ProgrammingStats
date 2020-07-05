package com.zeeshan.programmingstats.ratingchange;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.zeeshan.programmingstats.R;
import com.zeeshan.programmingstats.utils.ApiCall;
import com.zeeshan.programmingstats.utils.ServerCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class RatingChangeCreator {

    private LineChart lineChart;
    private TextView contestsTextView;
    private TextView bestRankTextView;
    private TextView worstRankTextView;
    private TextView maxUpTextView;
    private TextView maxDownTextView;
    private LinearLayout contestStatsLinearLayout;

    public void createRatingChangePage(final Activity activity, final ShimmerFrameLayout mShimmerViewContainer) {
        Intent intent = activity.getIntent();
        final String userName = intent.getStringExtra("userName");

        contestStatsLinearLayout = activity.findViewById(R.id.contestStatsLinearLayout);

        Log.v("username", userName);
        lineChart = activity.findViewById(R.id.ratingChangeLineChart);
        String codeforcesUrl = activity.getResources().getString(R.string.codeforcesApiUrl)
                + "user.rating?handle=" + userName;

        final ArrayList<Entry> arrayList = new ArrayList<>();


        ApiCall.volleyRequestGET(activity, codeforcesUrl, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    int contests = 0;
                    int bestRank = 1000000;
                    int worstRank = 0;
                    int maxUp = 0;
                    int maxDown = 0;

                    JSONArray jsonArray = new JSONArray(response.getString("result"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        long rating = Long.parseLong(object.getString("newRating"));
                        long time = Long.parseLong(object.getString("ratingUpdateTimeSeconds"));
                        arrayList.add(new Entry(time, rating));

                        bestRank = Math.min(bestRank, Integer.parseInt(object.getString("rank")));
                        worstRank = Math.max(worstRank, Integer.parseInt(object.getString("rank")));
                        maxUp = Math.max(maxUp, Integer.parseInt(object.getString("newRating"))
                                - Integer.parseInt(object.getString("oldRating")));
                        maxDown = Math.min(maxDown, Integer.parseInt(object.getString("newRating"))
                                - Integer.parseInt(object.getString("oldRating")));
                    }

                    populateContestStats(activity, jsonArray.length(), bestRank, worstRank, maxUp, maxDown);
                    LineDataSet lineDataSet = new LineDataSet(arrayList, "Data Set");
                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                    dataSets.add(lineDataSet);

                    lineDataSet.setLineWidth(3);
                    lineDataSet.setCircleRadius(3);
                    lineDataSet.setCircleColor(Color.RED);

                    Description description = new Description();
                    description.setText("");
                    description.setTextColor(Color.BLACK);
                    description.setTextSize(15);

                    XAxis xAxis = lineChart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getAxisLabel(float value, AxisBase axis) {
                            java.util.Date time = new java.util.Date((long) value * 1000);
                            String date = new SimpleDateFormat("MMM yyyy").format(time);
                            return date;
                        }
                    });


                    LineData data = new LineData(dataSets);
                    lineChart.setData(data);
                    lineChart.setDescription(description);
                    lineChart.setNoDataText("No data available");
                    lineChart.setNoDataTextColor(Color.RED);
                    lineChart.setDrawBorders(true);
                    lineChart.invalidate();

                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    contestStatsLinearLayout.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void populateContestStats(Activity activity, int contests, int bestRank, int worstRank, int maxUp, int maxDown)
    {
        contestsTextView = activity.findViewById(R.id.contestsTextView);
        bestRankTextView = activity.findViewById(R.id.bestRankTextView);
        worstRankTextView = activity.findViewById(R.id.worstRankTextView);
        maxUpTextView = activity.findViewById(R.id.maxUpTextView);
        maxDownTextView = activity.findViewById(R.id.maxDownTextView);

        contestsTextView.setText(String.format(Locale.ENGLISH, "%d", contests));
        bestRankTextView.setText(String.format(Locale.ENGLISH, "%d", bestRank));
        worstRankTextView.setText(String.format(Locale.ENGLISH, "%d", worstRank));
        maxUpTextView.setText(String.format(Locale.ENGLISH, "%d", maxUp));
        maxDownTextView.setText(String.format(Locale.ENGLISH, "%d", Math.abs(maxDown)));

    }


}
