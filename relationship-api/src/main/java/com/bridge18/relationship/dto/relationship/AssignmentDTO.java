package com.bridge18.relationship.dto.relationship;

import com.bridge18.relationship.entities.relationship.AssignmentType;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AssignmentDTO {
    public String assignment;
    public AssignmentType type;
    public String notes;

    public AssignmentDTO() {
    }

    public AssignmentDTO(String assignment, AssignmentType type, String notes) {
        this.assignment = assignment;
        this.type = type;
        this.notes = notes;
    }
}
