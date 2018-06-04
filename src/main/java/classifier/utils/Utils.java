package classifier.utils;

public class Utils {

    /**
     * Returns date in correct format
     */
    public static String getDate(int day, int month, int year) {
        String dayStr = String.valueOf(day);
        String monthStr = String.valueOf(month);
        String yearStr = String.valueOf(year);

        return dayStr + "." + monthStr + "." + yearStr;
    }

}
