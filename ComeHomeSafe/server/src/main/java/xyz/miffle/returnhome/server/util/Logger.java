package xyz.miffle.returnhome.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dsm on 2017-04-28.
 */
public class Logger {

    public static void log(String contents) {
        SimpleDateFormat formatter = new SimpleDateFormat( "[yyyy.MM.dd HH:mm:ss]", Locale.KOREA );
        Date currentTime = new Date( );
        String dTime = formatter.format ( currentTime );

        System.out.println(dTime + " : " + contents);
    }

    public static void log(String TAG, String contents) {
        SimpleDateFormat formatter = new SimpleDateFormat( "[yyyy.MM.dd HH:mm:ss]", Locale.KOREA );
        Date currentTime = new Date( );
        String dTime = formatter.format ( currentTime );

        System.out.println(dTime + TAG + " : " + contents);
    }
}
