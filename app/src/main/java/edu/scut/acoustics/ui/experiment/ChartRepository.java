package edu.scut.acoustics.ui.experiment;

import edu.scut.acoustics.utils.DSPMath;

public class ChartRepository {
    private DSPMath dspMath = new DSPMath();
    private final float[] audioData1;
    private final float[] audioData2;
    private float[] convolutionData;
    private float[] tailorData;
    private float[] real;
    private float[] imagine;
    private float[] length;
    private float[] phase;

    public ChartRepository(float[] a1, float[] a2){
        audioData1 = a1;
        audioData2 = a2;
    }

    public float[] getConvolutionData() throws Exception {
        if(convolutionData == null){
            convolutionData = new float[audioData1.length + audioData2.length - 1];
            dspMath.conv(audioData1, audioData2, convolutionData);
        }
        return convolutionData;
    }

    public float[] getTailorData() throws Exception {
        if(tailorData == null){
            int index = 0, lmost, rmost;
            float max = 0, temp;
            if(convolutionData == null){
                getConvolutionData();
            }
            for (int i = 0; i < convolutionData.length; i++) {
                temp = Math.abs(convolutionData[i]);
                if(temp > max){
                    max = temp;
                    index = i;
                }
            }
            lmost = (int) (index - 44100f * 0.01f);
            rmost = (int) (index + 44100f * 0.05f);
            if(lmost < 0){
                lmost = 0;
            }
            if(rmost >= convolutionData.length){
                rmost = convolutionData.length - 1;
            }
            tailorData = new float[rmost - lmost + 1];
            System.arraycopy(convolutionData, lmost, tailorData, 0, rmost + 1 - lmost);
        }
        return tailorData;
    }

    public float[] getPhase() {
        return phase;
    }

    public float[] getLength() {
        return length;
    }
}
