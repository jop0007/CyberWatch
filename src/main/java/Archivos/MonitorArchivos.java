package Archivos;

import Logs.Log;
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
        carpeta = new File(rutaCarpeta);
        File[] archivos = carpeta.listFiles();
        this.rutaCarpeta = rutaCarpeta;
        nombresAnteriores = new ArrayList<>();
        hashesAnteriores = new ArrayList<>();
        cambios = 0;
        
        // cargo los archivos iniciales
        rellenarArrays(archivos, nombresAnteriores, hashesAnteriores);
        
        Log.registrar("INTEGRIDAD", "Monitor iniciado");
        Log.registrar("INTEGRIDAD", "Escaneo inicial completado: " + nombresAnteriores.size() + " archivos");
    }
    
    public void rellenarArrays(File[] archivos, List<String> nombres, List<String> hashes) {
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
        
        // guardo el estado actual
        rellenarArrays(archivos, nombresActuales, hashesActuales);
        
        // busco archivos NUEVOS
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);
            
            boolean existe = false;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    existe = true;
                }
            }
            
            if (!existe) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual + " ha sido creado");
                cambios++;
            }
        }
        
        // busco archivos ELIMINADOS
        for (int i = 0; i < nombresAnteriores.size(); i++) {
            String nombreAnterior = nombresAnteriores.get(i);
            
            boolean existe = false;
            for (int j = 0; j < nombresActuales.size(); j++) {
                if (nombresActuales.get(j).equals(nombreAnterior)) {
                    existe = true;
                }
            }
            
            if (!existe) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreAnterior + " ha sido eliminado");
                cambios++;
            }
        }
        
        // busco archivos MODIFICADOS
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);
            String hashActual = hashesActuales.get(i);
            
            // busco el hash anterior
            String hashAnterior = null;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    hashAnterior = hashesAnteriores.get(j);
                }
            }
            
            // comparo los hashes si existia antes
            if (hashAnterior != null && !hashActual.equals(hashAnterior)) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual + " ha sido modificado");
                cambios++;
            }
        }
        
        // actualizo las listas para la proxima comprobacion
        nombresAnteriores = nombresActuales;
        hashesAnteriores = hashesActuales;
    }
    
    public String calcularHash(File archivo) {
        try {
            // uso SHA-256 para calcular el hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(archivo);
            
            // leo el archivo completo
            byte[] datos = new byte[(int) archivo.length()];
            fis.read(datos);
            fis.close();
            
            // calculo el hash
            byte[] hash = md.digest(datos);
            
            // convierto a hexadecimal
            String resultado = "";
            for (int i = 0; i < hash.length; i++) {
                resultado = resultado + String.format("%02x", hash[i]);
            }
            
            return resultado;
            
        } catch (Exception e) {
            // si hay error devuelvo vacio
            return "";
        }
    }
    
    public int getCantidadArchivos() {
        return nombresAnteriores.size();
    }
    
    public int getCambios() {
        return cambios;
    }
}