package com.bridge18.relationship.repository;


import com.bridge18.relationship.entities.relationship.AssignmentType;
import lombok.Data;

@Data
public class MongoAssignment {
    private String assignment;
    private AssignmentType type;
    private String notes;

    public MongoAssignment() {
    }

    public MongoAssignment(String assignment, AssignmentType type, String notes) {
        this.assignment = assignment;
        this.type = type;
        this.notes = notes;
    }
}
