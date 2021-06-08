package by.epam.first;

import org.apache.commons.lang3.math.NumberUtils;

public class StringUtils {
    public static void main(String[] args) {
        System.out.println(new StringUtils().isPositiveNumber("55"));
    }

    public boolean isPositiveNumber(String str) {
        boolean result = false;
        if (NumberUtils.isNumber(str)) {
            int number = NumberUtils.createInteger(str);
            if (number > 0) {
                result = true;
            }
        }
        return result;
    }
}