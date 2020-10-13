
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

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
    public static boolean flag = true;
    static boolean lock = true;
    InterfazFusion interfaz;
    public static String pathApp;

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
        ArrayList<Datos> listad = new ArrayList<>();
        ArrayList<Datos> listaR = new ArrayList<>();
        Datos jraplDAta;
        Datos liveData;
        String framework = "1";

        if (framework.equals("1")) {
            Date date = new Date();
            date.getTime();
            int val1 = 1;
            String fecha = "";
            int cont = 0;
            double auxC = 0;
            double auxRemaining = 0;
            int auxiliar = 0;
            while (flag == true) {
                double[] before = EnergyCheckUtils.getEnergyStats();
                long pre = System.currentTimeMillis();
                double preTime = pre / 100000;
                try {
                    if (val1 == 1) {
                        start(path);
                        val1++;
                    }
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
                    LocalDateTime now = LocalDateTime.now();

                    fecha = dtf.format(now);
                } catch (Exception e) {
                    Logger.getLogger(EnergyCheckUtils.class
                            .getName()).log(Level.SEVERE, null, e);
                }
                double[] after = EnergyCheckUtils.getEnergyStats();
                long post = System.currentTimeMillis();
                double postTime = post / 100000;
                double time = postTime - preTime;
                jraplDAta = new Datos(
                        String.valueOf(((after[0] - before[0]) / 10.0)),
                        String.valueOf(((after[1] - before[1]) / 10.0)),
                        String.valueOf(((after[2] - before[2]) / 10.0)),
                        String.valueOf(fecha)
                );
                auxC = auxC + ((after[0] - before[0]) / 10.0);
                cont++;
                if (cont == 999) {
                    auxiliar++;
                    auxC = auxC / 1000;
                    auxRemaining = auxRemaining + auxC;
                    liveData = new Datos(String.valueOf(auxC),
                            String.valueOf(auxC),
                            String.valueOf(auxC),
                            String.valueOf(fecha));
                    listaR.add(liveData);
                    generaLiveJSON2(listaR, date, path, auxiliar);
                    listaR.clear();
                    cont = 0;
                }
                listad.add(jraplDAta);
//                generaJSON(listad, date, path);

            }
            for (int i = 0; i < socketNum; i++) {
//                System.out.println("@Power consumption of dram: @" + (after[0] - before[0]) / 10.0 + "@power consumption of cpu: @" + (after[1] - before[1]) / 10.0 + "@power consumption of package: @" + (after[2] - before[2]) / 10.0 + " @time: @" + time);
            }
            InterfazFusion.data();
            generaCSV(listad, date, path);

//            generaJSON(listad, date, path);
        }
        if (framework.equals("2")) {
            powerAPI(path);
        }
    }

    public static void generaCSV(ArrayList<Datos> lista, Date date, String path) {
        int header = 1;
        String[] arrayPath = path.split("/");
        String appName = arrayPath[arrayPath.length - 1];
        FileWriter csvWriter = null;
        InterfazFusion interfaz = new InterfazFusion();
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        interfaz.txtStatus.setText("La medición ha empezado...\nObteniendo datos..."
                + "\nGenerando archivo Resultados_" + appName + "_" + date + ".csv");
        try {
            Iterator itr = lista.iterator();
            File f = new File("/home/roberth/Desktop/test/Resultados_" + appName + ".csv");
            csvWriter = new FileWriter(f);
            if (header == 1) {
                csvWriter.append("Energy DRAM (J)");
                csvWriter.append(";");
                csvWriter.append("Value");
                csvWriter.append(";");
                csvWriter.append("Energy Package (J)");
                csvWriter.append(";");
                csvWriter.append("Hora");
                csvWriter.append(";");
                csvWriter.append("Increase Energy DRAM (J)");
                csvWriter.append(";");
                csvWriter.append("Increase Energy CPU (J)");
                csvWriter.append(";");
                csvWriter.append("Increase Energy Package (J)");
                csvWriter.append("\n");
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
                csvWriter.append(st.data0);
                csvWriter.append(";");
                csvWriter.append(st.data1);
                csvWriter.append(";");
                csvWriter.append(st.data2);
                csvWriter.append(";");
                csvWriter.append(st.date);
                csvWriter.append(";");
                csvWriter.append(String.valueOf(auxDRAM));
                csvWriter.append(";");
                csvWriter.append(String.valueOf(auxCPU));
                csvWriter.append(";");
                csvWriter.append(String.valueOf(auxPKG));
                csvWriter.append("\n");

            }
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                csvWriter.close();
                interfaz.btnLoading.setVisible(false);
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void generaLiveJSON2(ArrayList<Datos> lista, Date date, String path, int header) {
        String[] name = path.split("/");
        int position = name.length - 1;
        String appName = name[position];
        FileWriter jsonWriter = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String ruta = "/home/roberth/Desktop/test/Resultados_live.csv";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator itr = lista.iterator();
            File file1 = new File(ruta);
            jsonWriter = new FileWriter(file1, true);
            if (header == 1) {
                jsonWriter.append("time");
                jsonWriter.append(",");
                jsonWriter.append("dram");
                jsonWriter.append(",");
                jsonWriter.append("cpu");
                jsonWriter.append(",");
                jsonWriter.append("package");
                jsonWriter.append("\n");
                header++;
            }

            while (itr.hasNext()) {
                Datos st = (Datos) itr.next();
                valueDRAM = Double.parseDouble(st.data0);
                System.out.println(valueDRAM + "000000000000000");
                auxDRAM = auxDRAM + valueDRAM;
                valueCPU = Double.parseDouble(st.data1);
                auxCPU = auxCPU + valueCPU;
                valuePKG = Double.parseDouble(st.data2);
                auxPKG = auxPKG + valuePKG;
                jsonWriter.append(st.date);
                jsonWriter.append(",");
                jsonWriter.append(st.data0);
                jsonWriter.append(",");
                jsonWriter.append(st.data1);
                jsonWriter.append(",");

                jsonWriter.append(st.data2);
                jsonWriter.append("\n");

            }
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

    public static void generaLiveCSVPowerAPI(ArrayList<Datos> lista, int header) {
        FileWriter jsonWriter = null;
        String ruta = "/home/roberth/Desktop/test/Resultados_live2.csv";

        double valueDRAM;
        try {
            Iterator itr = lista.iterator();
            File file1 = new File(ruta);
            jsonWriter = new FileWriter(file1, true);
            if (header == 1) {
                jsonWriter.append("time");
                jsonWriter.append(",");
                jsonWriter.append("cpu");
                jsonWriter.append("\n");
                header++;
            }

            while (itr.hasNext()) {

                Datos st = (Datos) itr.next();
                System.out.println("here");
                jsonWriter.append(st.date);
                jsonWriter.append(",");
                valueDRAM = Double.parseDouble(st.data1);
                jsonWriter.append(String.valueOf(valueDRAM));
                jsonWriter.append("\n");

            }
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

    public static void generaJSON(ArrayList<Datos> lista, Date date, String path) {
        String[] name = path.split("/");
        int position = name.length - 1;
        String appName = name[position];
        FileWriter jsonWriter = null;
        FileWriter jsonWriter2 = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String ruta = "/home/roberth/Desktop/test/Resultados_2.json";
        String ruta2 = "/home/roberth/Desktop/test/Resultados_" + appName + "2_" + fecha + ".json";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator itr = lista.iterator();
            File file1 = new File(ruta);
            File file2 = new File(ruta2);
            int header = 1;
            jsonWriter = new FileWriter(file1, false);
            jsonWriter2 = new FileWriter(file2, true);
            if (header == 1) {
                jsonWriter.append("[");
                jsonWriter.append("\n");
                jsonWriter2.append("[");
                jsonWriter2.append("\n");
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
                jsonWriter2.append("[\"" + st.date + "\"");
                jsonWriter2.append(",");
                jsonWriter.append(st.data0);
                jsonWriter.append(",");
                jsonWriter2.append(String.valueOf(auxDRAM));
                jsonWriter2.append(",");
                jsonWriter.append(st.data1);
                jsonWriter.append(",");
                jsonWriter2.append(String.valueOf(auxCPU));
                jsonWriter2.append(",");
                if (!itr.hasNext()) {
                    jsonWriter.append(st.data2 + "]");
                    jsonWriter2.append(String.valueOf(auxPKG) + "]");
                }
                if (itr.hasNext()) {
                    jsonWriter.append(st.data2 + "]");
                    jsonWriter2.append(String.valueOf(auxPKG) + "]");
                    jsonWriter.append(",");
                    jsonWriter2.append(",");
                }

                jsonWriter.append("\n");
                jsonWriter2.append("\n");

            }
//            ruta2 = "Resultados_" + appName + "2_" + fecha + ".json";
            jsonWriter.append("]");
            jsonWriter2.append("]");
//            htmlGenerator(ruta, ruta2);
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jsonWriter.close();
                jsonWriter2.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void htmlGenerator(String path, String path2) {
        File file = new File("/home/roberth/Desktop/test/template2.html");
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

            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/roberth/Desktop/test/template-powerAPI.html"));
            bw.write(builder.toString());
            bw.close();
            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void generaJSONPowerAPI(ArrayList<Datos> lista, String path) {
        String[] name = path.split("/");
        int position = name.length - 1;
        String appName = name[position];
        FileWriter jsonWriter = null;
        FileWriter jsonWriter2 = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        String fecha = dtf.format(now);
        String ruta = "/home/roberth/Desktop/test/Resultados_3.json";
        String ruta2 = "/home/roberth/Desktop/test/Resultados_" + appName + "3_" + fecha + ".json";
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        try {
            Iterator itr = lista.iterator();
            File file1 = new File(ruta);
            File file2 = new File(ruta2);
            int header = 1;
            jsonWriter = new FileWriter(file1, false);
            jsonWriter2 = new FileWriter(file2, true);
            if (header == 1) {
                jsonWriter.append("[");
                jsonWriter.append("\n");
                jsonWriter2.append("[");
                jsonWriter2.append("\n");
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
//            ruta2 = "Resultados_" + appName + "2_" + fecha + ".json";
            jsonWriter.append("]");
            htmlGenerator2(ruta);
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                jsonWriter.close();
                jsonWriter2.close();
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
        }
    }

    public static void htmlGenerator2(String path) {
        File file = new File("/home/roberth/Desktop/test/template-powerAPI.html");
        try {
            String ENDL = System.getProperty("line.separator");

            StringBuilder builder = new StringBuilder();

            BufferedReader buffer = new BufferedReader(new FileReader(file));
            String ln;
            while ((ln = buffer.readLine()) != null) {
                builder.append(ln
                        .replace("$1", "Resultados_3.json")
                ).append(ENDL);
            }
            buffer.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter("/home/roberth/Desktop/test/template-powerAPI2.html"));
            bw.write(builder.toString());
            bw.close();
            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void enableMSR() throws IOException {
        Process msr = Runtime.getRuntime().exec("modprobe msr"); //Habilita acceso a los MSR
    }

    public static void start(String path) throws InterruptedException, ExecutionException {

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                int cont = 0;
                enableMSR();
                jrapl(path);

                while (lock == true) {
                    System.out.println("here1");
                }
                return true;
            }

            @Override
            protected void done() {
                flag = false;

            }
        };
        worker.execute();
    }

    public static void jrapl(String path) throws IOException, InterruptedException {
        String jraplOutput = "";
        Process jrapl = Runtime.getRuntime().exec("sudo bash /home/roberth/java-bash.sh " + path);
        jrapl.waitFor();
        flag = false;
        lock = false;
    }

    static boolean lockRun;

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
        try {
            String fecha;
            int counter = 0;
            ArrayList<Datos> listaR = new ArrayList<>();
            ArrayList<Datos> lista2 = new ArrayList<>();
            String appRealPID = "";
            String[] arregloRuta = path.split("/");
            String appName = arregloRuta[arregloRuta.length - 1];
            String[] arrayAppName = appName.split("\\.");
            String app = arrayAppName[0];

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-mm-yyyy HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            fecha = dtf.format(now);

            ExecutorService exec = Executors.newSingleThreadExecutor();
            exec.submit(runner);

            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appName);
            Process builder = Runtime.getRuntime().exec("bash /home/roberth/getPID.sh " + appName);
            BufferedReader readerGetPidSh = new BufferedReader(new InputStreamReader(builder.getInputStream()));
            String outputLine = "";

            while ((outputLine = readerGetPidSh.readLine()) != null) {
                String[] pidPosition = outputLine.split(" ");
                appRealPID = pidPosition[0];
                String getJavaProccess = "";
                int arrPosition;
                for (int i = 0; i < pidPosition.length; i++) {
                    getJavaProccess = pidPosition[i];
                    if (getJavaProccess.equals("java")) {
                        arrPosition = i;
                        if (appRealPID.startsWith("p")) {
                            appRealPID = pidPosition[0];
                        }
                        int pidInt = Integer.parseInt(appRealPID);
                        appRealPID = String.valueOf(pidInt);
                        
                    }
                    break;
                }
            }
            Process runPowerAPI = Runtime.getRuntime().exec(
                    "bash /home/roberth/powerAPI.sh " + appRealPID
            );

            BufferedReader readerRunPowerAPISh = new BufferedReader(new InputStreamReader(runPowerAPI.getInputStream()));
            File file = new File("/home/roberth/Desktop/test/Data2.csv");
            FileWriter csvWriter;
            csvWriter = new FileWriter(file, false);
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
                String timestamp = timestampFilter[1];

                System.out.println("timestamp = " + timestamp);
                System.out.println("power = " + powerFInal);
                Date d = new Date(Long.parseLong(timestamp));
                String dateExec = d.toString();
                Datos datos = new Datos(powerFInal, powerFInal, powerFInal, dateExec);
                lista2.add(datos);
                listaR.add(datos);
                generaLiveCSVPowerAPI(listaR, header);
                counter = 0;
                listaR.clear();
                header++;
                System.out.println(timestamp + " " + powerFInal);
                csvWriter.append("\n");
                if (outputLine.startsWith("Power")) {
                    break;
                }

            }
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appName + "\n"
                    + "Se ha generado el archivo csv");
            csvWriter.close();
            Process builder2 = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh power");
            readerGetPidSh = new BufferedReader(new InputStreamReader(builder2.getInputStream()));
            String powerApiPId1;
            String powerApiPId2;
            String line2 = "";
            generaJSONPowerAPI(lista2, path);
            while ((line2 = readerGetPidSh.readLine()) != null) {
                String[] pid = line2.split(" ");
                powerApiPId1 = pid[1];
                powerApiPId2 = pid[0];
                Process kill = Runtime.getRuntime().exec("sudo kill " + powerApiPId1);
                Process kill2 = Runtime.getRuntime().exec("sudo kill " + powerApiPId2);
            }

            Thread.sleep(5000);
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + appName + "\n"
                    + "Se ha generado el archivo csv" + "\nHa comenzado la limpieza de datos");
            clean(app, fecha);
        } catch (IOException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void clean(String app, String fecha) throws IOException {
        InterfazFusion inter = new InterfazFusion();
        String csvFile = "/home/roberth/Desktop/test/Data2.csv";
        BufferedReader buffer;
        String outputLineCsv;
        String cvsSplit = ";";
        buffer = new BufferedReader(new FileReader(csvFile));
        File file = new File("/home/roberth/Desktop/Datos/Resultados_" + app + "_" + fecha + ".csv");
        FileWriter csvWriter;
        csvWriter = new FileWriter(file, false);

        try {

            while ((outputLineCsv = buffer.readLine()) != null) {
                String[] dataFromCsv = outputLineCsv.split(cvsSplit);
                String timestamp = dataFromCsv[1];
                String powerConsumption = dataFromCsv[4];
                if (timestamp.length() > 0) {
                    String[] timestampPart = timestamp.split("=");
                    String[] powerSplit = powerConsumption.split(" ");
                    String time = timestampPart[1];
                    double intTime = Double.parseDouble(time);
                    intTime = 25569 + intTime / 86400000;
                    String power = powerSplit[0];
                    String[] powerSplit2 = power.split("=");
                    String jpower = powerSplit2[1];
                    csvWriter.append(String.valueOf(intTime));
                    csvWriter.append(";");
                    csvWriter.append(String.valueOf(jpower));
                    csvWriter.append("\n");

                }

            }
            InterfazFusion.txtStatus.setText("Ha comenzado la medición de la aplicación " + app + "\n"
                    + "Se ha generado el archivo csv" + "\nHa comenzado la limpieza de datos"
                    + "\nHa finalizado la limpieza de datos");
            JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa\ny se ha generado el archivo html para visualizar los datos");
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No se ha encontrado el archivo para realizar la limpieza de datos", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                    csvWriter.close();
                    inter.load();
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
