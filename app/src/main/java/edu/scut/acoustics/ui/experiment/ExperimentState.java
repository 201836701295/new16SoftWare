package edu.scut.acoustics.ui.experiment;

public class ExperimentState {
    public static final int IDLE = -1;
    public static final int PREPARING = 1;
    public static final int PLAYING = 2;
    public static final int PROCESSING = 3;
    public static final int ERROR = 4;
    public static final int FINISH = 0;
    public int state = -1;
}
