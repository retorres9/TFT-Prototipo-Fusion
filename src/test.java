import Clases.Data;
/**
 * @see https://stackoverflow.com/a/15207445/230513
 */
public class test {

    public static void main(String[] args) {
        Data date = new Data();
        String fecha = date.getExecutionTime();
        System.out.println(fecha);
        System.out.println(date.getAppName("/home/kasj/Downloads/Calculadora.jar"));
    }
}