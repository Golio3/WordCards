package com.example.golio.wordcards;

import java.io.Serializable;

/**
 * Created by Golio on 03.09.2017.
 */
public class Word implements Serializable {

    private int id;
    private String lex1;
    private String lex2;
    private String lex3;
    private int language;
    private Integer weight;

    public Word()  {

    }

    public Word(String lex1, String lex2, String lex3, Integer language, Integer weight) {
        this.lex1= lex1;
        this.lex2= lex2;
        this.lex3= lex3;
        this.language= language;
        this.weight= weight;
    }

    public Word(Integer id, String lex1, String lex2, String lex3, Integer language, Integer weight) {
        this.id = id;
        this.lex1= lex1;
        this.lex2= lex2;
        this.lex3= lex3;
        this.language= language;
        this.weight= weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLex1() {
        return lex1;
    }

    public void setLex1(String lex1) {
        this.lex1 = lex1;
    }

    public String getLex2() {
        return lex2;
    }

    public void setLex2(String lex2) {
        this.lex2 = lex2;
    }

    public String getLex3() {
        return lex3;
    }

    public void setLex3(String lex3) {
        this.lex3 = lex3;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}
