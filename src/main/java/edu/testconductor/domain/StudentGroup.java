package edu.testconductor.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
public class StudentGroup {
    @Id
    @GeneratedValue
    private Long id;
    private String groupName;
    private String lang;

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }



    public static ArrayList<String> getSpecialGroupNames(){
        return new ArrayList<String>(Arrays.asList("REWORK"));
    }

    public StudentGroup() {
    }

    public StudentGroup(String groupName, String lang) {
        this.groupName = groupName;
        this.lang = lang;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}
