
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author root
 */
public class tests {

    public static void main(String[] args) {
        FileWriter csvWriter = null;
        try {
            List<List<String>> rows = Arrays.asList(
                    Arrays.asList("Jean", "author", "Java"),
                    Arrays.asList("David", "editor", "Python"),
                    Arrays.asList("Scott", "editor", "Node.js")
            );  csvWriter = new FileWriter("/home/laboratorio/Desktop/new.csv");
            csvWriter.append("Name");
            csvWriter.append(",");
            csvWriter.append("Role");
            csvWriter.append(",");
            csvWriter.append("Topic");
            csvWriter.append("\n");
            for (List<String> rowData : rows) {
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }   csvWriter.flush();
            csvWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(tests.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                csvWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(tests.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
