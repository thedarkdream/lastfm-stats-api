package ro.sopa.lastfm.job

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("singleton")
class JobRegistry {

    private val runningJobs: ArrayList<RunningJob> = ArrayList()

    fun register(descriptor: JobDescriptor) {

        var job = findRunningJob(descriptor)

        if(job != null) {
            throw RuntimeException("Job already running for $descriptor and is at $job.step/$job.total")
        }

        job = RunningJob(descriptor)

        runningJobs.add(job)

    }

    fun setTotalStep(descriptor: JobDescriptor, total: Int) {
        val runningJob = findRunningJob(descriptor) ?: throw RuntimeException("Invalid job: $descriptor")
        runningJob.total = total
    }

    fun setStep(descriptor: JobDescriptor, step: Int) {
        val runningJob = findRunningJob(descriptor) ?: throw RuntimeException("Invalid job: $descriptor")
        runningJob.step = step
    }

    fun finish(descriptor: JobDescriptor) {
        val runningJob = findRunningJob(descriptor) ?: throw RuntimeException("Invalid job: $descriptor")
        runningJobs.remove(runningJob)
    }

    fun findRunningJob(descriptor: JobDescriptor) : RunningJob? =
        runningJobs.firstOrNull { j -> j.descriptor == descriptor }

}