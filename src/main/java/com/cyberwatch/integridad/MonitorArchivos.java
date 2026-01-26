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

public class MonitorArchivos {
    private List<String> nombresAnteriores;
    private List<String> hashesAnteriores;
    private File carpeta;
    private String rutaCarpeta;
    private int cambios;
    
    public MonitorArchivos(String rutaCarpeta) {
        carpeta=new File(rutaCarpeta);
        File[] archivos = carpeta.listFiles();
        this.rutaCarpeta = rutaCarpeta;
        nombresAnteriores = new ArrayList<>();
        hashesAnteriores = new ArrayList<>();
        cambios=0;
        rellenarArrays(archivos, nombresAnteriores, hashesAnteriores);
        Log.registrar("INTEGRIDAD", "Monitor iniciado");
        Log.registrar("INTEGRIDAD", "Escaneo inicial completado: " + nombresAnteriores.size() + " archivos");
    }
    public void rellenarArrays(File[] archivos, List<String> nombres, List<String> hashes){
        if (archivos == null) return;
        for (int i = 0; i < archivos.length; i++) {
            File archivo = archivos[i];
            if (archivo.isFile()) {
                nombres.add(archivo.getName());
                hashes.add(calcularHash(archivo));
            }
        }
        
    }
    public void detectarCambios() {
        File[] archivos = carpeta.listFiles();
        List<String> nombresActuales = new ArrayList<>();
        List<String> hashesActuales = new ArrayList<>();
        // Calcular hashes actuales
        rellenarArrays(archivos, nombresActuales, hashesActuales);
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
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual+" ha sido creado");
                cambios++;
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
                Log.registrar("INTEGRIDAD", "El archivo " + nombreAnterior+" ha sido eliminado");
                cambios++;
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
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual+" ha sido modificado");
                cambios++;
            }
        }
        // Actualizar listas
        nombresAnteriores = nombresActuales;
        hashesAnteriores = hashesActuales;
    }
    public String calcularHash(File archivo) {
        try {
            // Creo el objeto que calcula el hash SHA-256
            // SHA-256 es un algoritmo que viene incluido en Java
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Abro el archivo para poder leerlo
            FileInputStream fis = new FileInputStream(archivo);

            // Creo un array del tamaño del archivo para guardar todos los bytes
            // El (int) es para convertir el tamaño a entero
            byte[] datos = new byte[(int) archivo.length()];

            // Leo todo el contenido del archivo y lo guardo en el array
            fis.read(datos);

            // Cierro el archivo porque ya lo he leído
            fis.close();

            // Calculo el hash de los datos que leí
            // El método digest() me devuelve el hash como array de bytes
            byte[] hash = md.digest(datos);

            // Creo un String vacío donde voy a guardar el hash en formato legible
            String resultado = "";

            // Recorro todos los bytes del hash para convertirlos a hexadecimal
            for (int i = 0; i < hash.length; i++) {
                // Convierto cada byte a hexadecimal con 2 dígitos
                // %02x significa: formato hexadecimal con 2 dígitos mínimo
                resultado = resultado + String.format("%02x", hash[i]);
            }

            // Devuelvo el hash completo como String
            return resultado;

        } catch (Exception e) {
            // Si hay algún error (archivo no existe, sin permisos, etc.)
            // devuelvo un String vacío
            return "";
        }
    }
    
    public int getCantidadArchivos() {
        return nombresAnteriores.size();
    }
    public int getCambios(){
        return cambios;
    }
}
