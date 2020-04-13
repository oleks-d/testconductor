package edu.testconductor.repos;

import edu.testconductor.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    User findByCode(String code);
    @Query("SELECT u FROM User u WHERE u.groupName != null")
    Iterable<User> findAllWithNonEmptyGroup();
    Iterable<User> findAllByGroupName(String groupName);
}
