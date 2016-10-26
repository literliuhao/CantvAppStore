package com.can.appstore.special_detail.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by atang on 2016/10/24.
 */

public class AppUtils {

    public static StringBuilder readFile(String filepath, String charsetName) {
        File file = new File(filepath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader bufferedReader = null;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), charsetName);
            bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (fileContent.toString().equals("")){
                    fileContent.append("\r\n");
                }
                    fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        }finally {

                try {
                    if(bufferedReader!=null) {
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

}
