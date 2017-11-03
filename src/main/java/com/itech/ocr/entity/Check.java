package com.itech.ocr.entity;

public class Check {
    private String id;
    private String value;
    private String line;

    public Check(String id, String value, String line) {
        this.id = id;
        this.value = value;
        this.line = line;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
