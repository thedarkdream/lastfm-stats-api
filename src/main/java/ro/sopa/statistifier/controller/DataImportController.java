package ro.sopa.statistifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.sopa.statistifier.importer.ListeningHistoryJob;
import ro.sopa.statistifier.job.JobDescriptor;
import ro.sopa.statistifier.job.JobRegistry;
import ro.sopa.statistifier.job.JobType;

@RestController
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);

    @Autowired
    private ListeningHistoryJob listeningHistoryJob;

    @Autowired
    JobRegistry jobRegistry;

    @PostMapping("/import/{username}")
    public void downloadListeningHistoryOfUser(@PathVariable String username, @RequestParam(required = false) Integer startPage) {

        JobDescriptor jobDescriptor = new JobDescriptor(JobType.IMPORT_HISTORY, username);
        jobRegistry.register(jobDescriptor);

        try {
            listeningHistoryJob.downloadAndPersistHistory(username, startPage != null ? startPage : 1, jobDescriptor);
        } finally {
            jobRegistry.finish(jobDescriptor);
        }
    }

}
