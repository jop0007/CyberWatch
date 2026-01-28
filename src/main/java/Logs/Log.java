package Logs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

public class Log {
    // constante con el nombre del archivo de logs
    private static final String archivo = "log_sdas.txt";
    // formato de fecha y hora para los logs
    private static final SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
    
    // jtextareas para mostrar los logs en la interfaz
    private static JTextArea textAreaIntegridad = null;
    private static JTextArea textAreaTrafico = null;
    private static JTextArea textAreaProcesos = null;
    
    // metodo para configurar los jtextareas desde la interfaz
    public static void setTextAreas(JTextArea integridad, JTextArea trafico, JTextArea procesos) {
        textAreaIntegridad = integridad;
        textAreaTrafico = trafico;
        textAreaProcesos = procesos;
    }
    
    public static synchronized void registrar(String tipo, String mensaje) {
        // obtengo timestamp
        String timestamp = formato.format(new Date());
        
        // creo la linea del log
        String lineaLog = String.format("[%s] [%s] %s", timestamp, tipo.toUpperCase(), mensaje);
        
        // muestro en consola
        System.out.println(lineaLog);
       
        // escribo en el archivo
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(archivo, true));
            writer.write(lineaLog);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo de logs: " + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el archivo: " + e.getMessage());
                }
            }
        }
        
        // escribo en el jtextarea correspondiente
        if (tipo.equalsIgnoreCase("INTEGRIDAD") && textAreaIntegridad != null) {
            textAreaIntegridad.append(lineaLog + "\n");
        } else if (tipo.equalsIgnoreCase("TRAFICO") && textAreaTrafico != null) {
            textAreaTrafico.append(lineaLog + "\n");
        } else if (tipo.equalsIgnoreCase("PROCESOS") && textAreaProcesos != null) {
            textAreaProcesos.append(lineaLog + "\n");
        }
    }
}