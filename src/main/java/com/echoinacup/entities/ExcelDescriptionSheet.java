package com.echoinacup.entities;

import com.echoinacup.excel.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class ExcelDescriptionSheet {

    private String companyName;
    private String corporateName;
    private BigInteger paidupCapital;
    private String shareParValue;
    private BigInteger numberOfShares;
    private String legalStructure;
    private String currency;
    private LocalDate inceptionDate;
    private String sector;
    private String country;
    private Status status;
    private int numberOfEmployees;
    private LocalDate listingDate;
    private String stockExchangeName;
    private String phone;
    private String contactEmail;
    private String website;
    private String companyAddress;
    private Map<String, String> socialMedia = new LinkedHashMap<>();
}
