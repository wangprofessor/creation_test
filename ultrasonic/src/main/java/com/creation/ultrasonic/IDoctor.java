package com.creation.ultrasonic;

public interface IDoctor extends IExamination {
    String getName();
    void setName(String name);
    void finish();
}
