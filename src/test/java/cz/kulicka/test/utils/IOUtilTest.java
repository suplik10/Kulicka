package cz.kulicka.test.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static cz.kulicka.utils.IOUtil.loadListOfStringsToFile;
import static cz.kulicka.utils.IOUtil.saveListOfStringsToFile;

public class IOUtilTest {

    @Test
    public void saveFile() throws IOException {
        ArrayList<String> testArray = new ArrayList<>();
        testArray.add("jedna");
        testArray.add("dva");
        testArray.add("tri");

        Assert.assertTrue(saveListOfStringsToFile(testArray, "src/test/resources/IOTestFile"));
    }


    @Test
    public void loadFile() throws IOException {

        Assert.assertEquals(loadListOfStringsToFile("src/test/resources/IOTestFile").size(), 3);

    }

}
