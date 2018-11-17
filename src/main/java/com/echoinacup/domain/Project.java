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
    private String completionDate;
    private String sector;
    private String projectType;
    private String country;
    private String landOwnership;
    private String totalAreaSize;
    private String totalBuiltupArea;
    private String totalRentableArea;
    private String status;
    private String projectAddress;
    private String projectWebsite;

    private List<String> projectUpdates = new ArrayList<>(); //TODO last element could be separated
    private List<String> projectPictures = new ArrayList<>();
    private List<String> projectVideos = new ArrayList<>();


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
                ", completionDate='" + completionDate + '\'' +
                ", sector='" + sector + '\'' +
                ", projectType='" + projectType + '\'' +
                ", country='" + country + '\'' +
                ", landOwnership='" + landOwnership + '\'' +
                ", totalAreaSize='" + totalAreaSize + '\'' +
                ", totalBuiltupArea='" + totalBuiltupArea + '\'' +
                ", totalRentableArea='" + totalRentableArea + '\'' +
                ", status='" + status + '\'' +
                ", projectAddress='" + projectAddress + '\'' +
                ", projectWebsite='" + projectWebsite + '\'' +
                ", projectUpdates=" + projectUpdates +
                ", projectPictures=" + projectPictures +
                ", projectVideos=" + projectVideos +
                '}';
    }
}
