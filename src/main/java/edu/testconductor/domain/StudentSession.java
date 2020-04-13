package edu.testconductor.domain;

import javax.persistence.*;

@Entity
public class StudentSession {

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    private Long id;
    String name;
    String email;
    boolean isActive;
    String time;
    String endTime;
    @OneToOne
    Exam exam;
    String code;
    String groupName;
    int result;
    @Lob
    @Column(length = 4000)
    String resultString;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }



    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }



    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }



    public StudentSession() {
    }

    public String getName() {
        return name;
    }

    public StudentSession(String name, String email, String groupName, String time, Exam exam) {
        this.name = name;
        this.email = email;
        this.groupName = groupName;
        this.time = time;
        this.exam = exam;
        this.isActive = false;
        this.result = -1;
        this.resultString = "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
