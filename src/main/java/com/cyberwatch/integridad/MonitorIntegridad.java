/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyberwatch.integridad;

import com.cyberwatch.utilidades.Log;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de la lógica de monitoreo de integridad de archivos.
 * Calcula hashes SHA-256 y detecta cambios, creaciones y eliminaciones.
 */
public class MonitorIntegridad {
    
    private List<String> nombresAnteriores;
    private List<String> hashesAnteriores;
    private String rutaCarpeta;
    
    public MonitorIntegridad(String rutaCarpeta) {
        this.rutaCarpeta = rutaCarpeta;
        this.nombresAnteriores = new ArrayList<>();
        this.hashesAnteriores = new ArrayList<>();
        
        Log.registrar("INTEGRIDAD", "MonitorIntegridad creado para: " + rutaCarpeta);
    }
    
    /**
     * Realiza el escaneo inicial de la carpeta
     */
    public void escanearInicial() {
        File carpeta = new File(rutaCarpeta);
        
        Log.registrar("INTEGRIDAD", "Realizando escaneo inicial...");
        
        // Limpiar datos anteriores
        nombresAnteriores.clear();
        hashesAnteriores.clear();
        
        // Obtener archivos
        File[] archivos = carpeta.listFiles();
        
        if (archivos == null || archivos.length == 0) {
            Log.registrar("INTEGRIDAD", "La carpeta está vacía");
            return;
        }
        
        // Procesar cada archivo
        int archivosEscaneados = 0;
        for (int i = 0; i < archivos.length; i++) {
            File archivo = archivos[i];
            if (archivo.isFile()) {
                String hash = calcularHash(archivo);
                nombresAnteriores.add(archivo.getName());
                hashesAnteriores.add(hash);
                archivosEscaneados++;
            }
        }
        
        Log.registrar("INTEGRIDAD", "Escaneo inicial completado: " + archivosEscaneados + " archivos");
    }
    
    /**
     * Detecta cambios en los archivos
     */
    public int detectarCambios() {
        File carpeta = new File(rutaCarpeta);
        File[] archivos = carpeta.listFiles();

        List<String> nombresActuales = new ArrayList<>();
        List<String> hashesActuales = new ArrayList<>();

        // Calcular hashes actuales
        if (archivos != null) {
            for (int i = 0; i < archivos.length; i++) {
                File archivo = archivos[i];
                if (archivo.isFile()) {
                    String hash = calcularHash(archivo);
                    nombresActuales.add(archivo.getName());
                    hashesActuales.add(hash);
                }
            }
        }

        int cambiosDetectados = 0;

        // Detectar archivos CREADOS
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);

            // Buscar si existe en anteriores
            boolean existe = false;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    existe = true;
                }
            }

            if (!existe) {
                Log.registrar("INTEGRIDAD", "Archivo CREADO: " + nombreActual);
                cambiosDetectados++;
            }
        }

        // Detectar archivos ELIMINADOS
        for (int i = 0; i < nombresAnteriores.size(); i++) {
            String nombreAnterior = nombresAnteriores.get(i);

            // Buscar si existe en actuales
            boolean existe = false;
            for (int j = 0; j < nombresActuales.size(); j++) {
                if (nombresActuales.get(j).equals(nombreAnterior)) {
                    existe = true;
                }
            }

            if (!existe) {
                Log.registrar("INTEGRIDAD", "Archivo ELIMINADO: " + nombreAnterior);
                cambiosDetectados++;
            }
        }

        // Detectar archivos MODIFICADOS
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);
            String hashActual = hashesActuales.get(i);

            // Buscar en anteriores y obtener hash
            String hashAnterior = null;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    hashAnterior = hashesAnteriores.get(j);
                }
            }

            // Si existía antes, comparar hashes
            if (hashAnterior != null && !hashActual.equals(hashAnterior)) {
                Log.registrar("INTEGRIDAD", "Archivo MODIFICADO: " + nombreActual);
                cambiosDetectados++;
            }
        }

        if (cambiosDetectados > 0) {
            Log.registrar("INTEGRIDAD", "Total de cambios detectados: " + cambiosDetectados);
        }

        // Actualizar listas
        nombresAnteriores = nombresActuales;
        hashesAnteriores = hashesActuales;

        return cambiosDetectados;
    }
    public String calcularHash(File archivo) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(archivo);
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            
            fis.close();
            
            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            
            for (int i = 0; i < hashBytes.length; i++) {
                byte b = hashBytes[i];
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
            
        } catch (Exception e) {
            Log.registrar("INTEGRIDAD", "Error al calcular hash: " + e.getMessage());
            return "";
        }
    }
    
    public int getCantidadArchivos() {
        return nombresAnteriores.size();
    }
}
