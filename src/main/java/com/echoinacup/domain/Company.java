package com.echoinacup.domain;

import com.echoinacup.service.word.Status;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Company {

    private String corporateName;
    private String paidupCapital;
    private String shareParValue;
    private String numberOfShares;
    private String legalStructure;
    private String currency;
    private String inceptionDate;
    private String sector;
    private String city;
    private String country;
    private Status status;
    private String numberOfEmployees;
    private String listingDate;
    private String stockExchangeName;
    private String linkToExchange;
    private String phone;
    private String contactEmail;
    private String website;
    private String companyAddress;
    private String linkedIn;
    private String twitter;
    private String facebook;
    private String instagram;


    private List<String> subsidiaries = new ArrayList<>(); // 4 each Set od data
    private List<String> activities = new ArrayList<>();
    private List<String> dataSources = new ArrayList<>();

    public void setStatus(String strStatus) {
        if (StringUtils.isNotEmpty(strStatus)) {
            this.status = Status.valueOf(strStatus.toUpperCase());
        }
        this.status = Status.PUBLIC; //By default
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
