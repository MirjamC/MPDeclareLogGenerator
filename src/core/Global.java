package core;

/**
 * Created by Vasiliy on 2017-11-03.
 */
public class Global {
    public static String samePrefix = "Same";

    public static String differentPrefix = "Diff";

    /*
    when true it work faster,
    but then 'same' for numbers will prefer numbers
    from the intervals 'EqualToN' in most cases.
    not recommended for short logs.
     */
    public static boolean singleFirstForSame = false;
}
