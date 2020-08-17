
import java.lang.reflect.Field;

public class EnergyCheckUtils4 {

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

    static {

        System.setProperty("java.library.path",
                //System.getProperty("user.dir")
                "/home/laboratorio/NetBeansProjects/jRAPLEnergy/src/"
        );
        try {
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        } catch (Exception e) {
            System.out.println("excepcion + " + e.getMessage());
        }

        String lib_path = System.getProperty("java.library.path", System.getProperty("user.dir"));
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(lib_path);
        System.loadLibrary("CPUScaler");
        wraparoundValue = EnergyCheckUtils.ProfileInit();
        socketNum = EnergyCheckUtils.GetSocketNum();
        //EnergyCheckUtils.GetPackagePowerSpec();
        //EnergyCheckUtils.GetDramPowerSpec();
    }

    /**
     * @return an array of current energy information. The first entry is:
     * Dram/uncore gpu energy(depends on the cpu architecture. The second entry
     * is: CPU energy The third entry is: Package energy
     */
    public static double[] getEnergyStats() {
        socketNum = GetSocketNum();
        System.out.println("Tracing....");
        System.out.println("[getEnergyStats] Begin ....");
        String EnergyInfo = EnergyCheckUtils4.EnergyStatCheck();
        System.out.println(EnergyInfo);
        /*One Socket*/
        String log = String.format("Calling getEnergyStats() .... Number of Sockets %d", socketNum);
        System.out.println(log);

        if (socketNum == 1) {
            double[] stats = new double[3];
            String[] energy = EnergyInfo.split("#");

            System.out.println(String.format("energy value is ::: %s", EnergyInfo));

            stats[0] = Double.parseDouble(energy[0]);
            stats[1] = Double.parseDouble(energy[1]);
            stats[2] = Double.parseDouble(energy[2]);

            return stats;

        } else {
            /*Multiple sockets*/
            System.out.println("Step 2");
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

    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        pro.waitFor();
    }

    public static void printTitle() {
        System.out.println("Frequency,hotMethodMin,hotMethodMax,MethodID,MethodName,MethodNameHashCode,DRAM/uncoreGPU,CPU,Package,Package_power,WallClockTime");
    }

    public static void main(String[] args) {
        //String str = String.format("sudo dist/FullAdaptiveMarkSweep_x86_64-linux/rvm -Xmx2500M -X:vm:interruptQuantum=%s -X:vm:errorsFatal=true -X:aos:enable_recompilation=true -X:aos:hot_method_time_min=0.1 -X:aos:hot_method_time_max=1 -X:aos:frequency_to_be_printed=%s -X:aos:event_counter=cache-misses,cache-references -X:aos:enable_counter_profiling=true -X:aos:enable_energy_profiling=true -X:aos:profiler_file=threads.csv -X:aos:enable_scaling_by_counters=false -X:aos:enable_counter_printer=true -jar dacapo-2006-10-MR2.jar -s large bloat -t %s", args[2], args[0], args[1]);

        //String str = String.format("sudo dist/FullAdaptiveMarkSweep_x86_64-linux/rvm -Xmx2500M -X:vm:interruptQuantum=%s -X:vm:errorsFatal=true -X:aos:enable_recompilation=true -X:aos:hot_method_time_min=50 -X:aos:hot_method_time_max=800 -X:aos:frequency_to_be_printed=%s -X:aos:event_counter=cache-misses -X:aos:enable_counter_profiling=false -X:aos:enable_energy_profiling=false -X:aos:profiler_file=threads.csv -X:aos:enable_scaling_by_counters=false -X:aos:enable_counter_printer=true -jar dacapo-9.12-bach.jar -s large sunflow -t %s", args[2], args[0], args[1]);
        //String str = String.format("sudo dist/FullAdaptiveMarkSweep_x86_64-linux/rvm -Xmx2500M -X:vm:interruptQuantum=%s -X:vm:errorsFatal=true -X:aos:enable_recompilation=true -X:aos:hot_method_time_min=50 -X:aos:hot_method_time_max=800 -X:aos:frequency_to_be_printed=%s -X:aos:event_counter=cache-misses -X:aos:enable_counter_profiling=false -X:aos:enable_energy_profiling=false -X:aos:profiler_file=threads.csv -X:aos:enable_scaling_by_counters=false -X:aos:enable_counter_printer=true -jar dacapo-9.12-bach.jar -s large sunflow -t");
        double[] preEner = EnergyCheckUtils.getEnergyStats();
        long pre = System.currentTimeMillis();
        double preTime = pre / 100000;
        int con = 0;
        try {
            for (int i = 0; i < 10; i++) {
                //runProcess(str);
                //System.out.println("contador " + i);
                con = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        double[] postEner = EnergyCheckUtils.getEnergyStats();
        long post = System.currentTimeMillis();
        double postTime = post / 1000;
        double time = postTime - preTime;
        //printTitle();
        System.out.println("postEner[0] - preEner[0]" + ","
                + "postEner[1] - preEner[1]"
                + "," + "postEner[2] - preEner[2]" + ","
                + "(postEner[2] - preEner[2])/ time" + "," + "post - pre");
        for (int i = 0; i < socketNum; i++) {
            //System.out.println(Integer.parseInt(args[0]) + "," + ",,,,," + (postEner[0] - preEner[0]) + "," + (postEner[1] - preEner[1]) + "," + (postEner[2] - preEner[2]) + "," + (postEner[2] - preEner[2]) / time + "," + (post - pre));
            System.out.println((postEner[0] - preEner[0]) + "," + (postEner[1] - preEner[1]) + "," + (postEner[2] - preEner[2]) + "," + (postEner[2] - preEner[2]) / time + "," + (post - pre));
        }
        EnergyCheckUtils.ProfileDealloc();
    }
}
