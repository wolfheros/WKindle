package com.wolfheros.wkindle.Utilities;

import java.io.File;

public class SystemOperation {
    public static String LOCAL = "WKindle";
    /**
     * create file direction.
     * */
    public static String FILE_MKDIR_SCRIPT = "mkdir ";
    /**
     * Home file location.
     * */
    public static String USER_HOME = System.getProperty("user.home");
    /**
     * String object WKindle location
     * */
    public static String STRING_WKindle = USER_HOME +File.separator+ LOCAL;
    /**
     * get WKindle FIle object.
     * */
    public static File FILE_OBJECT_WKindle = new File(STRING_WKindle);
   /**
    * mkdir file
    * */
   public static String mkdirFile(){
       return FILE_MKDIR_SCRIPT+ FILE_OBJECT_WKindle;
   }

}
