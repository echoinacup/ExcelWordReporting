package com.echoinacup.excel;


public enum Status {

    PUBLIC {
        @Override
        public String asNormalTitle() {
            return "Public";
        }
    },
    PRIVATE {
        @Override
        public String asNormalTitle() {
            return "Private";
        }
    };

    public abstract String asNormalTitle();

}
