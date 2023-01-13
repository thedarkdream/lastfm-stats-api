package ro.sopa.statistifier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ro.sopa.statistifier.importer.ListeningHistoryJob;

@RestController
public class ListeningHistoryController {

    @Autowired
    private ListeningHistoryJob listeningHistoryJob;

    @PostMapping("/download/{username}")
    public void downloadListeningHistoryOfUser(@PathVariable String username) {

        listeningHistoryJob.downloadAndPersistHistory(username);

    }
}
