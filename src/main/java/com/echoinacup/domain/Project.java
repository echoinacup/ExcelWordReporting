package com.echoinacup.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Project {

    private String projectName;
    private String developmentConstructionCost;
    private String currency;
    private String ownerCompany;
    private String parentCompany;
    private String projectDeveloper;
    private String projectContractor;
    private String constructionDate;
    private String constructionComprises;
    private String completionDate;
    private String sector;
    private String projectType;
    private String city;
    private String country;
    private String landOwnership;
    private String totalAreaSize;
    private String totalBuiltupArea;
    private String totalRentableArea;
    private String additionalArea;
    private String status;
    private String projectAddress;
    private String projectWebsite;
    private String projectLatitude;
    private String projectLongitude;


    private List<String> projectActivities = new ArrayList<>();
    private List<String> projectPictures = new ArrayList<>();
    private List<String> projectVideos = new ArrayList<>();
    private List<String> dataSources = new ArrayList<>();


    @Override
    public String toString() {
        return "Project{" +
                "projectName='" + projectName + '\'' +
                ", developmentConstructionCost='" + developmentConstructionCost + '\'' +
                ", currency='" + currency + '\'' +
                ", ownerCompany='" + ownerCompany + '\'' +
                ", parentCompany='" + parentCompany + '\'' +
                ", projectDeveloper='" + projectDeveloper + '\'' +
                ", projectContractor='" + projectContractor + '\'' +
                ", constructionDate='" + constructionDate + '\'' +
                ", constructionComprises='" + constructionComprises + '\'' +
                ", completionDate='" + completionDate + '\'' +
                ", sector='" + sector + '\'' +
                ", projectType='" + projectType + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", landOwnership='" + landOwnership + '\'' +
                ", totalAreaSize='" + totalAreaSize + '\'' +
                ", totalBuiltupArea='" + totalBuiltupArea + '\'' +
                ", totalRentableArea='" + totalRentableArea + '\'' +
                ", additionalArea='" + additionalArea + '\'' +
                ", status='" + status + '\'' +
                ", projectAddress='" + projectAddress + '\'' +
                ", projectWebsite='" + projectWebsite + '\'' +
                ", projectLatitude='" + projectLatitude + '\'' +
                ", projectLongitude='" + projectLongitude + '\'' +
                ", projectActivities=" + projectActivities +
                ", projectPictures=" + projectPictures +
                ", projectVideos=" + projectVideos +
                ", dataSources=" + dataSources +
                '}';
    }
}
