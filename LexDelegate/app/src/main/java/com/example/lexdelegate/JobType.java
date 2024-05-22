package com.example.lexdelegate;

import androidx.annotation.NonNull;

public enum JobType {
    TYPE1(1, "Dilekçe"),
    TYPE2(2, "Diğer");

    private final int value;
    private final String description;

    JobType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public static JobType fromInt(int value) {
        for (JobType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }

    public int toInt() {return value; }
}