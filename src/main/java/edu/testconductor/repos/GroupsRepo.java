package edu.testconductor.repos;

import edu.testconductor.domain.StudentGroup;
import org.springframework.data.repository.CrudRepository;

public interface GroupsRepo extends CrudRepository <StudentGroup, Long> {
    StudentGroup findByGroupName(String groupName);
    Iterable<StudentGroup> findAllByOrderByGroupNameAsc();
    StudentGroup getOne(Long id);

    StudentGroup findFirstByOrderById();
}
