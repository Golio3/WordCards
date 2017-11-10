package com.example.golio.wordcards;

import java.io.Serializable;

/**
 * Created by Golio on 03.09.2017.
 */
public class Options implements Serializable {

    private int id;
    private String type;
    private Integer value;

    public Options()  {

    }

    public Options(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public Options(int id, String type, int value) {
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
