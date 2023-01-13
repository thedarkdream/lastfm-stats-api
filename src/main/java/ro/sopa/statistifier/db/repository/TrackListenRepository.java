package ro.sopa.statistifier.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.sopa.statistifier.db.model.TrackListen;

import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Repository
public interface TrackListenRepository extends JpaRepository<TrackListen, Long> {

    @Query("select max(t.date) from TrackListen t where t.username=:username")
    LocalDateTime findLatestListenDate(@Param("username") String username);

    @Query("select t.artist, count(1) from TrackListen t group by t.artist")
    List<Object[]> findTopArtists(String username, int number);
}
