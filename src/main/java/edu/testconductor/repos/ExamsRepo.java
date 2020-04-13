package edu.testconductor.repos;

import edu.testconductor.domain.Exam;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ExamsRepo extends CrudRepository <Exam, Long> {

    Iterable<Exam> findAllByOrderByStartDateTimeDesc();

    Exam findByExamName(String examName);
    @Query("SELECT distinct a.examName FROM Exam a")
    List<String> getDistinctExamName();
    //@Query("SELECT Exam FROM Exam a where a.examName = '1'")
    Iterable<Exam> findAllByExamName(String examName);
    Iterable<Exam> findAllByExamNameOrderByStartDateTimeDesc(String examName);

    //@Query("SELECT Exam FROM Exam a where a.examName = '1' and  a.teacher = '2'")
    Iterable<Exam> findAllByExamNameAndTeacher(String name, String teacher);
    Iterable<Exam> findAllByExamNameAndTeacherOrderByStartDateTimeDesc(String name, String teacher);

    Iterable<Exam> findAllByTeacher(String teacher);
    Iterable<Exam> findAllByTeacherOrderByStartDateTimeDesc(String teacher);

}
