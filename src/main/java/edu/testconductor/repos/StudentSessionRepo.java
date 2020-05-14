package edu.testconductor.repos;

import edu.testconductor.domain.Exam;
import edu.testconductor.domain.StudentSession;
import edu.testconductor.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

public interface StudentSessionRepo extends CrudRepository<StudentSession, Long> {
    ArrayList<StudentSession> findAllByEmail(String email);
    @Query("SELECT distinct a.exam FROM StudentSession a WHERE a.email = '?1'")
    ArrayList<Long> findExamIDsByEmail(String email);
    StudentSession findByCode(String code);

    Iterable<StudentSession> findAllByExamId(Long exam);

    @Query("SELECT count(*) FROM StudentSession a WHERE a.exam = ?1")
    int getCountOfSessionsByExamID(Exam exam);
    @Query("SELECT count(*) FROM StudentSession a WHERE a.exam = ?1 and a.result > -1")
    int getCountOfFinishedSessionsByExamID(Exam exam);

    ArrayList<StudentSession> findAllByGroupName(String groupName);

    @Query("SELECT a FROM StudentSession a WHERE a.exam.theme = ?1")
    Iterable<StudentSession> findAllByExamTheme(String examTheme);

    @Query("SELECT a FROM StudentSession a WHERE a.exam.examName = ?1 and a.exam.theme = ?2")
    Iterable<StudentSession> findAllByGroupNameAndExamTheme(String groupName, String examTheme);

    @Query("SELECT a FROM StudentSession a WHERE a.exam.examName = ?1")
    Iterable<StudentSession> findAllByExamName(String examName);

    @Transactional
    void deleteAllByExamId(Long examID);
}
