package by.epam.multi;

import java.util.Arrays;
import by.epam.first.StringUtils;

public class Utils {
    public boolean isAllPositive(String... str){
        StringUtils utils = new StringUtils();
        boolean result = Arrays.stream(str).allMatch(s -> utils.isPositiveNumber(s));
        return result;
    }
}