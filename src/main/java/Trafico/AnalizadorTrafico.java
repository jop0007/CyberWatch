package Trafico;

import Logs.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorTrafico {
    
    public int analizarArchivo(String ruta) {
        Log.registrar("TRAFICO", "Analizando trafico...");
        
        int totalPaquetes = 0;
        int anomalias = 0;
        
        // variables para comparar
        String ipAnterior = "";
        int vecesRepetida = 0;
        int segundoAnterior = -1;
        
        // regex para detectar puertos sospechosos
        Pattern patronPuertosSospechosos = Pattern.compile(".* (31337|4444|1337|6667|12345) .*");
        
        // regex para capturar frame, tiempo, ips y puerto
        // Frame 1 [14:30:05] 192.168.1.100 8.8.8.8 TCP 443 1024
        Pattern patronCompleto = Pattern.compile("Frame (\\d+) \\[(\\d{2}):(\\d{2}):(\\d{2})\\] (\\S+) (\\S+) \\w+ (\\d+) \\d+");
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(ruta));
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                totalPaquetes++;
                
                // capturo todos los datos de la linea
                Matcher matcherCompleto = patronCompleto.matcher(linea);
                
                if (matcherCompleto.find()) {
                    int frame = Integer.parseInt(matcherCompleto.group(1));
                    // grupo 2 = hora
                    // grupo 3 = minuto
                    int segundo = Integer.parseInt(matcherCompleto.group(4));  // grupo 4 = segundo
                    String ipOrigen = matcherCompleto.group(5);  // grupo 5 = ip origen
                    String ipDestino = matcherCompleto.group(6);  // grupo 6 = ip destino
                    String puerto = matcherCompleto.group(7);  // grupo 7 = puerto
                    
                    // ===================================================================
                    // DETECCION 1: PUERTOS SOSPECHOSOS
                    // ===================================================================
                    Matcher matcherPuerto = patronPuertosSospechosos.matcher(linea);
                    if (matcherPuerto.matches()) {
                        Log.registrar("TRAFICO", "Frame " + frame + ": Conexion sospechosa detectada desde " + ipOrigen + " al puerto " + puerto);
                        anomalias++;
                    }
                    
                    // ===================================================================
                    // DETECCION 2: IPS REPETIDAS
                    // ===================================================================
                    if (ipOrigen.equals(ipAnterior)) {
                        vecesRepetida++;
                        if (vecesRepetida == 5) {
                            Log.registrar("TRAFICO", "Frame " + frame + ": Escaneo detectado desde " + ipOrigen + " (5+ conexiones consecutivas)");
                            anomalias++;
                        }
                    } else {
                        vecesRepetida = 0;
                    }
                    ipAnterior = ipOrigen;
                    
                    // ===================================================================
                    // DETECCION 3: INTERVALOS DE TIEMPO ANOMALOS
                    // ===================================================================
                    if (segundoAnterior != -1) {
                        int diferencia = segundo - segundoAnterior;
                        if (diferencia <= 0) {
                            Log.registrar("TRAFICO", "Frame " + frame + ": Trafico anomalo desde " + ipOrigen + " (multiples conexiones en el mismo segundo)");
                            anomalias++;
                        }
                    }
                    segundoAnterior = segundo;
                }
            }
            
            reader.close();
            
            Log.registrar("TRAFICO", "Analisis completado: " + totalPaquetes + " frames analizados");
            Log.registrar("TRAFICO", "Anomalias detectadas: " + anomalias);
            
        } catch (Exception e) {
            Log.registrar("TRAFICO", "Error: " + e.getMessage());
        }
        
        return anomalias;
    }
}