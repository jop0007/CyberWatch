/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyberwatch.utilidades;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Valentin
 */

/**
 * Clase encargada de gestionar el archivo de logs del sistema SDAS.
 * Todas las clases del sistema utilizan esta clase para registrar eventos.
 * 
 * Formato de log: [HH:mm:ss] [TIPO] mensaje
 * Ejemplo: [14:32:10] [INTEGRIDAD] El archivo config.txt ha sido modificado.
 */
public class Log {
    
    // Constante con el nombre del archivo de logs
    private static final String archivo = "log_sdas.txt";
    // Formato de fecha y hora para los logs
    private static final SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
    
    //AÑADIR synchronized para evitar problemas de concurrencia
    public static synchronized void escribirLog(String tipo, String mensaje) {
        // Obtener timestamp
        String timestamp = formato.format(new Date());
        
        // Crear la línea del log
        String lineaLog = String.format("[%s] [%s] %s", timestamp, tipo.toUpperCase(), mensaje);
        
        // Mostrar en consola
        System.out.println(lineaLog);
       
        // Escribir en el archivo
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
    }
}