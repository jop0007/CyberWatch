package Trafico;

import Logs.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Valentin
 */
public class AnalizadorTrafico {
    public void analizarArchivo(String ruta) {
        Log.registrar("RED", "Analizando trafico...");
        int totalPaquetes = 0;
        int anomalias = 0;
        //guardo la ip anterior para compararla con la actual
        String ipAnterior = "";
        int vecesRepetida = 0;
        //guardo el tiempo anterior para detectar conexiones simultaneas
        String tiempoAnterior = "";
        try {
            //abro el archivo de trafico
            File archivo = new File(ruta);
            FileReader fr = new FileReader(archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea;
            //leo el archivo linea por linea
            while ((linea = br.readLine()) != null) {
                totalPaquetes++;
                //separo la linea por espacios para extraer los datos
                String[] partes = linea.split(" ");
                // extraigo cada dato de su posicion
                // formato: Frame 1 [14:30:01] 192.168.1.100 8.8.8.8 TCP 443 1024
                String frame = partes[1];
                String tiempo = partes[2];
                String ipOrigen = partes[3];
                String ipDestino = partes[4];
                String puerto = partes[6];
                //busco puertos sospechosos
                //comparo el puerto con los que se usan para malware
                if (puerto.equals("31337") || puerto.equals("4444") || 
                    puerto.equals("1337") || puerto.equals("6667") || 
                    puerto.equals("12345")) {
                    Log.registrar("RED", "Frame " + frame + ": Conexion sospechosa detectada desde " + ipOrigen + " al puerto " + puerto);
                    anomalias++;
                }
                
                //detecto si la misma ip se repite muchas veces seguidas
                if (ipOrigen.equals(ipAnterior)) {
                    //la ip es la misma que la anterior
                    vecesRepetida++;
                    //si llega a 5 repeticiones es un escaneo
                    if (vecesRepetida == 5) {
                        Log.registrar("RED", "Frame " + frame + ": " + ipOrigen + " (5+ conexiones consecutivas)");
                        anomalias++;
                    }
                } else {
                    //la ip cambio, reinicio el contador
                    vecesRepetida = 1;
                }
                //actualizo la ip anterior para la siguiente comparacion
                ipAnterior = ipOrigen;
                //detecto si hay varias conexiones en el mismo segundo
                if (tiempo.equals(tiempoAnterior)) {
                    Log.registrar("RED", "Frame " + frame + ": " + ipOrigen + " multiples conexiones en el mismo segundo");
                    anomalias++;
                }
                //actualizo el tiempo anterior para la siguiente comparacion
                tiempoAnterior = tiempo;
            }
            br.close();
            Log.registrar("RED", "Analisis completado: " + totalPaquetes + " frames analizados");
            Log.registrar("RED", "Anomalias detectadas: " + anomalias);
        } catch (Exception e) {
        }
    }
}