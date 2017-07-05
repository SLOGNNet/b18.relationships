package com.bridge18.relationship.dto.relationship;

import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode
public class RelationshipDTO {
    public String id;
    public String provider;
    public String customer;
    public Date startDate;
    public Date terminationDate;
    public String notes;
    public List<AssignmentDTO> assignments;

    public RelationshipDTO() {
    }

    public RelationshipDTO(String id, String provider, String customer, Date startDate, Date terminationDate, String notes, List<AssignmentDTO> assignments) {
        this.id = id;
        this.provider = provider;
        this.customer = customer;
        this.startDate = startDate;
        this.terminationDate = terminationDate;
        this.notes = notes;
        this.assignments = assignments;
    }
}
