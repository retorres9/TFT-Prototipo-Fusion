
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
public class tests implements Runnable {
    

    @Override
    public void run() {
        System.out.println("empezo");
        try {
            Thread.sleep(5000);
            System.out.println("termino");
        } catch (InterruptedException ex) {
            Logger.getLogger(tests.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    
}
