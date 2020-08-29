
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.TimeUnit;
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

    public  void framework(String path) throws InterruptedException, IOException {
        ArrayList<Datos> listad = new ArrayList<>();
        Datos data1;
//        String path = args[1];
//        String framework = args[0];
        String framework = "1";
        System.out.println("framework");

        if (framework.equals("1")) {
            Date date = new Date();
            date.getTime();
            int val1 = 1;
            String fe = "";
            System.out.println("here");
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
        double valueDRAM;
        double valueCPU;
        double valuePKG;
        double auxDRAM = 0;
        double auxCPU = 0;
        double auxPKG = 0;
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
                System.out.println("start ecu");
                System.out.println("cont 0");
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
        System.out.println(path);
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
                System.out.println(pathApp+" here=======");
                Process appTest = Runtime.getRuntime().exec("java -jar " + pathApp);
                appTest.waitFor();
                System.out.println("started");
                lockRun = false;
            } catch (IOException ex) {
                Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(EnergyCheckUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    };
    
//    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
//        @Override
//        protected Boolean doInBackground() throws Exception {
//            lockRun = true;
//            System.out.println(pathApp+" here=======");
//            //                Process appTest = Runtime.getRuntime().exec("java -jar " + pathApp);
//            System.out.println(pathApp);
//            System.out.println("started");
//            lock = false;
//            return true;
//        }
//
//        @Override
//        protected void done() {
//            System.out.println("finished");
//            lockRun = false;
//        }
//
//    };
    
    


    public void powerAPI(String path) throws IOException, InterruptedException {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(runna);
        String appRealPID = "";
        String[] name = path.split("/");
        String name3 = name[4];
        String[] part = name3.split("\\.");
        String app = part[0];
//        worker.execute();
        //        appTest.waitFor(2, TimeUnit.SECONDS);
        Process builder = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh " + name3);
        BufferedReader reader = new BufferedReader(new InputStreamReader(builder.getInputStream()));
        Process getPID = Runtime.getRuntime().exec("/home/roberth/powerApiProccess.sh " + name3);

        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(reader);
            String[] pid = line.split(" ");
            System.out.println(pid[1]);
            appRealPID = pid[1];
            System.out.println(appRealPID + "real");
            break;
        }
        System.out.println(appRealPID);
//        ProcessBuilder pb = new ProcessBuilder("sudo bash /home/roberth/powerAPI.sh " + appRealPID);
        Process runPowerAPI = Runtime.getRuntime().exec(
                "bash /home/roberth/powerAPI.sh " + appRealPID
        );
//        Process pro = pb.start();
        while (lockRun == true) {
            String a = "sdf";
            System.out.println(a);
        }
        System.out.println("arrived");
        Process builder2 = Runtime.getRuntime().exec("sudo bash /home/roberth/getPID.sh powerAPI.sh");
        reader = new BufferedReader(new InputStreamReader(builder2.getInputStream()));
        String appRealPID2=null;
        String line2 = "";
        while ((line2 = reader.readLine()) != null) {
            System.out.println(reader);
            String[] pid = line2.split(" ");
            System.out.println(Arrays.toString(pid));
            System.out.println(pid[1]);
            appRealPID2 = pid[2];
            System.out.println(appRealPID2+"000000000000000000000000");
            break;
        }
        Process kill = Runtime.getRuntime().exec("sudo kill "+appRealPID2);
        Thread.sleep(5000);
//        BufferedReader stdInput = new BufferedReader(new InputStreamReader(runPowerAPI.getInputStream()));
//
//        BufferedReader stdError = new BufferedReader(new InputStreamReader(runPowerAPI.getErrorStream()));
//
//// Read the output from the command
//        System.out.println("Here is the standard output of the command:\n");
//        String s = null;
//        while ((s = stdInput.readLine()) != null) {
//            System.out.println(s);
//        }

// Read any errors from the attempted command
//        System.out.println("Here is the standard error of the command (if any):\n");
//        while ((s = stdError.readLine()) != null) {
//            System.out.println(s);
//        }
////        int val =   runPowerAPI.waitFor();
//        System.out.println("here en " + name3);
//        System.out.println("start");
//        Thread.sleep(20000);
        System.out.println(app);
        clean(app);
//        Process killer = Runtime.getRuntime().exec("sudo kill "+ appRealPID);
//        killer.waitFor();
    }

    public static void clean(String app) throws IOException {

        String csvFile = "/home/roberth/Desktop/Data.csv";
        BufferedReader br = null;
        String line2 = "";
        String cvsSplitBy = ";";
        br = new BufferedReader(new FileReader(csvFile));
        File f = new File("/home/roberth/Desktop/Datos/Resultados_" + app + ".csv");
        FileWriter csvWriter = null;
        csvWriter = new FileWriter(f, true);

        try {

            while ((line2 = br.readLine()) != null) {

                System.out.println(line2);
                // use comma as separator
                String[] country = line2.split(cvsSplitBy);
//                System.out.println("Country [code= " + country[1] + " , name=" + country[4] + "]");
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
//                    String power = parts[2];
                    System.out.println(intTime + " " + jpower);
                    csvWriter.append(String.valueOf(intTime));
                    csvWriter.append(";");
                    csvWriter.append(String.valueOf(jpower));
                    csvWriter.append("\n");

                }
//                
                // 034556F

            }

            System.out.println("reached");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                    csvWriter.close();
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
