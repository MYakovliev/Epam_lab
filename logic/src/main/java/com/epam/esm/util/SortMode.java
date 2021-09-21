package com.epam.esm.util;

import java.util.Arrays;

public enum SortMode {
    ASC,
    DESC;

    public static boolean contains(String str){
        return Arrays.stream(SortMode.values()).anyMatch(e->e.name().equalsIgnoreCase(str));
    }
}
