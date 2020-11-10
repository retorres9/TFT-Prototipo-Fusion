/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Archivos;

import Clases.Datos;
import Prototype.EnergyCheckUtils;
import Prototype.InterfazFusion;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author root
 */
public class Csv {

    public static void generaLiveCSVPowerAPI(ArrayList<Datos> lista, int header) {
        FileWriter csvWriter = null;
        String ruta = "/home/roberth/Desktop/test/Resultados_live2.csv";
        double cpuValue;

        try {
            Iterator iterator = lista.iterator();
            File csvLivePowerAPI = new File(ruta);
            csvWriter = new FileWriter(csvLivePowerAPI, true);

            if (header == 1) {
                csvWriter.append("time");
                csvWriter.append(",");
                csvWriter.append("cpu");
                csvWriter.append("\n");
                header++;
            }

            while (iterator.hasNext()) {
                Datos st = (Datos) iterator.next();
                csvWriter.append(st.date);
                csvWriter.append(",");
                cpuValue = Double.parseDouble(st.data1);
                csvWriter.append(String.valueOf(cpuValue));
                csvWriter.append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                csvWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void generaLiveCsvJRAPL(ArrayList<Datos> lista, Date date, String path, int header) {
        FileWriter csvWriter = null;
        String csvLiveJrapl = "/home/roberth/Desktop/test/Resultados_live.csv";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator iterator = lista.iterator();
            File csvFile = new File(csvLiveJrapl);
            csvWriter = new FileWriter(csvFile, true);

            while (iterator.hasNext()) {
                Datos st = (Datos) iterator.next();
                valueDRAM = Double.parseDouble(st.data0);
                auxDRAM = auxDRAM + valueDRAM;
                valueCPU = Double.parseDouble(st.data1);
                auxCPU = auxCPU + valueCPU;
                valuePKG = Double.parseDouble(st.data2);
                auxPKG = auxPKG + valuePKG;
                csvWriter.append(st.date);
                csvWriter.append(",");
                csvWriter.append(st.data0);
                csvWriter.append(",");
                csvWriter.append(st.data1);
                csvWriter.append(",");
                csvWriter.append(st.data2);
                csvWriter.append("\n");

            }
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                csvWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void clean(String appTested, String testDate) throws IOException {
        String csvFile = "/home/roberth/Desktop/test/Data2.csv";
        BufferedReader buffer;
        String outputLineCsv;
        String cvsSplit = ";";
        buffer = new BufferedReader(new FileReader(csvFile));
        File csvCleaned = new File("/home/roberth/Desktop/Datos/Resultados_" + appTested + "_" + testDate + ".csv");
        FileWriter csvWriter;
        csvWriter = new FileWriter(csvCleaned, false);

        try {

            while ((outputLineCsv = buffer.readLine()) != null) {
                String[] dataFromCsv = outputLineCsv.split(cvsSplit);
                String timestampCsv = dataFromCsv[1];
                String powerConsumption = dataFromCsv[4];
                if (timestampCsv.length() > 0) {
                    String[] timestampPart = timestampCsv.split("=");
                    String[] powerSplit = powerConsumption.split(" ");
                    String time = timestampPart[1];
                    double intTime = Double.parseDouble(time);
                    intTime = 25569 + intTime / 86400000;
                    String power = powerSplit[0];
                    String[] powerSplit2 = power.split("=");
                    String jpower = powerSplit2[1];
                    double dblWatts = Double.parseDouble(jpower) / 1000000;
                    jpower = String.valueOf(dblWatts);
                    csvWriter.append(String.valueOf(intTime));
                    csvWriter.append(";");
                    csvWriter.append(String.valueOf(jpower));
                    csvWriter.append("\n");

                }

            }
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appTested + "\n"
                    + "Se ha generado el archivo csv" + "\nHa comenzado la limpieza de datos"
                    + "\nHa finalizado la limpieza de datos");
            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo para realizar la limpieza de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            Logger.getLogger(Csv.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                    csvWriter.close();
                } catch (IOException e) {
                    Logger.getLogger(Csv.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

}
