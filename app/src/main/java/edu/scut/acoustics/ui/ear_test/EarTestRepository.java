package edu.scut.acoustics.ui.ear_test;

import android.content.Context;

import java.util.Arrays;

import edu.scut.acoustics.R;

public class EarTestRepository {
    static EarTestRepository repository;
    final int TEST_FINISH;
    int[] frequencies;
    int[] leftSensitivities;
    int[] rightSensitivities;
    int leftTested = 0;
    int rightTested = 0;

    private EarTestRepository(Context context) {
        frequencies = context.getResources().getIntArray(R.array.frequency);
        leftSensitivities = new int[frequencies.length];
        rightSensitivities = new int[frequencies.length];
        Arrays.fill(leftSensitivities, 0);
        Arrays.fill(rightSensitivities, 0);
        TEST_FINISH = (1 << frequencies.length) - 1;
    }

    public static EarTestRepository getInstance(Context context) {
        if (repository == null) {
            repository = new EarTestRepository(context);
        }
        return repository;
    }

    public int[] getLeftSensitivities() {
        return leftSensitivities;
    }

    public int[] getRightSensitivities() {
        return rightSensitivities;
    }

    public int[] getFrequencies() {
        return frequencies;
    }

}
