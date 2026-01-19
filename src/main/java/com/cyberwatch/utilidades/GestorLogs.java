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
public class GestorLogs {
    
    //Constante con el nombre del archivo de logs
    private static final String archivo = "log_sdas.txt";
    //Formato de fecha y hora para los logs
    private static final SimpleDateFormat formato = new SimpleDateFormat("HH:mm:ss");
    
    public static void escribirLog(String tipo, String mensaje) {
        String timestamp = obtenerTimestamp();
        String lineaLog = String.format("[%s] [%s] %s", timestamp, tipo.toUpperCase(), mensaje);
        // Escribir en el archivo
        escribirEnArchivo(lineaLog);
        // Tambien muestra en consola 
        System.out.println(lineaLog);
    }
    
    /**
     * Obtiene el timestamp actual en formato HH:mm:ss
     * 
     * @return String con la hora actual
     */
    private static String obtenerTimestamp() {
        return formato.format(new Date());
    }
    
    /**
     * Escribe una línea en el archivo de logs.
     * Si hay error, lo muestra en consola.
     * 
     * @param linea Texto a escribir
     */
    private static void escribirEnArchivo(String linea) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            writer.write(linea);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("❌ Error al escribir en el archivo de logs: " + e.getMessage());
        }
    } 
    
    /**
     * Método opcional para limpiar el archivo de logs.
     * Útil al inicio de una nueva sesión de monitoreo.
     */
    public static void limpiarLogs() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
            writer.write(""); // Vacía el archivo
            escribirLog("SISTEMA", "Archivo de logs reiniciado");
        } catch (IOException e) {
            System.err.println("❌ Error al limpiar el archivo de logs: " + e.getMessage());
        }
    }
}
