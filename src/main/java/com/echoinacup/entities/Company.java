package com.echoinacup.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Company {

    private String companyName;
    private List<List<String>> subsidiaries = new ArrayList<>(); // 4 each Set od data
    private List<List<String>> activities = new ArrayList<>();
    private List<String> dataSources = new ArrayList<>();


    public Company() {
    }

    public Company(String companyName) {
        this.companyName = companyName;
    }
}
