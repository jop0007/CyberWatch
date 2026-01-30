package Trafico;

import Logs.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 *
 * @author Valentin
 */
public class GeneradorTrafico {
    public void generarArchivo(String ruta, int cantidad) {
        Log.registrar("RED", "Generando archivo de trafico...");
        try {
            //creo el archivo de trafico
            File archivo = new File(ruta);
            FileWriter fw = new FileWriter(archivo);
            BufferedWriter bw = new BufferedWriter(fw);
            Random random = new Random();
            //configuracion inicial del tiempo
            int hora = 14;
            int minuto = 30;
            int segundo = 0;
            int frame = 1;
            String protocolo = "TCP";
            //genero las lineas de trafico
            for (int i = 0; i < cantidad; i++) {
                String tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                String ipOrigen = "192.168.1." + (100 + random.nextInt(5));
                String ipDestino = "8.8.8.8";
                int puerto;
                int bytes = 500 + random.nextInt(1500);
                // genero trafico normal con puertos comunes
                int tipoPuerto = random.nextInt(100);
                if (tipoPuerto < 30) {
                    puerto = 80;  //HTTP
                } else if (tipoPuerto < 55) {
                    puerto = 443;  //HTTPS
                } else if (tipoPuerto < 70) {
                    puerto = 22;  //SSH
                } else if (tipoPuerto < 80) {
                    puerto = 53;  //DNS
                } else if (tipoPuerto < 90) {
                    puerto = 3306;  //MySQL
                } else {
                    puerto = 8080; //HTTP alternativo
                }
                //inserto puertos sospechosos cada 5 lineas
                if(i % 5 == 0 && i != 0) {
                    ipOrigen = "10.0.0.5";
                    ipDestino = "192.168.1.100";
                    //elijo un puerto sospechoso aleatorio
                    int cual = random.nextInt(5);
                    if (cual == 0) puerto = 31337;
                    else if (cual == 1) puerto = 4444;
                    else if (cual == 2) puerto = 1337;
                    else if (cual == 3) puerto = 6667;
                    else puerto = 12345;
                }
                //escribo la linea en el archivo
                String linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + puerto + " " + bytes;
                bw.write(linea);
                bw.newLine();
                frame++;
                //genero 6 conexiones seguidas con la misma IP en la posicion 10
                if (i == 10) {
                    ipOrigen = "45.123.67.89";
                    //creo 6 lineas consecutivas con la misma IP
                    for (int j = 0; j < 6; j++) {
                        segundo++;
                        tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                        ipDestino = "192.168.1." + (100 + random.nextInt(4));
                        puerto = 22 + (j * 10);
                        linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + puerto + " " + bytes;
                        bw.write(linea);
                        bw.newLine();
                        frame++;
                    }
                }
                //genero 4 conexiones en el mismo segundo en la posicion 20
                if (i == 20) {
                    ipOrigen = "203.0.113.42";
                    ipDestino = "192.168.1.100";
                    //creo 4 lineas con el mismo tiempo
                    for (int j = 0; j < 4; j++) {
                        tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                        linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + 80 + " " + bytes;
                        bw.write(linea);
                        bw.newLine();
                        frame++;
                    }
                }
                segundo++;
                //ajusto minutos y horas si es necesario
                if (segundo >= 60) {
                    segundo = segundo - 60;
                    minuto++;
                }
                if (minuto >= 60) {
                    minuto = 0;
                    hora++;
                }
            }
            bw.close();
            Log.registrar("RED", "Archivo generado con " + (frame - 1) + " frames");
        } catch (Exception e) {
        }
    }
}