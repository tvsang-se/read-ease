package com.readease.documentservice.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtils {

    private static String filePath = "src/main/resources/tokenGG.txt";
    public static void writeFile(String token, String expiredAt) {
        try {
            FileWriter writerObj = new FileWriter(filePath, false);
            writerObj.write(token+"\n");
            writerObj.write(expiredAt);
            writerObj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readFile() {
        ArrayList<String> result = new ArrayList<String>();
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                result.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return result;
    }
}
