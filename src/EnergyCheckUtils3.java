
import java.lang.reflect.Field;
public class EnergyCheckUtils3 {
	public native static int scale(int freq);
	public native static int[] freqAvailable();
	
	public native static double[] GetPackagePowerSpec();
	public native static double[] GetDramPowerSpec();
	public native static void SetPackagePowerLimit(int socketId, int level, double costomPower);
	public native static void SetPackageTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramTimeWindowLimit(int socketId, int level, double costomTimeWin);
	public native static void SetDramPowerLimit(int socketId, int level, double costomPower);
	public native static int ProfileInit();
	public native static String EnergyStatCheck();
	public native static void ProfileDealloc();
	public native static void SetPowerLimit(int ENABLE);
	public static int wraparoundValue;	
	static {
		System.setProperty("java.library.path",
//				"/home/kenan/Downloads/Seminar/spec/src/spec/benchmarks/crypto/aes");
				"/usr/lib/jvm/java-8-openjdk-amd64/include/");
		try {
			Field fieldSysPath = ClassLoader.class
					.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) {

		}
		System.loadLibrary("CPUScaler");
		wraparoundValue = ProfileInit();
	}
	public static void main(String[] args) {
		//Info info = (Info)energyInfo.EnergyStatCheck();
		/*For jni header generation*/
		int socketNum = 0;
		double[] info1 = GetPackagePowerSpec();
//		double[] info2 = GetDramPowerSpec();

		String[][] before_info = new String[2][];
		String[][] after_info = new String[2][];
		String[] sockPreInfo = new String[2];
		String[] sockPostInfo = new String[2];


		//for(int i = 0; i < 4; i++)
			//System.out.println("package: " + info1[i]);
		
		SetPackagePowerLimit(0, 0, 150.0);
//		SetPackagePowerLimit(1, 0, 150.0);
//		SetDramPowerLimit(0, 0, 130.0);
//		SetDramPowerLimit(1, 0, 130.0);

		SetPackageTimeWindowLimit(0, 1, 1.0);
//		SetPackageTimeWindowLimit(1, 1, 1.0);
		
		/*
		EnergyStatCheck();
		ProfileDealloc();

		int[] a = freqAvailable();
		scale(10000);
		*/
		

		String before = EnergyStatCheck();
		//System.out.println(before);
		try {
			Thread.sleep(10000);
		} catch(Exception e) {
		}
		String after = EnergyStatCheck();
		//System.out.println(after);
		if(before.contains("@")) {
			socketNum = 2;
			sockPreInfo = before.split("@");
			sockPostInfo = after.split("@");

			for(int i = 0; i < sockPreInfo.length; i++) {
				before_info[i] = sockPreInfo[i].split("#");
				after_info[i] = sockPostInfo[i].split("#");
			}
		} else {
			socketNum = 1;
			before_info[0] = before.split("#");
			after_info[0] = after.split("#");
		}

		//System.out.println(after_info[0][0]);
		//System.out.println(before_info[0][0]);
		for(int i = 0; i < socketNum; i++) {
		System.out.println("gpu: " + (Double.parseDouble(after_info[i][0]) - Double.parseDouble(before_info[i][0])) / 10.0 + " cpu: " + (Double.parseDouble(after_info[i][1]) - Double.parseDouble(before_info[i][1])) / 10.0 + " package: " + (Double.parseDouble(after_info[i][2]) - Double.parseDouble(before_info[i][2])) / 10.0);
		}
		ProfileDealloc();
	}
}
