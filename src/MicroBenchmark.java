
import java.util.*;
import java.text.DecimalFormat;
import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
//import energy.receiver.SocketReceiver;

public class MicroBenchmark {

    /*make these three to be global variable to omit compiler optimization*/
    static int randomCount = 0;
    static boolean isRandom = false;
    static boolean isArrayAccess = false;
    public static int PKGNUM = 10;
    static int socketNum = 1;
    static String option = null;
    static String[] combine = new String[4];
    public static double frequency = 0.0;

    public native static int scale(int freq);

    public native static int[] freqAvailable();
    //public native static void ProfileInit();
    public native static String EnergyStatCheck();
    public static String stringWrite = new String();
    //public static JFrame testFrame = new JFrame();
    static boolean objCreated = false;
    static boolean intCreated = false;
    static int N = 500000;
    //static int N = 200000000;

    static int LOOPNO = 6;
    static int warmupLoop = 2;
    static int optionNum;
//	static int optionNum;
    public static int[] currentIndex = new int[N + 1];
    public static int TARGET_FILE_NUM = 3;

    class Info {

        double mem;
        double cpu;
        double gpu;
    }
    public static Integer integerData;
    public static int intData = 1;
    public static Object objData = new Object();

    public static int[] intArray = new int[N + 1];
    public static Integer[] integerArray = new Integer[N + 1];
    public static Object[] objArray = new Object[N + 1];

    public static List<Object> objContainer = new ArrayList<Object>(N);
    public static List<Integer> intContainer = new ArrayList<Integer>(N);

    /*
	static {
		System.setProperty("java.library.path",
//				"/home/kenan/Downloads/Seminar/spec/src/spec/benchmarks/crypto/aes");
				"/home/kenan/energy");
		try {
			Field fieldSysPath = ClassLoader.class
					.getDeclaredField("sys_paths");
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (Exception e) {

		}
		System.loadLibrary("CPUScaler");
		ProfileInit();
	}
     */

