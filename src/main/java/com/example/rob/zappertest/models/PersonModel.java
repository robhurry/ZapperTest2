package com.example.rob.zappertest.models;

/**
 * Created by Rob on 2015-06-23.
 * The person model represents a person from the JSON given
 */
public class PersonModel {
    public String id;
    public String name;

    public PersonModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}