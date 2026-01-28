package Trafico;

import Logs.Log;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class GeneradorTrafico {
    
    public void generarArchivo(String ruta, int cantidad) {
        Log.registrar("TRAFICO", "Generando archivo...");
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ruta));
            Random random = new Random();
            
            int hora = 14;
            int minuto = 30;
            int segundo = 0;
            int frame = 1;  // contador de frames
            
            for (int i = 0; i < cantidad; i++) {
                String tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                String ipOrigen = "192.168.1." + (100 + random.nextInt(4));
                String ipDestino = "8.8.8.8";
                String protocolo = "TCP";
                int puerto = 443;
                int bytes = 500 + random.nextInt(1500);
                
                // trafico normal
                int tipo = random.nextInt(100);
                if (tipo < 50) {
                    puerto = 80;
                } else if (tipo < 90) {
                    puerto = 443;
                }
                
                // anomalia 1: puertos sospechosos
                if (i % 5 == 0 && i > 0) {
                    ipOrigen = "10.0.0.5";
                    ipDestino = "192.168.1.100";
                    
                    int cual = random.nextInt(5);
                    if (cual == 0) puerto = 31337;
                    else if (cual == 1) puerto = 4444;
                    else if (cual == 2) puerto = 1337;
                    else if (cual == 3) puerto = 6667;
                    else puerto = 12345;
                }
                
                // escribo la linea CON FRAME
                String linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + puerto + " " + bytes;
                writer.write(linea);
                writer.newLine();
                frame++;
                
                // anomalia 2: ips repetidas
                if (i == 10) {
                    ipOrigen = "45.123.67.89";
                    for (int j = 0; j < 6; j++) {
                        segundo++;
                        tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                        ipDestino = "192.168.1." + (100 + random.nextInt(4));
                        puerto = 22 + (j * 10);
                        linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + puerto + " " + bytes;
                        writer.write(linea);
                        writer.newLine();
                        frame++;
                    }
                }
                
                // anomalia 3: muchas conexiones en poco tiempo
                if (i == 20) {
                    ipOrigen = "203.0.113.42";
                    ipDestino = "192.168.1.100";
                    for (int j = 0; j < 4; j++) {
                        tiempo = String.format("[%02d:%02d:%02d]", hora, minuto, segundo);
                        linea = "Frame " + frame + " " + tiempo + " " + ipOrigen + " " + ipDestino + " " + protocolo + " " + 80 + " " + bytes;
                        writer.write(linea);
                        writer.newLine();
                        frame++;
                    }
                }
                
                // siguiente segundo
                segundo++;
                
                // ajustar tiempo
                if (segundo >= 60) {
                    segundo = segundo - 60;
                    minuto++;
                }
                if (minuto >= 60) {
                    minuto = 0;
                    hora++;
                }
            }
            
            writer.close();
            
            Log.registrar("TRAFICO", "Archivo generado: " + (frame - 1) + " frames");
            
        } catch (Exception e) {
            Log.registrar("TRAFICO", "Error: " + e.getMessage());
        }
    }
}