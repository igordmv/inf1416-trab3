package Util;

import java.util.Calendar;
import java.util.Date;

public class Util {
    private static char[] hexArray = "0123456789abcdef".toCharArray();

    public static void printHexadecimal(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        System.out.println(new String(hexChars)+"\n");
    }

    public static String byteToHex(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /* **************************************************************************************************
     **
     **  Date After X minutes
     **
     ****************************************************************************************************/

    public static Date dateAfterXMinutes(int minutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, -minutes);
        cal.add(Calendar.HOUR, 3); //Time Zone
        return cal.getTime();
    }
}
