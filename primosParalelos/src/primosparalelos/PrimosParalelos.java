package primosparalelos;

import java.util.ArrayList;
import java.util.Scanner;
import com.opencsv.CSVWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Ignacio Rivera F
 */
public class PrimosParalelos {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
         // variable usadas para calcular el tiempo de ejecucion
        long tInicio = 0;
        long tFin = 0;
         // almacena el tiempo de ejecucion de cada muestra
        List<String[]> listaTiemos = new ArrayList<String[]>();
        // encabezado
        listaTiemos.add("n hilos;milisegundos;speed Up;cantidad de numeros primos".split(";"));
        System.out.println("Ingrese el numero maximo de hilos");
        Scanner sc = new Scanner(System.in);

        int cantHilosMax = sc.nextInt();

        if (cantHilosMax < 1 || cantHilosMax > 2000000) {
            System.err.println("la cantidad de hilos solicitada es invaida");
            System.exit(0);
        }
        long timeOne = 0;
         // muestreo para 1 ha n hilos  
        for (int j = 1; j <= cantHilosMax; j++) {
             // clase paralelizable
            Primo[] vectorPrimos = new Primo[j];
            Thread[] vecThread = new Thread[j];
             // se definen las particiones del problema segun la cantidad de hilos de la muestra
            for (int i = 0; i < vectorPrimos.length; i++) {
                vectorPrimos[i] = new Primo((int)((i * (100000.0 / j))+1.0), (int)((i + 1.0) * (100000.0 / j)));
                vecThread[i] = new Thread(vectorPrimos[i]);
            }
            //inicio de la ejecucion en paralelo
            tInicio = System.currentTimeMillis();
            for (int i = 0; i < vecThread.length; i++) {
                vecThread[i].start();
            }
            for (int i = 0; i < vecThread.length; i++) {
                vecThread[i].join();
            }
            // fin de la ejecucion en paralelo
            tFin = System.currentTimeMillis(); 
            long sumaCantPrimos = 0;
            for (int i = 0; i < vectorPrimos.length; i++) {
                sumaCantPrimos += vectorPrimos[i].cantPirmos;
                
            }
            
            if(j == 1){
            timeOne = (tFin - tInicio);
            }
            double speedUP =(((double)timeOne)/(tFin - tInicio));
            DecimalFormat BE_DF = (DecimalFormat)DecimalFormat.getNumberInstance(Locale.GERMAN);
            String muestra = j + ";" + (tFin - tInicio)+";"+BE_DF.format(speedUP)+";"+sumaCantPrimos;
            listaTiemos.add(muestra.split(";"));

        }

        CSVWriter writer = null;

        try {

            writer = new CSVWriter(new FileWriter("muestras.csv"), ';');
            // se exportan los datos en un archivo .csv
            writer.writeAll(listaTiemos);
            writer.close();
        } catch (Exception e) {
            System.err.println("fallo la escritura del archivo muestras.csv");
            return;
        }

        try {
            File archivo = new File("muestras.csv");
            Desktop.getDesktop().open(archivo);
            File grafico = new File("Grafico.xlsx");
            //se abre un archivo excel conectado al archivo .csv
            Desktop.getDesktop().open(grafico);

        } catch (Exception e) {
        }

    }

}

class Primo implements Runnable {

    private int starN;
    private int terminoN;
    public long cantPirmos;

    public Primo(int s, int t) {
        this.starN = s; //inicio de la particion del problema
        this.terminoN = t; // fin de la particion del problema
        this.cantPirmos = 0;
    }

    @Override
    public void run() {
        for (int i = this.starN; i <= this.terminoN; i++) {
            if(esPrimo(i)){
            this.cantPirmos++;
            }
        }
    }

    /**
     * evalua si un numero entero recibido por parametro es primo
     *
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
