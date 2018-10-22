package com.echoinacup.service.word;


public enum Status {

    PUBLIC("Public"),

    PRIVATE("Private");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String status() {
        return status;
    }


}
