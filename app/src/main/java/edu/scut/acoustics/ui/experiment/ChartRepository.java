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

    public void doFinal() throws Exception {
        if(audioData1 == null || audioData2 == null){
            throw new Exception("audio data not initialized");
        }
        //作卷积
        convolutionData = new float[audioData1.length + audioData2.length - 1];
        dspMath.conv(audioData1, audioData2, convolutionData);
        //作裁剪
        tailor();
        //作fft
        real = new float[tailorData.length];
        imagine = new float[tailorData.length];
        dspMath.fft(tailorData,tailorData.length,real,imagine);
        //作求模和相位
        phase = new float[tailorData.length];
        length = new float[tailorData.length];
        dspMath.phaseAndLength(real,imagine,phase,length);
    }

    private void tailor(){
        int index = 0, lmost, rmost;
        float max = 0, temp;
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

    public float[] getConvolutionData(){
        return convolutionData;
    }

    public float[] getTailorData() throws Exception {
        return tailorData;
    }

    public float[] getPhase() {
        return phase;
    }

    public float[] getLength() {
        return length;
    }
}
