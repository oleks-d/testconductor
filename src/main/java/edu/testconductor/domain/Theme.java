package edu.testconductor.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Theme(String name) {
        this.name = name;
    }

    public Theme() {
    }
}
