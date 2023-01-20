package ro.sopa.statistifier.job;

import java.awt.*;
import java.util.Objects;

public class RunningJob {

    private JobDescriptor descriptor;

    private int step;
    private int total;

    public JobDescriptor getDescriptor() {
        return descriptor;
    }

    public RunningJob(JobType type, String reference) {
        this.descriptor = new JobDescriptor(type, reference);
    }

    public RunningJob(JobDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public void setDescriptor(JobDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
