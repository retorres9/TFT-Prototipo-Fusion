
import clases.Cancha;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import logica.GestionCanchas;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author FERCHO
 */
public class main {
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
    public static String EnergyInfo;
    
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
        }

        //System.out.print(System.getProperty("java.library.path"));
        System.loadLibrary("CPUScaler");
        wraparoundValue = EnergyCheckUtils.ProfileInit();
        socketNum = EnergyCheckUtils.GetSocketNum();
    }

    /**
     * @return an array of current energy information. The first entry is:
     * Dram/uncore gpu energy(depends on the cpu architecture. The second entry
     * is: CPU energy The third entry is: Package energy
     */
    public static double[] getEnergyStats() {
        socketNum = EnergyCheckUtils.GetSocketNum();
        
        try{
         EnergyInfo = EnergyCheckUtils.EnergyStatCheck();
        }
        catch(Exception ex)
        {
            System.out.println("Exception +"+  ex.getMessage());
        }
        System.out.println("Energy Info" + EnergyInfo);
        /*One Socket*/
        if (socketNum == 1) {
            double[] stats = new double[3];
            String[] energy = EnergyInfo.split("#");

            NumberFormat nf = NumberFormat.getInstance();

            try {
                stats[0] = nf.parse(energy[0]).doubleValue();
                stats[1] = nf.parse(energy[1]).doubleValue();
                stats[2] = nf.parse(energy[2]).doubleValue();
            } catch (ParseException e) {
                stats[0] = 0.000000;
                stats[1] = 0.000000;
                stats[2] = 0.000000;
            }

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
    public static void main(String[] args) {
        
        double[] before = EnergyCheckUtils.getEnergyStats();
        long pre = System.currentTimeMillis();
        double preTime = pre / 100000;
        
        ArrayList<Cancha> listCanchas =  new ArrayList<>();
        Cancha cancha1 = new Cancha("Futbol", "Cancha de futbol");
        for (int i=0; i<=10; i++){
            listCanchas.add(cancha1);
        }
        
        
        
        GestionCanchas gcanchas = new GestionCanchas();
        try{
            //Forma 1
            //Forma 1.1
            /*for (Cancha cancha : listCanchas) {
                gcanchas.guardarCancha(cancha);
            }*/
            
            //Forma 1.2
            
            /*gcanchas.guardarCancha(listCanchas);*/
            
            //Forma 2
            //Forma 2.1
            /*gcanchas.obtenerCanchas();*/
            //Forma 2.2
            gcanchas.obtenerCanchas2();
        }catch(Exception ex){
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        double[] after = EnergyCheckUtils.getEnergyStats();
        long post = System.currentTimeMillis();
	double postTime = post / 1000;
	double time = postTime - preTime;
        
        FileWriter flwriter = null;
		try {
			//crea el flujo para escribir en el archivo
			flwriter = new FileWriter("/home/laboratorio/Documents/resultado10.txt");
			//crea un buffer o flujo intermedio antes de escribir directamente en el archivo
			BufferedWriter bfwriter = new BufferedWriter(flwriter);
                        for (int i = 0; i <  socketNum; i++) {
                            bfwriter.write("Power consumption of dram: " + (after[0] - before[0]) / 10.0 + " power consumption of cpu: " + (after[1] - before[1]) / 10.0 + " power consumption of package: " + (after[2] - before[2]) / 10.0 + " time: " + time);
                        }

			//cierra el buffer intermedio
			bfwriter.close();
			System.out.println("Archivo creado satisfactoriamente..");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (flwriter != null) {
				try {//cierra el flujo principal
					flwriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
        for (int i = 0; i <  socketNum; i++) {
            System.out.println("Power consumption of dram: " + (after[0] - before[0]) / 10.0 + " power consumption of cpu: " + (after[1] - before[1]) / 10.0 + " power consumption of package: " + (after[2] - before[2]) / 10.0 + " time: " + time);
        }
       
    }
}
