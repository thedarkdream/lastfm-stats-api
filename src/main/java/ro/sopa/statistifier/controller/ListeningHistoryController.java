package ro.sopa.statistifier.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ro.sopa.statistifier.importer.ListeningHistoryJob;

@RestController
public class ListeningHistoryController {

    private static final Logger logger = LoggerFactory.getLogger(ListeningHistoryController.class);

    @Autowired
    private ListeningHistoryJob listeningHistoryJob;

    @PostMapping("/download/{username}")
    public void downloadListeningHistoryOfUser(@PathVariable String username, @RequestParam Integer startPage) {

        listeningHistoryJob.downloadAndPersistHistory(username, startPage != null ? startPage : 1);

    }

    @PostMapping("/test")
    public void test() {

        logger.info("Debugged");

    }
}
