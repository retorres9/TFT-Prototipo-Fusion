/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Archivos;

import Clases.Data;
import Prototype.EnergyCheckUtils;
import Prototype.InterfazFusion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import Clases.Datos;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author root
 */
public class Json {
    
    public static void generaJSONJRAPL(ArrayList<Datos> lista, Date appTested, String timestamp) {
        FileWriter rawJsonWriter = null;
        FileWriter sumJsonWriter = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String ruta = "/home/roberth/Desktop/test/Resultados2_" + appTested + timestamp + ".json";
        String ruta2 = "/home/roberth/Desktop/test/Resultados_" + appTested + timestamp + ".json";
        String rutaJRAPL = "Resultados2_" + appTested + timestamp + ".json";
        String rutaHref = "Resultados2_" + appTested + timestamp + ".json";
        String rutaHref2 = "Resultados_" + appTested + timestamp + ".json";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator itr = lista.iterator();
            File jsonRawData = new File(ruta);
            File jsonSumData = new File(ruta2);
            int header = 1;
            rawJsonWriter = new FileWriter(jsonRawData, false);
            sumJsonWriter = new FileWriter(jsonSumData, true);
            if (header == 1) {
                rawJsonWriter.append("[");
                rawJsonWriter.append("\n");
                sumJsonWriter.append("[");
                sumJsonWriter.append("\n");
                header++;
            }

            while (itr.hasNext()) {
                Datos st = (Datos) itr.next();
                valueDRAM = Double.parseDouble(st.data0);
                auxDRAM = auxDRAM + valueDRAM;
                valueCPU = Double.parseDouble(st.data1);
                auxCPU = auxCPU + valueCPU;
                valuePKG = Double.parseDouble(st.data2);
                auxPKG = auxPKG + valuePKG;
                rawJsonWriter.append("[\"" + st.date + "\"");
                rawJsonWriter.append(",");
                sumJsonWriter.append("[\"" + st.date + "\"");
                sumJsonWriter.append(",");
                rawJsonWriter.append(st.data0);
                rawJsonWriter.append(",");
                sumJsonWriter.append(String.valueOf(auxDRAM));
                sumJsonWriter.append(",");
                rawJsonWriter.append(st.data1);
                rawJsonWriter.append(",");
                sumJsonWriter.append(String.valueOf(auxCPU));
                sumJsonWriter.append(",");
                if (!itr.hasNext()) {
                    rawJsonWriter.append(st.data2 + "]");
                    sumJsonWriter.append(String.valueOf(auxPKG) + "]");
                }
                if (itr.hasNext()) {
                    rawJsonWriter.append(st.data2 + "]");
                    sumJsonWriter.append(String.valueOf(auxPKG) + "]");
                    rawJsonWriter.append(",");
                    sumJsonWriter.append(",");
                }

                rawJsonWriter.append("\n");
                sumJsonWriter.append("\n");

            }
            rawJsonWriter.append("]");
            sumJsonWriter.append("]");
            String boo = InterfazFusion.twiceFrameworks;
            if (boo.equals("true")) {
                System.out.println("salta");
            } else {
                Html.htmlGeneratorJRAPL(rutaHref, rutaHref2, timestamp, appTested, fecha);
            }

        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rawJsonWriter.close();
                sumJsonWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }
    
    public static void generaJSONPowerAPI(ArrayList<Datos> lista, String appTested, String timestamp) {
        FileWriter jsonWriter = null;
        String ruta = "/home/roberth/Desktop/test/Resultados3_" + appTested + timestamp + ".json";
        String rutaPower = "Resultados3_" + appTested + timestamp + ".json";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator itr = lista.iterator();
            File jsonPowerAPI = new File(ruta);
            int header = 1;
            jsonWriter = new FileWriter(jsonPowerAPI, false);
            if (header == 1) {
                jsonWriter.append("[");
                jsonWriter.append("\n");
                header++;
            }

            while (itr.hasNext()) {

                Datos st = (Datos) itr.next();
                valueDRAM = Double.parseDouble(st.data0);
                auxDRAM = auxDRAM + valueDRAM;
                valueCPU = Double.parseDouble(st.data1);
                auxCPU = auxCPU + valueCPU;
                valuePKG = Double.parseDouble(st.data2);
                auxPKG = auxPKG + valuePKG;
                jsonWriter.append("[\"" + st.date + "\"");
                jsonWriter.append(",");
                jsonWriter.append(st.data0);
                jsonWriter.append(",");
                jsonWriter.append(st.data1);
                jsonWriter.append(",");
                if (!itr.hasNext()) {
                    jsonWriter.append(st.data2 + "]");
                }
                if (itr.hasNext()) {
                    jsonWriter.append(st.data2 + "]");
                    jsonWriter.append(",");
                }

                jsonWriter.append("\n");

            }
            jsonWriter.append("]");

            String boo = InterfazFusion.twiceFrameworks;
            if (boo.equals("true")) {
                htmlGeneratorBoth(rutaJRAPL, rutaPower, date);
            } else {
                htmlGeneratorPowerAPI(ruta, appTested, date);
            }
            InterfazFusion.twiceFrameworks = "false";

        } catch (IOException ex) {
            Logger.getLogger(Json.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(Json.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }
}
