/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author root
 */
public class Data {
    public String getExecutionTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime currentTime = LocalDateTime.now();
        String executionTime = dateFormatter.format(currentTime);
        return executionTime;
    }
    
    public static Long getTimestamp() {
        Date date = new Date();
        Long timestamp = date.getTime();
        return timestamp;
    }
    
    public String getAppName(String path) {
        String [] pathArray = path.split("/");
        int position = pathArray.length - 1;
        String app = pathArray[position];
        return app;
    }
}
