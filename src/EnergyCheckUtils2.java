import java.lang.reflect.Field;
//package fr.inria.measurenergy;

//import fr.inria.approxloop.perfenergy.JsynLoopsMicroBenchs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Random;

public class EnergyCheckUtils2 {
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
		//System.setProperty("java.library.path", 
                        //"/home/elmarce/PROJECTS/PHD/JRALP-measure-energy/src/main/java");
				//System.getProperty("user.dir"));
                //System.setProperty("java.library.path", "/home/laboratorio/NetBeansProjects/EnergySampler");                
                System.setProperty("java.library.path", 
                        "/home/laboratorio/NetBeansProjects/jRAPLEnergy/src/"
                );
                
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) { }

		System.out.print(System.getProperty("java.library.path"));

		System.loadLibrary("CPUScaler");
		wraparoundValue = EnergyCheckUtils.ProfileInit();
		socketNum = EnergyCheckUtils.GetSocketNum();
		System.out.println("Sockets: " + socketNum);
	}

	public static double getCPUEnergy(String statsStr) {
		String e = statsStr.replace(",", ".");
		int b = e.indexOf("#") + 1;
		return Double.parseDouble(e.substring(b, e.indexOf("#", b + 1)));
	}

	/**
	 * @return an array of current energy information.
	 * The first entry is: Dram/uncore gpu energy(depends on the cpu architecture.
	 * The second entry is: CPU energy
	 * The third entry is: Package energy
	 */

	public static double[] getEnergyStats(String statsStr) {
		socketNum = EnergyCheckUtils.GetSocketNum();
		String EnergyInfo = statsStr.replace(",", ".");
		System.out.println(EnergyInfo);

		/*One Socket*/
		if(socketNum == 1) {
			double[] stats = new double[3];
			String[] energy = EnergyInfo.split("#");

			stats[0] = Double.parseDouble(energy[0]);
			stats[1] = Double.parseDouble(energy[1]);
			stats[2] = Double.parseDouble(energy[2]);

			return stats;

		} else {
		/*Multiple sockets*/
			String[] perSockEner = EnergyInfo.split("@");
			double[] stats = new double[3 * socketNum];
			int count = 0;

			for(int i = 0; i < perSockEner.length; i++) {
				String[] energy = perSockEner[i].split("#");
				for(int j = 0; j < energy.length; j++) {
					count = i * 3 + j;	//accumulative count
					stats[count] = Double.parseDouble(energy[j]);
				}
			}
			return stats;

		}

	}

	private static final int N = 500000*2;

	static double outputs[] = new double[N];

	static double compsumtions[] = new double[1000];

	/**
	 * Entry point for the energy measurements.
	 * @param args
	 */
	public static void main(String[] args) {
		JsynLoopsMicroBenchs mb = new JsynLoopsMicroBenchs();
		for ( int j = 0; j < 1000; j ++) {
			String a = EnergyCheckUtils.EnergyStatCheck();
			mb.benchmarkOutput();
			String b = EnergyCheckUtils.EnergyStatCheck();

			double d1 = EnergyCheckUtils2.getEnergyStats(a)[2];
			double d2 = EnergyCheckUtils2.getEnergyStats(b)[2];

			compsumtions[j] = (d2 - d1) / 10.0;
			System.out.println(mb.outputs[new Random().nextInt(N - 1)]);
		}
		EnergyCheckUtils.ProfileDealloc();
		System.out.println("---------- DONE ---------");
		try(BufferedWriter w = new BufferedWriter(new FileWriter(new File("result.txt"))))
		{
			for ( int j = 50; j < 100; j ++)
				w.write(String.valueOf(compsumtions[j]) + "\n");
		}
		catch(IOException ex)
		{
		}
	}
}