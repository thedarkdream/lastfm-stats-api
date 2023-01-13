package ro.sopa.statistifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.sopa.statistifier.importer.ListeningHistoryJob;

@RestController
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);

    @Autowired
    private ListeningHistoryJob listeningHistoryJob;

    @PostMapping("/import/{username}")
    public void downloadListeningHistoryOfUser(@PathVariable String username, @RequestParam(required = false) Integer startPage) {
        listeningHistoryJob.downloadAndPersistHistory(username, startPage != null ? startPage : 1);
    }

}
