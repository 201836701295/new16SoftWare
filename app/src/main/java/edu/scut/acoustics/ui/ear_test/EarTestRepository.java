package edu.scut.acoustics.ui.ear_test;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Arrays;

import edu.scut.acoustics.R;

public class EarTestRepository {
    static EarTestRepository repository;

    public final int TEST_FINISH;
    MutableLiveData<int[]> leftSensitivitiesLiveData;
    MutableLiveData<int[]> rightSensitivitiesLiveData;
    MutableLiveData<Tested> testedLiveData;
    int[] frequencies;
    int[] leftSensitivities;
    int[] rightSensitivities;
    Tested tested;

    private EarTestRepository(Context context) {
        frequencies = context.getResources().getIntArray(R.array.frequency);
        leftSensitivities = new int[frequencies.length];
        rightSensitivities = new int[frequencies.length];
        Arrays.fill(leftSensitivities, context.getResources().getInteger(R.integer.minear));
        Arrays.fill(rightSensitivities, context.getResources().getInteger(R.integer.minear));
        TEST_FINISH = (1 << frequencies.length) - 1;

        leftSensitivitiesLiveData = new MutableLiveData<>(leftSensitivities);
        rightSensitivitiesLiveData = new MutableLiveData<>(rightSensitivities);
        tested = new Tested();
        testedLiveData = new MutableLiveData<>(tested);
    }

    public static EarTestRepository getRepository(Context context) {
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

    public void testLeft(int i) {
        if (tested.left == TEST_FINISH + 1) {
            tested.left = TEST_FINISH;
            testedLiveData.setValue(tested);
            return;
        }
        tested.left |= 1 << i;
        testedLiveData.setValue(tested);
    }

    public void resetTested() {
        tested.left = 0;
        tested.right = 0;
        testedLiveData.setValue(tested);
        Arrays.fill(leftSensitivities, 0);
        Arrays.fill(rightSensitivities, 0);
        leftSensitivitiesLiveData.setValue(leftSensitivities);
        rightSensitivitiesLiveData.setValue(rightSensitivities);
    }

    public void setLeftFinished() {
        if (tested.left == TEST_FINISH) {
            tested.left = TEST_FINISH + 1;
            testedLiveData.setValue(tested);
        }
    }

    public void setRightFinished() {
        if (tested.right == TEST_FINISH) {
            tested.right = TEST_FINISH + 1;
            testedLiveData.setValue(tested);
        }
    }

    public void testRight(int i) {
        if (tested.right == TEST_FINISH + 1) {
            tested.right = TEST_FINISH;
            testedLiveData.setValue(tested);
            return;
        }
        tested.right |= 1 << i;
        testedLiveData.setValue(tested);
    }

    public void updateSensitivitiesLiveData() {
        leftSensitivitiesLiveData.setValue(leftSensitivities);
        rightSensitivitiesLiveData.setValue(rightSensitivities);
    }

    public LiveData<int[]> getLeftSensitivitiesLiveData() {
        return leftSensitivitiesLiveData;
    }

    public LiveData<int[]> getRightSensitivitiesLiveData() {
        return rightSensitivitiesLiveData;
    }

    public LiveData<Tested> getTestedLiveData() {
        return testedLiveData;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        repository = null;
    }

    public void resetLeftFinished() {
        tested.left = 0;
        testedLiveData.setValue(tested);
    }

    public void resetRightFinished() {
        tested.right = 0;
        testedLiveData.setValue(tested);
    }

    public static class Tested {
        public int left = 0;
        public int right = 0;
    }
}
