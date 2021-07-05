package com.epam.esm.util;

import java.util.Arrays;

public enum SortParameter {
    ID,
    NAME,
    DESCRIPTION,
    PRICE,
    DURATION,
    CREATE_DATE,
    LAST_UPDATE_DATE;

    public static boolean contains(String str){
        return Arrays.stream(SortParameter.values()).anyMatch(e->e.name().equalsIgnoreCase(str));
    }
}
