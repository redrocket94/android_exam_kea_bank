package com.example.exam_project.Modules;

import java.util.Random;

public class NemID {

    private int MAX = 999999;
    private int MIN = 100000;

    public int getRandomValue() {
        return new Random().nextInt((MAX - MIN)) + MIN;
    }


}
