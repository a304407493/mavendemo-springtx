package com.mavendemo.springtx;

import org.apache.log4j.Logger;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Hello world!
 *
 */
public class App
{
	private static Logger logger = Logger.getLogger(App.class);
    public static void main( String[] args )
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmsss");
    	String test = "QFF" + "T" + "Dedicated" + df.format(new Date()) + "|"+genRandom(10, 20) + "|" + Math.random();
    	System.out.println( test );
    	System.out.println( "This is a console message." );
        logger.debug("This is a debug message.");
        logger.info("This is a info message.");
        logger.warn("This is a warn message.");
        logger.error("This is a error message.");
    }
    

    public static String genRandom(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(s);
    }
}
