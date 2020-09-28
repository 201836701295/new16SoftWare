package edu.scut.acoustics.ui.experiment;

public class ExperimentState {
    private Integer success;
    private Integer play;
    private Integer process;
    private Integer error;

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        play = null;
        process = null;
        error = null;
        this.success = null;
        this.success = success;
    }

    public Integer getPlay() {
        return play;
    }

    public void setPlay(Integer play) {
        this.play = play;
    }

    public Integer getProcess() {
        return process;
    }

    public void setProcess(Integer process) {
        this.process = process;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }
}
