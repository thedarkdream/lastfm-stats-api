package ro.sopa.lastfm.controller

import org.springframework.web.bind.annotation.*
import ro.sopa.lastfm.importer.ListeningHistoryJob
import ro.sopa.lastfm.job.JobDescriptor
import ro.sopa.lastfm.job.JobRegistry
import ro.sopa.lastfm.job.JobType

@RestController
class DataImportController(
    val listeningHistoryJob: ListeningHistoryJob,
    val jobRegistry: JobRegistry
) {

    @PostMapping("/import/{username}")
    fun downloadListeningHistoryOfUser(@PathVariable username: String, @RequestParam(required = false) startPage: Int?) {
        val jobDescriptor = JobDescriptor(JobType.IMPORT_HISTORY, username)
        jobRegistry.register(jobDescriptor)

        try {
            listeningHistoryJob.downloadAndPersistHistory(username, startPage ?: 1, jobDescriptor)
        } finally {
            jobRegistry.finish(jobDescriptor)
        }
    }

}