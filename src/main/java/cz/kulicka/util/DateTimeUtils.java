package cz.kulicka.util;

import cz.kulicka.CoreEngine;

import java.util.Date;

public class DateTimeUtils {

 public static Date getCurrentServerDate(){
     return new Date(new Date().getTime() - CoreEngine.DATE_DIFFERENCE_BETWEN_SERVER_AND_CLIENT);
 }


}
