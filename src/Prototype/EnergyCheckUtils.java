package Prototype;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import Clases.Data;

public class EnergyCheckUtils {

    public native static int scale(int freq);

    public native static int[] freqAvailable();

    public native static double[] GetPackagePowerSpec();

    public native static double[] GetDramPowerSpec();

    public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);

    public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);

    public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);

    public native static void SetDramPowerLimit(int socketId, int level, double costomPower);

    public native static int ProfileInit();

    public native static int GetSocketNum();

    public native static String EnergyStatCheck();

    public native static void ProfileDealloc();

    public native static void SetPowerLimit(int ENABLE);
    public static int wraparoundValue;
    public static int socketNum;
    public static boolean jraplRepeat = true;
    static boolean lock = true;
    public static String pathApp;
    public static String href;
    public static String rutaJRAPL;
    public static String rutaPower;
    public static String appTested;
    public static String testDate;
    public static Long timestamp;
    static boolean lockRun;
    String appRealPID = "";

    Data testInfo = new Data();

    static {
        System.setProperty("java.library.path",
                "/home/roberth/Downloads/jRAPL/jRAPLEnergy/jRAPLEnergy/src"
        );
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
        }

        System.loadLibrary("CPUScaler");
        wraparoundValue = ProfileInit();
        socketNum = GetSocketNum();
    }

    /**
     * @return an array of current energy information. The first entry is:
     * Dram/uncore gpu energy(depends on the cpu architecture. The second entry
     * is: CPU energy The third entry is: Package energy
     */
    public static double[] getEnergyStats() {
        socketNum = GetSocketNum();
        String EnergyInfo = EnergyStatCheck();
        /*One Socket*/
        if (socketNum == 1) {
            double[] stats = new double[3];
            String[] energy = EnergyInfo.split("#");

            stats[0] = Double.parseDouble(energy[0].replaceAll(",", "."));
            stats[1] = Double.parseDouble(energy[1].replaceAll(",", "."));
            stats[2] = Double.parseDouble(energy[2].replaceAll(",", "."));

            return stats;

        } else {
            /*Multiple sockets*/
            String[] perSockEner = EnergyInfo.split("@");
            double[] stats = new double[3 * socketNum];
            int count = 0;

            for (int i = 0; i < perSockEner.length; i++) {
                String[] energy = perSockEner[i].split("#");
                for (int j = 0; j < energy.length; j++) {
                    count = i * 3 + j;	//accumulative count
                    stats[count] = Double.parseDouble(energy[j]);
                }
            }
            return stats;
        }

    }

    public void framework(String path) throws InterruptedException, IOException {
        appTested = testInfo.getAppName(path);
        testDate = testInfo.getExecutionTime();
        String boo = InterfazFusion.twiceFrameworks;
        ArrayList<Datos> datosJraplOffline = new ArrayList<>();
        ArrayList<Datos> datosJraplOnline = new ArrayList<>();
        Datos jraplOffline;
        Datos jraplOnline;

        Date date = new Date();
        date.getTime();
        int flagStarterApp = 1;
        String fecha = "";
        int cont = 0;
        double datosRAM = 0;
        double datosCPU = 0;
        double datosPKG = 0;
        int auxiliarOnline = 0;

        while (jraplRepeat == true) {
            double[] before = EnergyCheckUtils.getEnergyStats();
            try {
                if (flagStarterApp == 1) {
                    start(path);
                    flagStarterApp++;
                }
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                LocalDateTime getTimeNow = LocalDateTime.now();

                fecha = dateFormatter.format(getTimeNow);
            } catch (Exception e) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, e);
            }
            double[] after = EnergyCheckUtils.getEnergyStats();
            jraplOffline = new Datos(
                    String.valueOf(((after[0] - before[0]) / 10.0)),
                    String.valueOf(((after[1] - before[1]) / 10.0)),
                    String.valueOf(((after[2] - before[2]) / 10.0)),
                    String.valueOf(fecha)
            );
            datosRAM = datosRAM + ((after[0] - before[0]) / 10.0);
            datosCPU = datosCPU + ((after[1] - before[1]) / 10.0);
            datosPKG = datosPKG + ((after[2] - before[2]) / 10.0);
            cont++;
            if (cont == 999) {
                auxiliarOnline++;
                jraplOnline = new Datos(String.valueOf(datosCPU),
                        String.valueOf(datosRAM),
                        String.valueOf(datosPKG),
                        String.valueOf(fecha));
                datosJraplOnline.add(jraplOnline);
                generaLiveCsvJRAPL(datosJraplOnline, date, path, auxiliarOnline);
                datosJraplOnline.clear();
                cont = 0;
            }
            datosJraplOffline.add(jraplOffline);

        }
        for (int i = 0; i < socketNum; i++) {
//                System.out.println("@Power consumption of dram: @" + (after[0] - before[0]) / 10.0 + "@power consumption of cpu: @" + (after[1] - before[1]) / 10.0 + "@power consumption of package: @" + (after[2] - before[2]) / 10.0 + " @time: @" + time);
        }
        InterfazFusion.data();
        generaJSONJRAPL(datosJraplOffline, date, path);
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

    public static void generaJSONJRAPL(ArrayList<Datos> lista, Date date, String path) {
        timestamp = Data.getTimestamp();
        FileWriter rawJsonWriter = null;
        FileWriter sumJsonWriter = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String ruta = "/home/roberth/Desktop/test/Resultados2_" + appTested + timestamp + ".json";
        String ruta2 = "/home/roberth/Desktop/test/Resultados_" + appTested + timestamp + ".json";
        rutaJRAPL = "Resultados2_" + appTested + timestamp + ".json";
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
                htmlGeneratorJRAPL(rutaHref, rutaHref2, timestamp, appTested, fecha);
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

    public static void htmlGeneratorJRAPL(String path, String path2, Long date, String appName, String fecha) {
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

    public static void generaJSONPowerAPI(ArrayList<Datos> lista, String path, String date) {
        FileWriter jsonWriter = null;
        String ruta = "/home/roberth/Desktop/test/Resultados3_" + appTested + timestamp + ".json";
        rutaPower = "Resultados3_" + appTested + timestamp + ".json";
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
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void htmlGeneratorPowerAPI(String path, String name, String date) {
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
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void enableMSR() throws IOException {
        Process msr = Runtime.getRuntime().exec("modprobe msr"); //Habilita acceso a los MSR
    }

    public static void start(String path) throws InterruptedException, ExecutionException {

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                enableMSR();
                jrapl(path);

                while (lock == true) {
                    System.out.println("here1");
                }
                return true;
            }

            @Override
            protected void done() {
                jraplRepeat = false;

            }
        };
        worker.execute();
    }

    public static void jrapl(String path) throws IOException, InterruptedException {
        Process jrapl = Runtime.getRuntime().exec("sudo bash /home/roberth/java-bash.sh " + path);
        jrapl.waitFor();
        jraplRepeat = false;
        lock = false;
    }

    Runnable runner = new Runnable() {
        @Override
        public void run() {
            try {
                lockRun = true;
                Process appTest = Runtime.getRuntime().exec("java -jar " + pathApp);
                appTest.waitFor();
                lockRun = false;
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };

    public void powerAPI(String path) {
        testDate = testInfo.getExecutionTime();
        appTested = testInfo.getAppName(path);
        ArrayList<Datos> liveDataPowerAPI = new ArrayList<>();
        ArrayList<Datos> dataFinalPowerAPI = new ArrayList<>();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            ExecutorService exec = Executors.newSingleThreadExecutor();
            exec.submit(runner);

            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appTested);
            Process builder = Runtime.getRuntime().exec("bash /home/roberth/getPID.sh " + appTested);
            BufferedReader readerGetPidSh = new BufferedReader(new InputStreamReader(builder.getInputStream()));
            String outputLine = "";
            String getJavaProccess = "";
            int arrPosition;
            while ((outputLine = readerGetPidSh.readLine()) != null) {
                String[] pidPosition = outputLine.split(" ");
                System.out.println(Arrays.toString(pidPosition));
                appRealPID = pidPosition[0];
                System.out.println("" + appRealPID);
                if (appRealPID.equals("")) {
                    appRealPID = pidPosition[1];
                }
                for (int i = 0; i < pidPosition.length; i++) {
                    getJavaProccess = pidPosition[i];
                    if ("java".equals(getJavaProccess)) {
                        arrPosition = i;
                        if (appRealPID.startsWith("p")) {
                            System.out.println("entre if");
                            System.out.println(appRealPID);
                            appRealPID = pidPosition[0];
                        } else {
                            System.out.println("entra else");
                            int pidInt = Integer.parseInt(appRealPID);
                            System.out.println(pidInt);
                            appRealPID = String.valueOf(pidInt);
                            break;
                        }

                    }
                }

                if (getJavaProccess.equals("java")) {
                    break;
                }
            }
            System.out.println(appRealPID + " PID");
            Process runPowerAPI = Runtime.getRuntime().exec(
                    "bash /home/roberth/powerAPI.sh " + appRealPID
            );

            BufferedReader readerRunPowerAPISh = new BufferedReader(new InputStreamReader(runPowerAPI.getInputStream()));
            File rawDataPowerAPI = new File("/home/roberth/Desktop/test/Data2.csv");
            FileWriter csvWriter;
            csvWriter = new FileWriter(rawDataPowerAPI, false);
            int header = 1;
            while (((outputLine = readerRunPowerAPISh.readLine()) != null) && (lockRun == true)) {
                csvWriter.append(outputLine);
                System.out.println(outputLine);
                String[] dataFiltered = outputLine.split(";");
                String[] timestampFilter = dataFiltered[1].split("=");
                String[] powerFilter1 = dataFiltered[4].split("=");
                String powerFiltered = powerFilter1[1];
                String[] powerFilter2 = powerFiltered.split(" ");
                String powerFInal = powerFilter2[0];
                String timestampRaw = timestampFilter[1];
                Double dbWatt = Double.parseDouble(powerFInal) / 1000000;
                powerFInal = String.valueOf(dbWatt);
                Date d = new Date(Long.parseLong(timestampRaw));
                String dateExec = d.toString();
                Datos datos = new Datos(powerFInal, powerFInal, powerFInal, dateExec);
                dataFinalPowerAPI.add(datos);
                liveDataPowerAPI.add(datos);
                generaLiveCSVPowerAPI(liveDataPowerAPI, header);
                liveDataPowerAPI.clear();
                header++;
                csvWriter.append("\n");
                if (outputLine.startsWith("Power")) {
                    break;
                }
            }
            generaJSONPowerAPI(dataFinalPowerAPI, path, testDate);

            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appTested + "\n"
                    + "Se ha generado el archivo csv");
            csvWriter.close();
            Process getpowerApiProcess = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh power");
            readerGetPidSh = new BufferedReader(new InputStreamReader(getpowerApiProcess.getInputStream()));
//            String powerApiPId1;
//            String powerApiPId2;
            String line2 = "";

            while ((line2 = readerGetPidSh.readLine()) != null) {
                String[] pid = line2.split(" ");
                String powerApiPid1 = pid[1];
                String powerApiPid2 = pid[0];
                Process kill = Runtime.getRuntime().exec("sudo kill " + powerApiPid1);
                Process kill2 = Runtime.getRuntime().exec("sudo kill " + powerApiPid2);
            }
//            clean(app, fecha);
//            interfaz.nuevoRegistro(appName, date.toString(), "", href);
//            interfaz.load();
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appTested + "\n"
                    + "Se ha generado el archivo csv" + "\nHa comenzado la limpieza de datos");

        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void clean(String app, String fecha) throws IOException {
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
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + app + "\n"
                    + "Se ha generado el archivo csv" + "\nHa comenzado la limpieza de datos"
                    + "\nHa finalizado la limpieza de datos");
//            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo para realizar la limpieza de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                    csvWriter.close();
                } catch (IOException e) {
                    Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }
}

class Datos {

    public String data0;
    public String data1;
    public String data2;
    public String date;

    Datos(String data0, String data1, String data2, String date) {
        this.data0 = data0;
        this.data1 = data1;
        this.data2 = data2;
        this.date = date;
    }

}
