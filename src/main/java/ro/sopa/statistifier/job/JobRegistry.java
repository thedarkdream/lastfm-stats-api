package ro.sopa.statistifier.job;

import org.springframework.web.context.annotation.ApplicationScope;
import ro.sopa.statistifier.UserImportException;

import java.util.ArrayList;
import java.util.List;

@ApplicationScope
public class JobRegistry {

    private List<RunningJob> runningJobs = new ArrayList<>();

    public RunningJob register(JobDescriptor descriptor) {
        RunningJob rj = findRunningJob(descriptor);

        if(rj != null) {
            throw new RuntimeException("Job already running for " + descriptor.toString() + " and is at " + rj.getStep() + "/" + rj.getTotal());
        }

        rj = new RunningJob(descriptor);

        runningJobs.add(rj);

        return rj;
    }

    public void setTotalStep(JobDescriptor descriptor, int total) {
        RunningJob runningJob = findRunningJob(descriptor);

        if(runningJob == null) {
            throw new RuntimeException("Invalid job: " + descriptor.toString());
        }

        runningJob.setTotal(total);
    }

    public void setStep(JobDescriptor descriptor, int step) {
        RunningJob runningJob = findRunningJob(descriptor);

        if(runningJob == null) {
            throw new RuntimeException("Invalid job: " + descriptor.toString());
        }

        runningJob.setStep(step);
    }

    public void finish(JobDescriptor descriptor) {
        RunningJob runningJob = findRunningJob(descriptor);

        if(runningJob == null) {
            throw new RuntimeException("Invalid job: " + descriptor.toString());
        }

        runningJobs.remove(runningJob);
    }

    public RunningJob findRunningJob(JobDescriptor descriptor) {
        return runningJobs.stream().filter(j -> j.getDescriptor().equals(descriptor)).findFirst().orElse(null);
    }

}
