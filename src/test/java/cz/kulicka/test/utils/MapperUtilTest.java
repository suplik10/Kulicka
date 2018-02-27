package cz.kulicka.test.utils;

import cz.kulicka.utils.MapperUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static cz.kulicka.utils.IOUtil.saveListOfStringsToFile;

public class MapperUtilTest {

    @Test
    public void arrayListToJsonAndReverseTest() throws IOException {

        ArrayList<Float> floatArrayList = new ArrayList<>();
        floatArrayList.add(1.454541654654f);
        floatArrayList.add(2.55555555f);
        floatArrayList.add(89.656565656f);
        floatArrayList.add(150.454654f);

        String json = MapperUtil.listOfFloatToJson(floatArrayList);

        Assert.assertNotNull(json);

        ArrayList<Float> loadedList = MapperUtil.jsonToListOfFloat(json);

        Assert.assertNotNull(loadedList);
        Assert.assertEquals(loadedList.size(), 4);
    }
}