    public static void doOption() {
        int index;
        //Integer j = new Integer(0);
        //int j = 0;
        if (optionNum == 1) {			//Object reference write
            for (int i = 0; i < N; i++) {
                index = currentIndex[i];
                objContainer.set(index, objData);
            }
        } else if (optionNum == 2) {		//Integer reference write
            for (int i = 0; i < N; i++) {
                index = currentIndex[i];
                intContainer.set(index, integerData);
            }
        }
        if (optionNum == 3) {			//Object reference read
            for (int i = 0; i < N; i++) {
                index = currentIndex[i];
                objData = objContainer.get(index);

            }
        } else if (optionNum == 4) {		//Integer reference read
            for (int i = 0; i < N; i++) {
                index = currentIndex[i];
                objData = intContainer.get(index);
            }
        } else if (optionNum == 5) {		//Value query
            for (int i = 0; i < N; i++) {
                index = currentIndex[i];
                //intContainer.get(index).intValue();
                intContainer.get(index);
            }
        } else if (optionNum == 6) {		//Type query
            for (int i = 0; i < N; i++) {

                index = currentIndex[i];
                objData = objContainer.get(index);
                if (objData instanceof Float) {
                    //int j = ((Integer)objContainer.get(index)).intValue();
                }
            }
        } else if (optionNum == 7) {		//Write bytes to the file with buffer
            try {
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("testData")));
                for (int i = 0; i < N; i++) {
                    out.writeByte(3);
                }
                out.close();
            } catch (IOException e) {

            }
        } else if (optionNum == 8) {		//Read from the file with buffer
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream("testData")));
                for (int i = 0; i < N; i++) {
                    in.readByte();
                }
                in.close();
            } catch (IOException e) {

            }
        } /*else if(optionNum == 9) {
			int temp;
			try {
				InputStreamReader in = new InputStreamReader(new FileInputStream("testData"));
				String message = new String();
				while((temp = in.read()) != -1) {
					message += (char)temp;
				}
				in.close();
			} catch (IOException e) {

			}
		}*/ else if (optionNum == 9) {	//Write bytes to the file without buffer
            try {
                DataOutputStream out = new DataOutputStream(new FileOutputStream("testData"));
                for (int i = 0; i < N; i++) {
                    out.writeByte(3);
                }
                out.close();
            } catch (IOException e) {

            }
        } else if (optionNum == 10) {	//Read from the file without buffer 5000000 times
            try {
                DataInputStream in = new DataInputStream(new FileInputStream("testData"));
                for (int i = 0; i < N; i++) {
                    in.readByte();
                }
                in.close();
            } catch (IOException e) {

            }
        } else if (optionNum == 11) {	//standard I/O operation 500000 times
            for (int i = 0; i < N / 100; i++) {
                System.out.println("ok");
            }
        } else if (optionNum == 12 || optionNum == 13 || optionNum == 14 || optionNum == 15 || optionNum == 16) {

            MatMultc.multiply();

        } /*
		}else if(optionNum == 15) {
			try {
				SocketReceiver.receiveFile(); //15. Receive a file from socket server
			} catch(Exception e) {
			}
		}
		else if(optionNum == 16) {			//Object reference write
			for(int k = 0; k < 10; k++) {
			for(int i = 0; i < N; i++) {
				index = currentIndex[i];
				objArray[index] = objData;
			}
			}
		}
         */ else if (optionNum == 17) {		//int write
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {
                    index = currentIndex[i];
                    intArray[index] = intData;
                }
            }
        }
        if (optionNum == 18) {			//Object reference read
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {
                    index = currentIndex[i];
                    objData = objArray[index];

                }
            }
        } else if (optionNum == 19) {		//Integer reference read
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {
                    index = currentIndex[i];
                    integerData = integerArray[index];
                }
            }
        } else if (optionNum == 20) {		//Int read
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {
                    index = currentIndex[i];
                    //intContainer.get(index).intValue();
                    intData = intArray[index];
                }
            }
        } else if (optionNum == 21) {		//Type query
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {

                    index = currentIndex[i];
                    if (integerArray[index] instanceof Integer) {
                        //	j = ((Integer)objContainer.get(index)).intValue();
                    }
                }
            }
        } else if (optionNum == 22) {		//Value query
            for (int k = 0; k < 10; k++) {
                for (int i = 0; i < N; i++) {
                    index = currentIndex[i];
                    integerData = integerArray[index].intValue();
                }
            }
        } else if (optionNum == 23) {		//grouped/ungrouped array access
//			ArrayTest.ArrayCalc();	
        } else if (optionNum == 24) {
//			PiCalc.intPi();
        } else if (optionNum == 25) {
//			PiCalc.floatPi();
        } else if (optionNum == 26) {
//			PiCalc.doublePi();
        } else if (optionNum == 27) {
//			PiCalc.shortPi();
        } else if (optionNum == 28) {
//			PiCalc.longPi();
        }
    }

    public static void createString() {
        StringBuffer sb = new StringBuffer(stringWrite);
        for (int i = 0; i < N; i++) {
            sb.append("i");
        }
        stringWrite = sb.toString();
    }

    public static void storeRandomIndex() {
        int range = N - 1;
        isRandom = true;
        randomCount++;
        Random ran = new Random();
        switch (randomCount) {
            case 1: {
                break;		//100% N range
            }
            case 2: {
                range /= 4;	//25% N range
                break;
            }
            case 3: {
                range /= 16; //6.s5% of N range
                break;
            }
            case 4: {
                range /= 50; //2% of N range
                break;
            }
            case 5: {
                range /= 100; //1% of N range
                break;
            }
            case 6: {
                range /= 1000; //0.1% of N range
                break;
            }
        }
        for (int i = 0; i < N; i++) {
            int fraction = (int) (range * ran.nextDouble());
            //System.out.println(fraction);
            currentIndex[i] = fraction;
        }
    }

    public static void storeSequentialIndex() {
        isRandom = false;
        for (int i = 0; i < N; i++) {
            currentIndex[i] = i;
        }
    }

    public static void createArray() {

        for (int i = 0; i < N; ++i) {
            objArray[i] = i;
        }
        for (int i = 0; i < N; ++i) {
            intArray[i] = i;
        }
        for (int i = 0; i < N; ++i) {
            integerArray[i] = new Integer(i);
        }
    }

    public static void createContainer() {

        for (int i = 0; i < N; ++i) {
            objContainer.add(i);
        }
        for (int i = 0; i < N; ++i) {
            intContainer.add(i);
        }
        /*
		end = EnergyCheckUtils.EnergyStatCheck();
		timeEnd = System.currentTimeMillis()/1000.0;
		stop = end.split("#");
		start = before.split("#");
		gpu = Double.parseDouble(stop[0]) - Double.parseDouble(start[0]);
		cpu = Double.parseDouble(stop[1]) - Double.parseDouble(start[1]);
		pkg = Double.parseDouble(stop[2]) - Double.parseDouble(start[2]);
		timeUsing = timeEnd - timeStart;
         */
        //System.out.println(gpu + ";" + cpu + ";" + timeUsing + ";" + gpu/timeUsing + ";" + cpu/timeUsing);
    }

    public static double calculateEnergy(double end, double start) {
        double delta = 0;
        delta = end - start;
        if (delta < 0) //If the value is set to be 0 during the measurement, it would be negative
        {
            delta += (double) EnergyCheckUtils.wraparoundValue;
        }

        return delta;
    }

    public static void runTest() throws ParseException {

        double wallClockTimeStart, wallClockTimeEnd;
        int actualLoop;
        int count = 0;
        double delta = 0;
        Double tempUserModeTime = 0.0;
        Double tempCpuTime = 0.0;

        Double umTimeSum = 0.0;
        Double kmTimeSum = 0.0;
        Double cpuTimeSum = 0.0;
        Double wcTimeSum = 0.0;

        Double umTimeWarmupSum = 0.0;
        Double kmTimeWarmupSum = 0.0;
        Double cpuTimeWarmupSum = 0.0;
        Double wcTimeWarmupSum = 0.0;

        double[] cpuEnerSum = new double[PKGNUM];
        double[] gpuEnerSum = new double[PKGNUM];
        double[] pkgEnerSum = new double[PKGNUM];

        double[] cpuEnerWarmupSum = new double[PKGNUM];
        double[] gpuEnerWarmupSum = new double[PKGNUM];
        double[] pkgEnerWarmupSum = new double[PKGNUM];

        String timePreamble = null;
        String timeEpilogue = null;
        String[] timeInfoStart = null;
        String[] timeInfoEnd = null;
        String[] start = null;
        String[] stop = null;
        String[][] loopEnergyStart = new String[PKGNUM][];
        String[][] loopEnergyStop = new String[PKGNUM][];
        String[] sockPreInfo = new String[PKGNUM];
        String[] sockPostInfo = new String[PKGNUM];
        Double timeStart = 0.0;
        Double timeEnd = 0.0;
        Double warmupTimeUsing = 0.0;
        String before = null;
        String loopBefore = null;
        String end = null;
        String loopStop = null;

        double[] wcTime = new double[LOOPNO];
        double[] cpuTime = new double[LOOPNO];
        double[] umTime = new double[LOOPNO];
        double[] kmTime = new double[LOOPNO];
        double[][] gpuEnergy = new double[PKGNUM][LOOPNO];
        double[][] cpuEnergy = new double[PKGNUM][LOOPNO];
        double[][] pkgEnergy = new double[PKGNUM][LOOPNO];

        double[] wcTimeWarmup = new double[LOOPNO];
        double[] cpuTimeWarmup = new double[LOOPNO];
        double[] umTimeWarmup = new double[LOOPNO];
        double[] kmTimeWarmup = new double[LOOPNO];
        double[][] gpuEnergyWarmup = new double[PKGNUM][LOOPNO];
        double[][] cpuEnergyWarmup = new double[PKGNUM][LOOPNO];
        double[][] pkgEnergyWarmup = new double[PKGNUM][LOOPNO];

        double wcTimeSD = 0.0;
        double cpuTimeSD = 0.0;
        double umTimeSD = 0.0;
        double kmTimeSD = 0.0;
        double[] gpuEnerSD = new double[PKGNUM];
        double[] cpuEnerSD = new double[PKGNUM];
        double[] pkgEnerSD = new double[PKGNUM];

        double wcTimeWarmupSD = 0.0;
        double cpuTimeWarmupSD = 0.0;
        double umTimeWarmupSD = 0.0;
        double kmTimeWarmupSD = 0.0;
        double[] gpuEnerWarmupSD = new double[PKGNUM];
        double[] cpuEnerWarmupSD = new double[PKGNUM];
        double[] pkgEnerWarmupSD = new double[PKGNUM];

        double[] gpuEnerPower = new double[PKGNUM];
        double[] cpuEnerPower = new double[PKGNUM];
        double[] pkgEnerPower = new double[PKGNUM];

        DecimalFormat df = new DecimalFormat("#.##");
        //currentIndex = randomIndex;
        //timeStart = System.currentTimeMillis()/1000.0;
        //before = EnergyCheckUtils.EnergyStatCheck();
        /*
		if(optionNum > 6) {
			createString();
		}*/
        //When rendering image, stop looping. Without system-triggered and app-triggered painting, paint() would not be invoked
        {
            if (optionNum == 14 || (optionNum >= 24 && optionNum <= 28)) {
                LOOPNO = 1;
                warmupLoop = 0;
            }
            for (int j = 0; j < LOOPNO; j++) {
                loopBefore = EnergyCheckUtils.EnergyStatCheck();
                wallClockTimeStart = System.currentTimeMillis() / 1000.0;
//				timePreamble = TimeCheckUtils.getCurrentThreadTimeInfo();

                doOption();

//				timeEpilogue= TimeCheckUtils.getCurrentThreadTimeInfo();
                wallClockTimeEnd = System.currentTimeMillis() / 1000.0;
                loopStop = EnergyCheckUtils.EnergyStatCheck();
                //warmup iterations
                if (j < warmupLoop) {
                    /*Only one socket*/
                    if (!loopBefore.contains("@")) {
                        socketNum = 1;

                        loopEnergyStop[0] = loopStop.split("#");
                        loopEnergyStart[0] = loopBefore.split("#");

                    } else {
                        /*Multiple sockets*/
                        sockPreInfo = loopBefore.split("@");

                        socketNum = sockPreInfo.length;

                        for (int i = 0; i < sockPreInfo.length; i++) {
                            loopEnergyStart[i] = sockPreInfo[i].split("#");
                        }
                        sockPostInfo = loopStop.split("@");
                        for (int i = 0; i < sockPostInfo.length; i++) {
                            loopEnergyStop[i] = sockPostInfo[i].split("#");
                        }
                    }

                    for (int i = 0; i < socketNum; i++) {
                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(loopEnergyStop[i][0]);
                        double d = number.doubleValue();
                        Number number2 = format.parse(loopEnergyStart[i][0]);
                        double a = number2.doubleValue();
                        delta = calculateEnergy(d, a);
                        gpuEnergyWarmup[i][j] = delta;//Restore energy information for each loop

                        
                        Number number3 = format.parse(loopEnergyStart[i][0]);
                        double b = number3.doubleValue();
                        Number number4 = format.parse(loopEnergyStart[i][0]);
                        double c = number4.doubleValue();
                        delta = calculateEnergy(b, c);
                        cpuEnergyWarmup[i][j] = delta;

                        
                        Number number5 = format.parse(loopEnergyStart[i][0]);
                        double e = number5.doubleValue();
                        Number number6 = format.parse(loopEnergyStart[i][0]);
                        double f = number6.doubleValue();
                        delta = calculateEnergy(e, f);
                        pkgEnergyWarmup[i][j] = delta;
                    }

//                    timeInfoStart = timePreamble.split("#");	//Split CPU time and user mode time
//                    timeInfoEnd = timeEpilogue.split("#");
//
//                    tempUserModeTime = Double.parseDouble(timeInfoEnd[0]) - Double.parseDouble(timeInfoStart[0]);		//Calcualte time usage	
//                    tempCpuTime = Double.parseDouble(timeInfoEnd[1]) - Double.parseDouble(timeInfoStart[1]);

                    tempUserModeTime /= 1000000000.0;	//Get second
                    tempCpuTime /= 1000000000.0;

                    //tempUserModeTime = Double.valueOf(df.format(tempUserModeTime));	//Round 2 digits
                    //tempCpuTime = Double.valueOf(df.format(tempCpuTime));
                    umTimeWarmup[j] = tempUserModeTime;	//Restore Time info for each loop
                    cpuTimeWarmup[j] = tempCpuTime;
                    kmTimeWarmup[j] = tempCpuTime - tempUserModeTime;
                    wcTimeWarmup[j] = wallClockTimeEnd - wallClockTimeStart;

                } else {
                    /*One Socket*/
                    if (!loopBefore.contains("@")) {
                        loopEnergyStop[0] = loopStop.split("#");
                        loopEnergyStart[0] = loopBefore.split("#");

                    } else {
                        /*Multiple sockets*/
                        sockPreInfo = loopBefore.split("@");
                        for (int i = 0; i < sockPreInfo.length; i++) {
                            loopEnergyStart[i] = sockPreInfo[i].split("#");
                        }
                        sockPostInfo = loopStop.split("@");
                        for (int i = 0; i < sockPostInfo.length; i++) {
                            loopEnergyStop[i] = sockPostInfo[i].split("#");
                        }
                    }
                    for (int i = 0; i < socketNum; i++) {

                        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                        Number number = format.parse(loopEnergyStop[i][0]);
                        double d = number.doubleValue();
                        Number number2 = format.parse(loopEnergyStart[i][0]);
                        double a = number2.doubleValue();
                        delta = calculateEnergy(d, a);
                        gpuEnergy[i][j] = delta;		//Gpu could be Dram, it depends on the 

                        Number number3 = format.parse(loopEnergyStart[i][0]);
                        double b = number3.doubleValue();
                        Number number4 = format.parse(loopEnergyStart[i][0]);
                        double c = number4.doubleValue();
                        delta = calculateEnergy(b, c);
                        cpuEnergy[i][j] = delta;		//architecture

                        Number number5 = format.parse(loopEnergyStart[i][0]);
                        double e = number5.doubleValue();
                        Number number6 = format.parse(loopEnergyStart[i][0]);
                        double f = number6.doubleValue();
                        delta = calculateEnergy(e, f);
                        pkgEnergy[i][j] = delta;
                    }

//                    timeInfoStart = timePreamble.split("#");	//Split CPU time and user mode time
//                    timeInfoEnd = timeEpilogue.split("#");
//
//                    tempUserModeTime = Double.parseDouble(timeInfoEnd[0]) - Double.parseDouble(timeInfoStart[0]);		//Calcualte time usage	
//                    tempCpuTime = Double.parseDouble(timeInfoEnd[1]) - Double.parseDouble(timeInfoStart[1]);

                    tempUserModeTime /= 1000000000.0;	//Get second
                    tempCpuTime /= 1000000000.0;

                    //tempUserModeTime = Double.valueOf(df.format(tempUserModeTime));										//Round 2 digits
                    //tempCpuTime = Double.valueOf(df.format(tempCpuTime));
                    umTime[j] = tempUserModeTime;
                    cpuTime[j] = tempCpuTime;
                    kmTime[j] = tempCpuTime - tempUserModeTime;
                    wcTime[j] = wallClockTimeEnd - wallClockTimeStart;

                }
            }

            actualLoop = LOOPNO - warmupLoop;

            /*Formatted data*/
            for (int k = 0; k < socketNum; k++) {
                for (int j = 0; j < LOOPNO; j++) {
                    if (j < warmupLoop) {

                        gpuEnergyWarmup[k][j] = Double.valueOf(df.format(gpuEnergyWarmup[k][j]));
                        cpuEnergyWarmup[k][j] = Double.valueOf(df.format(cpuEnergyWarmup[k][j]));
                        pkgEnergyWarmup[k][j] = Double.valueOf(df.format(pkgEnergyWarmup[k][j]));
                        cpuTimeWarmup[j] = Double.valueOf(df.format(cpuTimeWarmup[j]));

                        umTimeWarmup[j] = Double.valueOf(df.format(umTimeWarmup[j]));
                        kmTimeWarmup[j] = Double.valueOf(df.format(kmTimeWarmup[j]));
                        wcTimeWarmup[j] = Double.valueOf(df.format(wcTimeWarmup[j]));

                        //		if(gpuEnerWarmupSum[k] != null) {
                        gpuEnerWarmupSum[k] += gpuEnergyWarmup[k][j];		//Restore sum of all loops information
                        cpuEnerWarmupSum[k] += cpuEnergyWarmup[k][j];
                        pkgEnerWarmupSum[k] += pkgEnergyWarmup[k][j];
                        //		} else {
                        //			gpuEnerWarmupSum[k] = gpuEnergyWarmup[k][j];		//Restore sum of all loops information
                        //			cpuEnerWarmupSum[k] = cpuEnergyWarmup[k][j];
                        //			pkgEnerWarmupSum[k] = pkgEnergyWarmup[k][j];

                        //		}
                        umTimeWarmupSum += umTimeWarmup[j];	//Get the sum of user mode time
                        cpuTimeWarmupSum += cpuTimeWarmup[j];	//Get the sum of CPU time
                        kmTimeWarmupSum += (cpuTimeWarmup[j] - umTimeWarmup[j]);		//Get the sum of kernel mode time	
                        wcTimeWarmupSum += wcTimeWarmup[j];	//Get the sum of Wall Clock Time

                    } else {
                        gpuEnergy[k][j] = Double.valueOf(df.format(gpuEnergy[k][j]));	//Round 2 digits
                        cpuEnergy[k][j] = Double.valueOf(df.format(cpuEnergy[k][j]));
                        pkgEnergy[k][j] = Double.valueOf(df.format(pkgEnergy[k][j]));

                        cpuTime[j] = Double.valueOf(df.format(cpuTime[j]));
                        umTime[j] = Double.valueOf(df.format(umTime[j]));
                        kmTime[j] = Double.valueOf(df.format(kmTime[j]));
                        wcTime[j] = Double.valueOf(df.format(wcTime[j]));

                        //if(gpuEnerSum[k] != null) {
                        gpuEnerSum[k] += gpuEnergy[k][j];
                        cpuEnerSum[k] += cpuEnergy[k][j];
                        pkgEnerSum[k] += pkgEnergy[k][j];
                        //} else {
                        //	gpuEnerSum[k] = gpuEnergy[k][j];
                        //	cpuEnerSum[k] = cpuEnergy[k][j];
//							pkgEnerSum[k] = pkgEnergy[k][j];

//						}
                        umTimeSum += umTime[j];		//Get the sum of user mode time
                        cpuTimeSum += cpuTime[j];	//Get the sum of CPU time
                        if (cpuTime[j] - umTime[j] < 0) {
                            kmTimeSum += 0.0;
                        } else {
                            kmTimeSum += (cpuTime[j] - umTime[j]);
                        }
                        wcTimeSum += wcTime[j];		//Get the sum of wall clock time

                    }
                }
            }

            //System.out.println(gpuEnerWarmupSum	 + "  " + cpuEnerWarmupSum + "  " + pkgEnerWarmupSum + "  " + cpuTimeWarmupSum);

            /*Standard deviation calculation*/
            for (int k = 0; k < socketNum; k++) {
                for (int j = 0; j < LOOPNO; j++) {
                    if (j < warmupLoop) {
                        gpuEnerWarmupSD[k] += Math.pow((gpuEnergyWarmup[k][j] - gpuEnerWarmupSum[k] / warmupLoop), 2.0);
                        cpuEnerWarmupSD[k] += Math.pow((cpuEnergyWarmup[k][j] - cpuEnerWarmupSum[k] / warmupLoop), 2.0);
                        pkgEnerWarmupSD[k] += Math.pow((pkgEnergyWarmup[k][j] - pkgEnerWarmupSum[k] / warmupLoop), 2.0);

                        cpuTimeWarmupSD += Math.pow((cpuTimeWarmup[j] - cpuTimeWarmupSum / warmupLoop), 2.0);
                        kmTimeWarmupSD += Math.pow((kmTimeWarmup[j] - kmTimeWarmupSum / warmupLoop), 2.0);
                        umTimeWarmupSD += Math.pow((umTimeWarmup[j] - umTimeWarmupSum / warmupLoop), 2.0);
                        wcTimeWarmupSD += Math.pow((wcTimeWarmup[j] - wcTimeWarmupSum / warmupLoop), 2.0);

                        if (j == (warmupLoop - 1)) {
                            gpuEnerWarmupSD[k] = Math.sqrt(gpuEnerWarmupSD[k] / warmupLoop);	//Caculate standard deviation at the final round 
                            cpuEnerWarmupSD[k] = Math.sqrt(cpuEnerWarmupSD[k] / warmupLoop);					//of warmup loop
                            pkgEnerWarmupSD[k] = Math.sqrt(pkgEnerWarmupSD[k] / warmupLoop);
                            cpuTimeWarmupSD = Math.sqrt(cpuTimeWarmupSD / warmupLoop);
                            umTimeWarmupSD = Math.sqrt(umTimeWarmupSD / warmupLoop);
                            kmTimeWarmupSD = Math.sqrt(kmTimeWarmupSD / warmupLoop);
                            wcTimeWarmupSD = Math.sqrt(wcTimeWarmupSD / warmupLoop);
                        }

                    } else {
                        gpuEnerSD[k] += Math.pow((gpuEnergy[k][j] - gpuEnerSum[k] / actualLoop), 2.0);
                        cpuEnerSD[k] += Math.pow((cpuEnergy[k][j] - cpuEnerSum[k] / actualLoop), 2.0);
                        pkgEnerSD[k] += Math.pow((pkgEnergy[k][j] - pkgEnerSum[k] / actualLoop), 2.0);

                        cpuTimeSD += Math.pow((cpuTime[j] - cpuTimeSum / actualLoop), 2.0);
                        kmTimeSD += Math.pow((kmTime[j] - kmTimeSum / actualLoop), 2.0);
                        umTimeSD += Math.pow((umTime[j] - umTimeSum / actualLoop), 2.0);
                        wcTimeSD += Math.pow((wcTime[j] - wcTimeSum / actualLoop), 2.0);

                        if (j == LOOPNO - 1) {
                            gpuEnerSD[k] = Math.sqrt(gpuEnerSD[k] / actualLoop);	//Caculate standard deviation at the final round 
                            cpuEnerSD[k] = Math.sqrt(cpuEnerSD[k] / actualLoop);	//of measurement loop 
                            pkgEnerSD[k] = Math.sqrt(pkgEnerSD[k] / actualLoop);
                            cpuTimeSD = Math.sqrt(cpuTimeSD / actualLoop);
                            umTimeSD = Math.sqrt(umTimeSD / actualLoop);
                            kmTimeSD = Math.sqrt(kmTimeSD / actualLoop);
                            wcTimeSD = Math.sqrt(wcTimeSD / actualLoop);
                        }
                    }
                }

                /**
                 * ****Round standard deviation*******
                 */
                gpuEnerSD[k] = Double.valueOf(df.format(gpuEnerSD[k]));	//Round 2 digits
                cpuEnerSD[k] = Double.valueOf(df.format(cpuEnerSD[k]));
                pkgEnerSD[k] = Double.valueOf(df.format(pkgEnerSD[k]));
                cpuTimeSD = Double.valueOf(df.format(cpuTimeSD));
                umTimeSD = Double.valueOf(df.format(umTimeSD));
                kmTimeSD = Double.valueOf(df.format(kmTimeSD));
                wcTimeSD = Double.valueOf(df.format(wcTimeSD));

                gpuEnerWarmupSD[k] = Double.valueOf(df.format(gpuEnerWarmupSD[k]));	//Round 2 digits
                cpuEnerWarmupSD[k] = Double.valueOf(df.format(cpuEnerWarmupSD[k]));
                pkgEnerWarmupSD[k] = Double.valueOf(df.format(pkgEnerWarmupSD[k]));
                cpuTimeWarmupSD = Double.valueOf(df.format(cpuTimeWarmupSD));
                umTimeWarmupSD = Double.valueOf(df.format(umTimeWarmupSD));
                kmTimeWarmupSD = Double.valueOf(df.format(kmTimeWarmupSD));
                wcTimeWarmupSD = Double.valueOf(df.format(wcTimeWarmupSD));

//                gpuEnerPower[k] = Double.valueOf(df.format(gpuEnerSum[k] / wcTimeSum));
//                cpuEnerPower[k] = Double.valueOf(df.format(cpuEnerSum[k] / wcTimeSum));
//                pkgEnerPower[k] = Double.valueOf(df.format(pkgEnerSum[k] / wcTimeSum));
            }

            /*Print title*/
 /*
		System.out.print("Frequency,OptionNum,WallClockTime,CpuTime,UserModeTime,KernelModeTime");
		for(int i = 0; i < socketNum; i++) {
			//String str = String.format("socket%d", i);
			System.out.print(",DramEnergy" + i + "," + "CPUEnergy" + i + "," + "PackageEnergy" + i + "," 
								+ "DramPower" + i + "," + "CPUPower" + i + "," + "PackagePower" + i); 
		}

		System.out.print(",WallClockTimeStandardDeviation,CpuTimeStandardDeviation,UserModeTimeStandardDeviation,KernelModeTimeStandardDeviation");

		for(int i = 0; i < socketNum; i++) {
			//String str = String.format("socket%d", i);
			System.out.print(",CpuEnergyStandardDeviation" + i + "," + "DramEnergyStandardDeviation" + i + "," + "PackageEnergyStandardDeviation" + i);
		}

		System.out.print(",WarmpupWallClockTimeStandardDeviation,WarmpupCpuTimeStandardDeviation,WarmpupUserModeTimeStandardDeviation,WarmpupKernelModeTimeStandardDeviation");

		for(int i = 0; i < socketNum; i++) {
			//String str = String.format("socket%d", i);
			if(i + 1 == socketNum) {
				System.out.println(",WarmupCpuEnergyStandardDeviation" + i + "," + "WarmupDramEnergyStandardDeviation" + i + ","
									 + "WarmupPackageEnergyStandardDeviation" + i); 
			} else {
				System.out.print(",WarmupCpuEnergyStandardDeviation" + i + "," + "WarmupDramEnergyStandardDeviation" + i + "," 
									+ "WarmupPackageEnergyStandardDeviation" + i); 
			}
		}
             */
            /**
             * **Time and Energy information***
             */
            if (isArrayAccess) {
                if (isRandom) {
                    switch (randomCount) {
                        case 1: {
                            System.out.print(frequency + ",option" + optionNum + "-r_100%,");
                            break;
                        }
                        case 2: {
                            System.out.print(frequency + ",option" + optionNum + "-r_25%,");
                            break;
                        }
                        case 3: {
                            System.out.print(frequency + ",option" + optionNum + "-r_6.25%,");
                            break;
                        }
                        case 4: {
                            System.out.print(frequency + ",option" + optionNum + "-r_2%,");
                            break;
                        }
                        case 5: {
                            System.out.print(frequency + ",option" + optionNum + "-r_1%,");
                            break;
                        }
                        case 6: {
                            System.out.print(frequency + ",option" + optionNum + "-r_0.1%,");
                            break;
                        }
                    }
                } else {
                    System.out.print(frequency + ",option" + optionNum + ",");
                }
            } else {
//				if(ArrayTest.isGrouped) {
//					System.out.print(frequency + ",option" + optionNum + "-grouped,");	
//				} else {
//					System.out.print(frequency + ",option" + optionNum + "-ungrouped,");	
//				}
            }

            System.out.print(Double.valueOf(df.format(wcTimeSum / actualLoop)) + ","
                    + Double.valueOf(df.format(cpuTimeSum / actualLoop)) + "," + Double.valueOf(df.format(umTimeSum / actualLoop))
                    + "," + Double.valueOf(df.format(kmTimeSum / actualLoop)));

            for (int i = 0; i < socketNum; i++) {
                System.out.print("," + Double.valueOf(df.format(gpuEnerSum[i] / actualLoop)) + ","
                        + Double.valueOf(df.format(cpuEnerSum[i] / actualLoop)) + "," + Double.valueOf(df.format(pkgEnerSum[i] / actualLoop)) + ",");	//Power information
                if (wcTimeSum != 0.0) {
                    System.out.print(gpuEnerPower[i] + "," + cpuEnerPower[i] + "," + pkgEnerPower[i]);
                } else {
                    System.out.print("0.00," + "0.00," + "0.00");
                }
            }

            /**
             * **Standard Deviation information for measurement loop***
             */
            System.out.print("," + wcTimeSD + "," + cpuTimeSD + "," + umTimeSD + "," + kmTimeSD);

            for (int i = 0; i < socketNum; i++) {
                System.out.print("," + cpuEnerSD[i] + ","
                        + gpuEnerSD[i] + "," + pkgEnerSD[i]);
            }

            /**
             * **Srandard Deviation information for warmup loop****
             */
            System.out.print("," + wcTimeWarmupSD + "," + cpuTimeWarmupSD + "," + umTimeWarmupSD + "," + kmTimeWarmupSD);
            for (int i = 0; i < socketNum; i++) {
                if (i + 1 == socketNum) {
                    System.out.println("," + cpuEnerWarmupSD[i] + "," + gpuEnerWarmupSD[i] + "," + pkgEnerWarmupSD[i]);
                } else {
                    System.out.print("," + cpuEnerWarmupSD[i] + "," + gpuEnerWarmupSD[i] + "," + pkgEnerWarmupSD[i]);
                }
            }
        }

        /*
		if(optionNum == 12 || optionNum == 13) {	//12. Draw Multiple random lines, 13. Draw Image
			testFrame.add("Center", imgBuilder);
		} 
         */
    }

    public static void main(String[] argv) throws ParseException {
        int freq;
        option = "14";
        //freq = Integer.parseInt(argv[1]);
        //frequency = freq / 1000000.0;
        DecimalFormat df = new DecimalFormat("#.##");
        frequency = Double.valueOf(df.format(frequency));
        optionNum = Integer.parseInt(option);

        //data1 *= 2;

        /*
		timeStart = System.currentTimeMillis()/1000.0;
		before = EnergyCheckUtils.EnergyStatCheck();
         */
 /*
		if(optionNum == 12 || optionNum == 13) {
			testFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {System.exit(0);}
			});
        		URL imageSrc;
			//TODO: dependency
			try {
             	imageSrc = ((new File("/home/kenan/energy/" + ImageApplet.imageFileName)).toURI()).toURL();
				imgBuilder = new ImageApplet(imageSrc);
				imgBuilder.buildUI();

			} catch (MalformedURLException e) {
				System.out.println("excpetion happened!");
			}
						//RenderImage[] img = imgBuilder.getImage();
			
		} else */
 /*
		if(optionNum == 15) {
			try {
				//Runtime.getRuntime().exec("java SocketSender");
				//SocketReceiver.basePath = System.getProperty("user.dir");
				SocketReceiver.conn = new Socket("127.0.0.1", 8989);
				SocketReceiver.streamReader = new DataInputStream(SocketReceiver.conn.getInputStream());
				SocketReceiver.buf = new byte[4092];
				SocketReceiver.basePath = "/home/kenan/energy/";
			//	String[] targetName = {"/targetFile1","/targetFile2","/targetFile3","/targetFile4","/targetFile5"};
				SocketReceiver.fileCount = SocketReceiver.streamReader.readInt();	
				SocketReceiver.setFileWriter(SocketReceiver.fileCount);
				//SocketReceiver.fileSize = SocketReceiver.streamReader.readLong();
				for(int i = 0; i < SocketReceiver.fileCount; i++) {
					//System.out.println("target file name: " + SocketReceiver.streamReader.readUTF());
					File targetFile = new File(SocketReceiver.basePath + SocketReceiver.streamReader.readUTF());
					SocketReceiver.targetFiles.add(targetFile);	
				}
				for(int i = 0; i < SocketReceiver.fileCount; i++) {
					//System.out.println("target file name: " + SocketReceiver.targetFiles.get(i).getName());
				}
				/*	
				String[] targetName = new String[SocketReceiver.fileCount];
				for(int i = 0; i < TARGET_FILE_NUM; i++) {
					targetName[i] = "/targetFile" + Integer.toString(i);
					//System.out.println(targetName[i]);
				}
         */
 /*
				for(int i = 0; i < SocketReceiver.fileCount; i++) {
					//DataOutputStream writer = new DataOutputStream(new FileOutputStream(SocketReceiver.basePath + SocketReceiver.streamReader.readUTF()));
					//SocketReceiver.fileWriter.add(new DataOutputStream(new FileOutputStream(SocketReceiver.basePath + targetName[i])));
					SocketReceiver.fileWriter[i] = new DataOutputStream(new FileOutputStream(SocketReceiver.basePath + SocketReceiver.targetFiles.get(i).getName()));
				}
				
				
				//SocketReceiver.fileWriter = new DataOutputStream(new FileOutputStream(SocketReceiver.basePath + "/targetFile"));
			} catch(IOException ioe) {
				System.out.println("IO Exception in receiver part");
				System.exit(0);
			}
			
		
		}*/
        if (optionNum < 7) {
            isArrayAccess = true;
            createContainer();
            storeSequentialIndex();
            runTest();
            for (int i = 0; i < 6; i++) {
                storeRandomIndex();
                runTest();
            }
        } else if (optionNum > 16 && optionNum < 23) {
            isArrayAccess = true;
            if (optionNum == 19) {
                createContainer();
            }
            createArray();

            storeSequentialIndex();
            runTest();
            for (int i = 0; i < 6; i++) {
                storeRandomIndex();
                runTest();
            }
        } else if (optionNum >= 7 && optionNum <= 16) {
            if (optionNum >= 12 && optionNum <= 16) {
                MatMultc.createMat(optionNum);
            }

            /*
			if(optionNum == 12 || optionNum == 13 || optionNum == 14)
				MatMultc.createMat(optionNum);
			if(optionNum == 14) {
				runTest();
			}
             */
            runTest();
        } else if (optionNum == 23) {
//			ArrayTest.arrayInit();
//			ArrayTest.isGrouped = true;
            runTest();
//			ArrayTest.isGrouped = false;
            runTest();
        } else if (optionNum >= 24 && optionNum <= 28) {
            runTest();
        }
        /*
		if(optionNum == 12 || optionNum == 13) {
			testFrame.pack();
			testFrame.setVisible(true);				
		}
         */
    }
}
