package com.bridge18.relationship.dto.relationship;

import org.pcollections.PSequence;

public class PaginatedSequence<T> {
    PSequence<T> values;
    int pageSize;
    int count;

    public PaginatedSequence() {
    }

    public PaginatedSequence(PSequence<T> values, int pageSize, int count) {
        this.values = values;
        this.pageSize = pageSize;
        this.count = count;
    }

    public PSequence<T> getValues() {
        return values;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }
}
