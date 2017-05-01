package com.bridge18.relationship.repository;


import java.util.Date;
import java.util.List;

import lombok.Data;
import org.mongodb.morphia.annotations.Id;

@Data
public class MongoRelationship {
    @Id
    private String id;

    private String relationshipId;
    private String provider;
    private String customer;
    private Date startDate;
    private Date terminationDate;
    private String notes;
    private List<MongoAssignment> assignments;

    public MongoRelationship() {
    }

    public MongoRelationship(String relationshipId, String provider, String customer, Date startDate, Date terminationDate, String notes, List<MongoAssignment> assignments) {
        this.relationshipId = relationshipId;
        this.provider = provider;
        this.customer = customer;
        this.startDate = startDate;
        this.terminationDate = terminationDate;
        this.notes = notes;
        this.assignments = assignments;
    }
}
