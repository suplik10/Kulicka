package cz.kulicka.utils;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

@Deprecated
public class IOUtil {

    static Logger log = Logger.getLogger(IOUtil.class);

    public static boolean saveListOfStringsToFile(ArrayList<String> listToSave, String filePath) {
        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("File not found exception: " + e.getStackTrace());
            return false;
        }

        try {
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(listToSave);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            log.error("IO exception: " + e.getStackTrace());
            return false;
        }
        return true;
    }

    public static ArrayList<String> loadListOfStringsToFile(String filePath) {
        FileInputStream fileInputStream = null;
        ObjectInputStream objectInputStream = null;
        ArrayList<String> result = null;

        try {
            fileInputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            log.error("File not found exception: " + e.getStackTrace());
            return result;
        }

        try {
            objectInputStream = new ObjectInputStream(fileInputStream);
            result = (ArrayList<String>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            log.error("IO exception: " + e.getStackTrace());
            return result;
        } catch (ClassNotFoundException e) {
            log.error("Class not found exception: " + e.getStackTrace());
            return result;
        }

        return result;
    }
}
