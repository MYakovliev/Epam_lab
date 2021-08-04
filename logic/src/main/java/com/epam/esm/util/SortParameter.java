package com.epam.esm.util;

import java.util.Arrays;

public enum SortParameter {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    PRICE("price"),
    DURATION("duration"),
    CREATE_DATE("createDate"),
    LAST_UPDATE_DATE("lastUpdateDate");

    SortParameter(String field){
        this.field = field;
    }

    private String field;

    public String getField() {
        return field;
    }

    public static boolean contains(String str){
        return Arrays.stream(SortParameter.values()).anyMatch(e->e.name().equalsIgnoreCase(str));
    }
}
