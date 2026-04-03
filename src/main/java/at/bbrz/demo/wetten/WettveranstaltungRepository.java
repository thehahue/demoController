package at.bbrz.demo.wetten;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WettveranstaltungRepository extends JpaRepository<Wettveranstaltung, Integer> {
    List<Wettveranstaltung> findAllByOrderByDatumAscUhrzeitAscIdAsc();
}
