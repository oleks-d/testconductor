package edu.testconductor.repos;

import edu.testconductor.domain.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionsRepo extends CrudRepository <Question, Long> {

    List<Question> findAllByTheme(String theme);
    List<Question> findAllByText(String text);
    Question getOne(Long id);

}
