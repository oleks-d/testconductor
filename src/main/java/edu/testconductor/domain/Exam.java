package edu.testconductor.domain;

import javax.persistence.*;

@Entity
public class Exam {

    @Id
    @GeneratedValue
    private Long id;
    private String examName;
    private String theme;
    private String startDateTime;
    private String endDateTime;
    private int timeForCompletionInMinutes;
    private int numberOfQuestions;
    private String teacher;


//    private int numberOfQuestions = 10;

    @Transient
    public int numberOfSessions;
    @Transient
    public int numberOfFinishedSessions;
    @Transient
    public int result = -2; // for student TODO find normal way of showing results

    public int getNumberOfSessions() {
        return numberOfSessions;
    }
    public int getNumberOfFinishedSessions() {
        return numberOfFinishedSessions;
    }
    public int getResult(){return result;}


    public Exam(String examName, String startDateTime, String endDateTime, int timeForCompletionInMinutes, int numberOfQuestions, String theme, String teacher) {
        this.examName = examName;
        this.theme = theme;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.timeForCompletionInMinutes = timeForCompletionInMinutes;
        this.numberOfQuestions = numberOfQuestions;
        this.teacher = teacher;
    }


    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public int getTimeForCompletionInMinutes() {
        return timeForCompletionInMinutes;
    }

    public void setTimeForCompletionInMinutes(int timeForCompletionInMinutes) {
        this.timeForCompletionInMinutes = timeForCompletionInMinutes;
    }

    public Exam() {
    }



}
