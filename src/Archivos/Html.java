/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Archivos;

import Prototype.EnergyCheckUtils;
import Prototype.InterfazFusion;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author root
 */
public class Html {
    public static void htmlGeneratorPowerAPI(String appTested, String timestamp, String testDate, String rutaJRAPL) {
        File patgTemplatePowerAPI = new File("/home/roberth/Desktop/test/template-powerAPI.html");
        InterfazFusion interfaz = new InterfazFusion();
        try {
            String endLine = System.getProperty("line.separator");

            StringBuilder builder = new StringBuilder();

            BufferedReader buffer = new BufferedReader(new FileReader(patgTemplatePowerAPI));
            String ln;
            while ((ln = buffer.readLine()) != null) {
                builder.append(ln
                        .replace("$1", "Resultados3_" + appTested + timestamp + ".json")
                ).append(endLine);
            }
            buffer.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/roberth/Desktop/test/template-powerAPI" + appTested + timestamp + ".html"));
            String hrefPower = "template-powerAPI" + appTested + timestamp + ".html";
            interfaz.shPath = "sh /home/roberth/browser2.sh";

            String boo = InterfazFusion.twiceFrameworks;
            if (boo.equals("true")) {
                interfaz.nuevoRegistro(appTested, testDate, hrefPower, rutaJRAPL);
            } else {
                interfaz.nuevoRegistro(appTested, testDate, hrefPower, "");
            }
            bw.write(builder.toString());
            bw.close();
            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            Logger.getLogger(Html.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void htmlGeneratorJRAPL(String path, String path2, Long date, String appTested, String fecha) {
        File file = new File("/home/roberth/Desktop/test/template2.html");
        InterfazFusion interfaz = new InterfazFusion();

        try {
            String ENDL = System.getProperty("line.separator");

            StringBuilder builder = new StringBuilder();

            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = buffer.readLine()) != null) {
                builder.append(ln
                        .replace("$1", path)
                        .replace("$2", path2)
                ).append(ENDL);
            }
            buffer.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/roberth/Desktop/test/template" + appTested + timestamp + ".html"));
            interfaz.srcPower = "/home/roberth/Desktop/test/template.html";
            String hreference = "template" + appTested + timestamp + ".html";
            String boo = InterfazFusion.twiceFrameworks;
            if (boo.equals("true")) {
                rutaJRAPL = hreference;
            } else {
                interfaz.nuevoRegistro(appTested, testDate, "", hreference);
            }

            bw.write(builder.toString());
            bw.close();
            interfaz.shPath = "sh /home/roberth/browser.sh";

            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void htmlGeneratorBoth(String path, String path2, String date) {
        File pathTemplateBoth = new File("/home/roberth/Desktop/test/template3.html");
        InterfazFusion interfaz = new InterfazFusion();

        try {
            String endLine = System.getProperty("line.separator");

            StringBuilder builder = new StringBuilder();

            BufferedReader buffer = new BufferedReader(new FileReader(pathTemplateBoth));
            String ln;
            while ((ln = buffer.readLine()) != null) {
                builder.append(ln
                        .replace("$1", path)
                        .replace("$2", path2)
                ).append(endLine);
            }
            buffer.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/roberth/Desktop/test/template4" + appTested + timestamp + ".html"));
            interfaz.srcPower = "/home/roberth/Desktop/test/template.html";
            String hreference = "template4" + appTested + timestamp + ".html";
            String boo = InterfazFusion.twiceFrameworks;
            interfaz.nuevoRegistro(appTested, testDate, "", hreference);

            bw.write(builder.toString());
            bw.close();
            interfaz.shPath = "sh /home/roberth/browser.sh";

            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        } catch (SQLException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
