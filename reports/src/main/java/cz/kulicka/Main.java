package cz.kulicka;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        List<String> fileNamesList = new ArrayList();
        Map<String, Double> results = new HashMap<>();

        //C:/APPS/reports
        try {
            Files.newDirectoryStream(Paths.get("C:/APPS/reports"), path -> path.toFile().isFile()).forEach(filePath -> fileNamesList.add(filePath.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String csvFile : fileNamesList) {

            String line = "";
            String cvsSplitBy = ";";

            double fileResultSum = 0.0;
            int rowCount = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

                while ((line = br.readLine()) != null) {
                    //skip header csv file
                    if(rowCount == 0){
                        rowCount++;
                        continue;
                    }

                    rowCount++;
                    // use comma as separator
                    String[] percentageProfitBTC = line.split(cvsSplitBy);
                    fileResultSum+=Double.parseDouble(percentageProfitBTC[10].replace(",","."));

                    //System.out.println("percentageProfitBTC [" + percentageProfitBTC[10]  + "]");

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            rowCount--;
            results.put(csvFile, fileResultSum/rowCount);

        }

        for(Map.Entry<String, Double> entry : results.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            // do what you have to do here
            // In your case, another loop.
        }

        System.out.println(results.toString());
    }


}