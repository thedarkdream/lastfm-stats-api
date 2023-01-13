package ro.sopa.statistifier.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.sopa.statistifier.db.model.TrackListen;

@org.springframework.stereotype.Repository
public interface TrackListenRepository extends JpaRepository<TrackListen, Long> {

}
