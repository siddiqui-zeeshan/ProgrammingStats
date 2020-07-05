package com.zeeshan.programmingstats.userstats;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.zeeshan.programmingstats.R;
import com.zeeshan.programmingstats.utils.ApiCall;
import com.zeeshan.programmingstats.utils.ServerCallback;
import com.zeeshan.programmingstats.utils.UtilFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class UserStatsCreator {

    private Dialog pieChartDialog;
    private Dialog barChartDialog;
    private Dialog listDialog;
    private Button languageUsedButton;
    private Button questionRatingButton;
    private Button verdictButton;
    private Button questionLevelButton;
    private Button questionTagsButton;
    private Button unsolvedButton;
    private PieChart pieChart;
    private BarChart barChart;
    private TextView pieTitleView;
    private TextView barTitleView;
    private TextView triedTextView;
    private TextView solvedTextView;
    private TextView averageAttemptsTextView;
    private TextView maxAttemptsTextView;
    private TextView oneSubmissionTextView;
    private TextView listTitleView;
    private LinearLayout statsLinearLayout;
    private ListView listView;

    public void createUserStats(final Activity activity, final ShimmerFrameLayout mShimmerViewContainer) {

        languageUsedButton = activity.findViewById(R.id.languagesUsedButton);
        questionRatingButton = activity.findViewById(R.id.questionRatingButton);
        questionLevelButton = activity.findViewById(R.id.questionLevelButton);
        verdictButton = activity.findViewById(R.id.verdictButton);
        unsolvedButton = activity.findViewById(R.id.unsolvedButton);
        questionTagsButton = activity.findViewById(R.id.questionTagsButton);
        statsLinearLayout = activity.findViewById(R.id.statsLinearLayout);

        Intent intent = activity.getIntent();
        final String userName = intent.getStringExtra("userName");


        String codeforcesUrl = activity.getResources().getString(R.string.codeforcesApiUrl) + "user.status?handle=" + userName;
        ApiCall.volleyRequestGET(activity, codeforcesUrl, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    final JSONArray jsonArray = new JSONArray(response.getString("result"));
                    final Map<String, Integer> languagesUsedMap = new HashMap<>();
                    final Map<String, Integer> questionLevelMap = new HashMap<>();
                    final Map<String, Integer> questionRatingMap = new HashMap<>();
                    final Map<String, Integer> verdictMap = new HashMap<>();
                    final Map<String, Integer> problemsAttempted = new HashMap<>();
                    final Map<String, Integer> problemsSolved = new HashMap<>();
                    final Set<String> questionTags = new HashSet<>();
                    final Set<String> unsolvedProblems = new HashSet<>();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        int val;

                        String language = object.getString("programmingLanguage");
                        val = languagesUsedMap.containsKey(language) ? languagesUsedMap.get(language) + 1 : 1;
                        languagesUsedMap.put(language, val);

                        String verdict = object.getString("verdict");
                        verdict = verdict.replaceAll("_", " ");
                        if (verdict.equalsIgnoreCase("OK"))
                            verdict = "ACCEPTED";
                        val = verdictMap.containsKey(verdict) ? verdictMap.get(verdict) + 1 : 1;
                        verdictMap.put(verdict, val);

                        try {
                            String problem = object.getJSONObject("problem").getString("contestId")
                                    + "-" + object.getJSONObject("problem").getString("index");
                            val = problemsAttempted.containsKey(problem) ? problemsAttempted.get(problem) + 1 : 1;
                            problemsAttempted.put(problem, val);
                            if (object.getString("verdict").equalsIgnoreCase("OK")) {
                                val = problemsSolved.containsKey(problem) ? problemsSolved.get(problem) + 1 : 1;
                                problemsSolved.put(problem, val);
                            }
                        } catch (JSONException exception){
                            Log.e("Error in getting problem", exception.toString());
                        }


                        if (object.getString("verdict").equalsIgnoreCase("OK")) {
                            String questionLevel;
                            String questionRating;
                            try {

                                questionLevel = object.getJSONObject("problem").getString("index");
                                questionLevel = questionLevel.replaceAll("\\d", "");
                                val = questionLevelMap.containsKey(questionLevel) ? questionLevelMap.get(questionLevel) + 1 : 1;
                                questionLevelMap.put(questionLevel, val);

                                questionRating = object.getJSONObject("problem").getString("rating");
                                val = questionRatingMap.containsKey(questionRating) ? questionRatingMap.get(questionRating) + 1 : 1;
                                questionRatingMap.put(questionRating, val);
                            } catch (JSONException e) {
                                Log.e("Exception", e.toString());
                            }
                        }

                        JSONArray tags = object.getJSONObject("problem").getJSONArray("tags");
                        for(int j = 0; j < tags.length(); j++)
                        {
                            questionTags.add(tags.getString(j));
                        }

                    }

                    TreeMap<String, Integer> sorted = new TreeMap<>();
                    sorted.putAll(questionLevelMap);
                    questionLevelMap.clear();
                    questionLevelMap.putAll(sorted);

                    sorted.clear();
                    sorted.putAll(questionRatingMap);
                    questionRatingMap.clear();
                    questionRatingMap.putAll(sorted);

                    mShimmerViewContainer.stopShimmer();
                    mShimmerViewContainer.setVisibility(View.GONE);
                    statsLinearLayout.setVisibility(View.VISIBLE);

                    populateQuestionStats(activity, problemsAttempted, problemsSolved);

                    for (Map.Entry<String, Integer> entry : problemsAttempted.entrySet()) {
                        if (!problemsSolved.containsKey(entry.getKey())) {
                            unsolvedProblems.add(entry.getKey());
                        }
                    }

                    languageUsedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPieChart(activity, languagesUsedMap, "Languages Used");
                        }
                    });

                    questionRatingButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBarChart(activity, questionRatingMap, "Question Ratings");
                        }
                    });

                    questionLevelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showBarChart(activity, questionLevelMap, "Question Level");
                        }
                    });

                    verdictButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPieChart(activity, verdictMap, "Verdicts");
                        }
                    });

                    questionTagsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            populateTags(activity, questionTags, "Question Tags");
                        }
                    });

                    unsolvedButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            populateTags(activity, unsolvedProblems,"Unsolved Problems");
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void populateTags(final Activity activity, Set<String> stringSet, final String title)
    {
        listDialog = new Dialog(activity, R.style.Dialog);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        listDialog.setContentView(R.layout.list_popup_window);

        listView = listDialog.findViewById(R.id.listView);
        listTitleView = listDialog.findViewById(R.id.listTitleView);
        listTitleView.setText(title);


        ArrayList<String> arrayList = new ArrayList<>();
        Iterator iterator = stringSet.iterator();

        while (iterator.hasNext())
        {
            arrayList.add(iterator.next().toString());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                if(title.equalsIgnoreCase("Unsolved Problems"))
                {
                    String url = UtilFunctions.createCodeforcesUrl(item);
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }

            }
        });

        listDialog.show();
    }

    public void populateQuestionStats(final Activity activity, Map<String, Integer> problemsAttempted, Map<String, Integer> problemsSolved) {
        triedTextView = activity.findViewById(R.id.triedTextView);
        solvedTextView = activity.findViewById(R.id.solvedTextView);
        averageAttemptsTextView = activity.findViewById(R.id.averageAttemptsTextView);
        maxAttemptsTextView = activity.findViewById(R.id.maxAttemptsTextView);
        oneSubmissionTextView = activity.findViewById(R.id.oneSubmissionTextView);

        triedTextView.setText(String.format(Locale.ENGLISH, "%d", problemsAttempted.size()));
        solvedTextView.setText(String.format(Locale.ENGLISH, "%d", problemsSolved.size()));

        double unsolvedAttempts = 0;
        double solvedAttempts = 0;
        int maxAttempts = 0;
        int oneAttempt = 0;
        String questionId = "";
        for (Map.Entry<String, Integer> entry : problemsAttempted.entrySet()) {
            if (problemsSolved.containsKey(entry.getKey())) {
                if (entry.getValue() > maxAttempts) {
                    maxAttempts = entry.getValue();
                    questionId = entry.getKey();
                }
                unsolvedAttempts += entry.getValue();
                solvedAttempts += problemsSolved.get(entry.getKey());
                if (problemsSolved.get(entry.getKey()) == entry.getValue() && entry.getValue() == 1)
                    oneAttempt++;
            }
        }

        averageAttemptsTextView.setText(String.format(Locale.ENGLISH, "%.3f", (unsolvedAttempts / solvedAttempts)));
        maxAttemptsTextView.setText(String.format(Locale.ENGLISH, "%d", maxAttempts) + " (" + questionId + ")");
        oneSubmissionTextView.setText(String.format(Locale.ENGLISH, "%d", oneAttempt));

        final String finalQuestionId = questionId;
        maxAttemptsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = UtilFunctions.createCodeforcesUrl(finalQuestionId);
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });

    }

    public void showPieChart(final Activity activity, Map<String, Integer> map, String title) {
        pieChartDialog = new Dialog(activity, R.style.Dialog);
        pieChartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        pieChartDialog.setContentView(R.layout.pie_chart_popup_window);


        pieChart = pieChartDialog.findViewById(R.id.pieChart);
        pieTitleView = pieChartDialog.findViewById(R.id.pieTitleView);
        pieTitleView.setText(title);

        ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            pieEntryArrayList.add(new PieEntry(entry.getValue(), entry.getKey()));
            //Log.v("legend", entry.getKey());
        }


        PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "");
        pieDataSet.setColors(UtilFunctions.getColors());
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setDrawValues(false);

        PieData pieData = new PieData(pieDataSet);


        pieChart.setData(pieData);
        pieChart.setDrawMarkers(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(45);
        pieChart.animateXY(1000, 1000);
        pieChart.getDescription().setEnabled(false);


        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                PieEntry entry = (PieEntry) e;
                String label = entry.getLabel();
                String val = Integer.toString((int) entry.getValue());
                Toast.makeText(activity
                        , label + " : " + val
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        Legend legend = pieChart.getLegend();
        legend.setWordWrapEnabled(true);
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        pieChart.invalidate();
        pieChartDialog.show();
    }


    public void showBarChart(final Activity activity, Map<String, Integer> map, String title) {
        barChartDialog = new Dialog(activity, R.style.Dialog);
        barChartDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        barChartDialog.setContentView(R.layout.bar_chart_popup_window);
        barChart = barChartDialog.findViewById(R.id.barChart);
        barTitleView = barChartDialog.findViewById(R.id.barTitleView);
        barTitleView.setText(title);

        ArrayList<BarEntry> barEntryArrayList = new ArrayList<>();
        final ArrayList<String> labelNames = new ArrayList<>();

        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            barEntryArrayList.add(new BarEntry(i, entry.getValue()));
            labelNames.add(entry.getKey());
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "");
        barDataSet.setColors(UtilFunctions.getColors());

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(7);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                BarEntry entry = (BarEntry) e;
                String label = labelNames.get(((int) entry.getX()));
                String val = Integer.toString((int) entry.getY());
                Toast.makeText(activity
                        , label + " Rating : " + val + " Problems"
                        , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        Legend legend = barChart.getLegend();
        legend.setEnabled(false);

        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();
        barChartDialog.show();
    }
}
