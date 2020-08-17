
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
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

    public static void framework(String path) throws InterruptedException, IOException {
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
                System.out.println(time);
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
                    System.out.println("1");
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

    public static void powerAPI(String path) throws IOException, InterruptedException {
        String appRealPID = "";
        String[] name = path.split("/");
        String name3 = name[4];
        System.out.println(name3);
        Process appTest = Runtime.getRuntime().exec("java -jar " + path);
        appTest.waitFor(2, TimeUnit.SECONDS);
//        ProcessBuilder builder = new ProcessBuilder("ps ax | grep " + name3);
//        builder.redirectErrorStream(true);
//        Process process = builder.start();
//        InputStream is = process.getInputStream();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        Process getPID = Runtime.getRuntime().exec("/home/roberth/powerApiProccess.sh " + name3);
        InputStream is = getPID.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//
        String line = "";
        while ((line = reader.readLine()) != null) {
            String[] pid = line.split(" ");
            appRealPID = pid[0];
            break;
        }
        System.out.println(appRealPID);
//        Process runPowerAPI = Runtime.getRuntime().exec(
//                "/home/roberth/Downloads/powerapi-cli-4.2.1 ./bin/powerapi \\\n"
//                + "    modules rapl \\\n"
//                + "    monitor \\\n"
//                + "      --frequency 500 \\\n"
//                + "      --pids" + appRealPID + " \\\n"
//                + "      --console > /home/roberth/Desktop/Data.csv"
//        );
//        runPowerAPI.waitFor(2, TimeUnit.MINUTES);
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
