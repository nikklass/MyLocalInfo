package ke.co.heavybit.mylocalinfo.utils;

/**
 * Created by heavybit on 10/17/2016.
 */

public class StringUtils {
    public static String capitalizeString(String description) {
        StringBuilder descriptionSb = new StringBuilder(description);
        descriptionSb.setCharAt(0, Character.toUpperCase(descriptionSb.charAt(0)));
        description = descriptionSb.toString();
        return description;
    }
}