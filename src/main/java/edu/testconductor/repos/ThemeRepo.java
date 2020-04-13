package edu.testconductor.repos;

import edu.testconductor.domain.Role;
import edu.testconductor.domain.Theme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepo extends JpaRepository<Theme, Integer> {
    public Theme findByName(String name);
    Iterable<Theme> findAllByOrderByNameAsc();
}