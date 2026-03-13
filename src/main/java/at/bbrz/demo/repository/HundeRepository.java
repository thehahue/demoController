package at.bbrz.demo.repository;

import at.bbrz.demo.model.Hund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HundeRepository extends JpaRepository<Hund, Integer> {
}
