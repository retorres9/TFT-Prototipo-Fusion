
import java.io.BufferedReader;
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
        Datos data1;
        String framework = "1";

        if (framework.equals("1")) {
            Date date = new Date();
            date.getTime();
            int val1 = 1;
            String fe = "";
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

                    fe = dtf.format(now);
                } catch (Exception e) {
                    Logger.getLogger(EnergyCheckUtils.class
                            .getName()).log(Level.SEVERE, null, e);
                }
                double[] after = EnergyCheckUtils.getEnergyStats();
                long post = System.currentTimeMillis();
                double postTime = post / 100000;
                double time = postTime - preTime;
                data1 = new Datos(
                        String.valueOf(((after[0] - before[0]) / 10.0)),
                        String.valueOf(((after[1] - before[1]) / 10.0)),
                        String.valueOf(((after[2] - before[2]) / 10.0)),
                        String.valueOf(fe)
                );

                listad.add(data1);
            }
            for (int i = 0; i < socketNum; i++) {
//                System.out.println("@Power consumption of dram: @" + (after[0] - before[0]) / 10.0 + "@power consumption of cpu: @" + (after[1] - before[1]) / 10.0 + "@power consumption of package: @" + (after[2] - before[2]) / 10.0 + " @time: @" + time);
            }
            generaCSV(listad, date, path);
        }
        if (framework.equals("2")) {
            powerAPI(path);
        }
    }

    public static void generaCSV(ArrayList<Datos> lista, Date date, String path) {
        String appRealPID = "";
        String[] name = path.split("/");
        String name3 = name[4];
        FileWriter csvWriter = null;
        InterfazFusion interfaz = new InterfazFusion();
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
        interfaz.txtStatus.setText("La medición ha empezado...\nObteniendo datos..."
                + "\nGenerando archivo Resultados_" + name3 + "_" + date + ".csv");
        try {
            Iterator itr = lista.iterator();
            File f = new File("/home/roberth/Desktop/Datos/Resultados_" + name3 + "_" + date + ".csv");
            int header = 1;
            csvWriter = new FileWriter(f, true);
            if (header == 1) {
                csvWriter.append("Energy DRAM (J)");
                csvWriter.append(";");
                csvWriter.append("Energy CPU (J)");
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
                interfaz.txtStatus.setText("La medición ha empezado...\nObteniendo datos..."
                        + "\nGenerando archivo Resultados_" + name3 + "_" + date + ".csv\n"
                        + "Archivo Resultados_" + name3 + "_" + date + ".csv generado exitosamente!!!");
                interfaz.btnLoading.setVisible(false);
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class
                        .getName()).log(Level.SEVERE, null, ex);

            }
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
                JOptionPane.showMessageDialog(null, "Terminó la ejecución del programa");
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

    Runnable runna = new Runnable() {
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

    public void powerAPI(String path) throws IOException, InterruptedException {
        String fecha;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-mm-yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        fecha = dtf.format(now);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(runna);
        String appRealPID = "";
        String[] name = path.split("/");
        String name3 = name[4];
        String[] part = name3.split("\\.");
        String app = part[0];
        Process builder = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh " + name3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(builder.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            String[] pid = line.split(" ");
            appRealPID = pid[0];
            int pidInt = Integer.parseInt(appRealPID) + 1;
            appRealPID = String.valueOf(pidInt);
            break;
        }

        Process runPowerAPI = Runtime.getRuntime().exec(
                "bash /home/roberth/powerAPI.sh " + appRealPID
        );

        BufferedReader reader3 = new BufferedReader(new InputStreamReader(runPowerAPI.getInputStream()));
        File f = new File("/home/roberth/Desktop/test/Data2.csv");
        FileWriter csvWriter = null;
        csvWriter = new FileWriter(f, false);
        while (((line = reader3.readLine()) != null) && (lockRun == true)) {
            csvWriter.append(line);
            csvWriter.append("\n");
            if (line.startsWith("Power")) {
                break;
            }

        }
        csvWriter.close();
        Process builder2 = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh power");
        reader = new BufferedReader(new InputStreamReader(builder2.getInputStream()));
        String appRealPID2 = null;
        String line2 = "";
        while ((line2 = reader.readLine()) != null) {
            String[] pid = line2.split(" ");
            appRealPID2 = pid[1];
            Process kill = Runtime.getRuntime().exec("sudo kill " + appRealPID2);
        }

        Thread.sleep(5000);
        clean(app, fecha);
    }

    public static void clean(String app, String fecha) throws IOException {
        InterfazFusion inter = new InterfazFusion();
        String csvFile = "/home/roberth/Desktop/test/Data2.csv";
        BufferedReader br = null;
        String line2 = "";
        String cvsSplitBy = ";";
        br = new BufferedReader(new FileReader(csvFile));
        File f = new File("/home/roberth/Desktop/Datos/Resultados_" + app + "_" + fecha + ".csv");
        FileWriter csvWriter = null;
        csvWriter = new FileWriter(f, false);

        try {

            while ((line2 = br.readLine()) != null) {
                String[] country = line2.split(cvsSplitBy);
                String string = country[1];
                String string2 = country[4];
                if (string.length() > 0) {
                    String[] parts = string.split("=");
                    String[] parts2 = string2.split(" ");
                    String time = parts[1];
                    double intTime = Double.parseDouble(time);
                    intTime = 25569 + intTime / 86400000;
                    intTime = intTime / 86400000;
                    intTime = 25569 + intTime;
                    String power = parts2[0];
                    String[] parts3 = power.split("=");
                    String jpower = parts3[1];
                    csvWriter.append(String.valueOf(intTime));
                    csvWriter.append(";");
                    csvWriter.append(String.valueOf(jpower));
                    csvWriter.append("\n");

                }

            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    csvWriter.close();
                    inter.load();
                    System.out.println("doneeeeeeeee");
                } catch (IOException e) {
                    e.printStackTrace();
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

class DatosPower {

    public String data0;
    public String data1;

    DatosPower(String data0, String data1) {
        this.data0 = data0;
        this.data1 = data1;
    }

}
