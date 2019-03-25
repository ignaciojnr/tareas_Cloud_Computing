
package primosparalelos;

import java.util.ArrayList;
import java.util.Scanner;
import com.opencsv.CSVWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author Ignacio Rivera F
 */
public class PrimosParalelos {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
       
        long tInicio, tFin; // variable usadas para calcular el tiempo de ejecucion
        ArrayList<String[]> listaTiemos = new ArrayList<String[]>(); // almacena el tiempo de ejecucion de cada muestra
        listaTiemos.add("n° hilos;milisegundos".split(";"));// encabezado
        System.out.println("Ingrese el numero maximo de hilos");
        Scanner sc = new Scanner(System.in);

        int cantHilosMax = sc.nextInt();

        if (cantHilosMax < 1 || cantHilosMax > 2000000) {
            System.err.println("la cantidad de hilos solicitada es invaida");
            System.exit(0);
        }
     

            for (int j = 1; j <= cantHilosMax; j++) { // muestreo para 1 ha n hilos  
                Primo[] vectorPrimos = new Primo[j]; // clase paralelizable
                Thread[] vecThread = new Thread[j]; 
                for (int i = 0; i < vectorPrimos.length; i++) { // se definen las particiones del problema segun la cantidad de hilos de la muestra
                    vectorPrimos[i] = new Primo(i * (20000 / j), i + 1 * (20000 / j));
                    vecThread[i] = new Thread(vectorPrimos[i]);
                }
                tInicio = System.currentTimeMillis();//inicio de la ejecucion en paralelo
                for (int i = 0; i < vecThread.length; i++) {
                    vecThread[i].start();
                }
                for (int i = 0; i < vecThread.length; i++) {
                    vecThread[i].join();
                }
                tFin = System.currentTimeMillis(); // fin de la ejecucion en paralelo
                String muestra = j + ";" + (tFin - tInicio);
                listaTiemos.add(muestra.split(";"));

            }

     

        CSVWriter writer = null;

        try {

            writer = new CSVWriter(new FileWriter("muestras.csv"), ';');
            writer.writeAll(listaTiemos);// se exportan los datos en un archivo .csv
            writer.close();
        } catch (Exception e) {
            System.err.println("fallo la escritura del archivo muestras.csv");
            return;
        }

        try {
            File archivo = new File("muestras.csv");
            Desktop.getDesktop().open(archivo);
            File grafico = new File("Grafico.xlsx");
            Desktop.getDesktop().open(grafico);//se abre un archivo excel conectado al archivo .csv

        } catch (Exception e) {
        }

    }

}

 class Primo implements Runnable {


    int starN, terminoN;

    public Primo(int s, int t) {
        this.starN = s; //inicio de la particion del problema
        this.terminoN = t; // fin de la particion del problema

    }

    @Override
    public void run() {
        for (int i = this.starN; i < this.terminoN; i++) {
            boolean primo = esPrimo(i);
        }
    }
/**
 * evalua si un numero entero recibido por parametro es primo 
 * @param n
 * @return 
 */
    private boolean esPrimo(int n) {

        if (n < 2) {
            return false;
        }
        for (int i = n - 1; i > 1; i--) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;

    }

}
