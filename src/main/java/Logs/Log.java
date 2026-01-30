package Logs;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTextArea;

/**
 *
 * @author Valentin
 */
public class Log {
    private static final String archivo = "log_sdas.txt";
    private static final SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
     //jtextareas para mostrar los logs en la interfaz
    private static JTextArea textAreaIntegridad;
    private static JTextArea textAreaTrafico;
    private static JTextArea textAreaProcesos;
     //metodo para configurar los jtextareas desde la interfaz
    public static void setTextAreas(JTextArea integridad, JTextArea trafico, JTextArea procesos) {
        textAreaIntegridad = integridad;
        textAreaTrafico = trafico;
        textAreaProcesos = procesos;
    }
    public static void registrar(String tipo, String mensaje) {
        String timestamp = formato.format(new Date());
        String lineaLog = String.format("[%s] [%s] %s", timestamp, tipo.toUpperCase(), mensaje);
        //muestro en consola
        System.out.println(lineaLog);
        //escribo en el archivo
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true));
            writer.write(lineaLog);
            writer.newLine();
            writer.close();
        } catch (Exception e) {
        }
        //escribo en el jtextarea correspondiente
        if (tipo.equalsIgnoreCase("INTEGRIDAD")) {
            textAreaIntegridad.append(lineaLog + "\n");
        } else if (tipo.equalsIgnoreCase("RED")) {
            textAreaTrafico.append(lineaLog + "\n");
        } else if (tipo.equalsIgnoreCase("PROCESOS")) {
            textAreaProcesos.append(lineaLog + "\n");
        }
    }
}