package com.zeeshan.programmingstats.utils;

import com.github.mikephil.charting.utils.ColorTemplate;

public class UtilFunctions {

    public static int[] getColors() {
        int[] colors = new int[100];
        int counter = 0;

        for (int color : ColorTemplate.JOYFUL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }

        for (int color : ColorTemplate.MATERIAL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }

        for (int color : ColorTemplate.PASTEL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }

        for (int color : ColorTemplate.LIBERTY_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }
        for (int color : ColorTemplate.COLORFUL_COLORS
        ) {
            colors[counter] = color;
            counter++;
        }
        return colors;
    }

    public static String createCodeforcesUrl(String problem) {
        String[] problemBroken = problem.split("-");
        String url = "https://codeforces.com/contest/"+problemBroken[0]+"/problem/"+problemBroken[1];
        return url;
    }
}
