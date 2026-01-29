package Trafico;

import Logs.Log;
import java.io.BufferedReader;
import java.io.FileReader;

public class AnalizadorTrafico {
    
    public int analizarArchivo(String ruta) {
        Log.registrar("RED", "Analizando trafico...");
        
        int totalPaquetes = 0;
        int anomalias = 0;
        
        // guardo la ip anterior para compararla
        String ipAnterior = "";
        int vecesRepetida = 0;
        // guardo el tiempo anterior
        String tiempoAnterior = "";
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ruta));
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                totalPaquetes++;
                
                // separo la linea por espacios
                String[] partes = linea.split(" ");
                
                // saco los datos que necesito de cada posicion
                // la linea es: Frame 1 [14:30:01] 192.168.1.100 8.8.8.8 TCP 443 1024
                String frame = partes[1];
                String tiempo = partes[2];
                String ipOrigen = partes[3];
                String ipDestino = partes[4];
                String puerto = partes[6];
                
                // busco puertos sospechosos
                if (puerto.equals("31337") || puerto.equals("4444") || 
                    puerto.equals("1337") || puerto.equals("6667") || 
                    puerto.equals("12345")) {
                    
                    Log.registrar("RED", "Frame " + frame + 
                                 ": Conexion sospechosa detectada desde " + 
                                 ipOrigen + " al puerto " + puerto);
                    anomalias++;
                }
                
                // detecto si la misma ip se repite muchas veces seguidas
                if (ipOrigen.equals(ipAnterior)) {
                    vecesRepetida++;
                    // si se repite 5 veces es un escaneo
                    if (vecesRepetida == 5) {
                        Log.registrar("RED", "Frame " + frame + 
                                     ": Escaneo detectado desde " + ipOrigen + 
                                     " (5+ conexiones consecutivas)");
                        anomalias++;
                    }
                } else {
                    vecesRepetida = 1;
                }
                ipAnterior = ipOrigen;
                
                // detecto si hay varias conexiones en el mismo segundo
                if (!tiempoAnterior.equals("")) {
                    if (tiempo.equals(tiempoAnterior)) {
                        Log.registrar("RED", "Frame " + frame + 
                                     ": Trafico anomalo desde " + ipOrigen + 
                                     " (multiples conexiones en el mismo segundo)");
                        anomalias++;
                    }
                }
                tiempoAnterior = tiempo;
            }
            
            reader.close();
            
            Log.registrar("RED", "Analisis completado: " + totalPaquetes + " frames analizados");
            Log.registrar("RED", "Anomalias detectadas: " + anomalias);
            
        } catch (Exception e) {
            Log.registrar("RED", "Error al analizar: " + e.getMessage());
        }
        
        return anomalias;
    }
}