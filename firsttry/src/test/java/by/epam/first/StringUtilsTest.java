import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import by.epam.first.StringUtils;

class StringUtilsTest {
    private static final StringUtils utils = new StringUtils();

    @Test
    void isPositiveNumber() {
        boolean actual = utils.isPositiveNumber("55");
        Assertions.assertTrue(actual);
    }
}