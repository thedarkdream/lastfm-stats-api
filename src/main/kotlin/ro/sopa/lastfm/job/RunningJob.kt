package ro.sopa.lastfm.job

data class RunningJob(val descriptor: JobDescriptor) {
    var step: Int? = null;
    var total: Int? = null;
}