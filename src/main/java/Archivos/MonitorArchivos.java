package Archivos;

import Logs.Log;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Valentin
 */
public class MonitorArchivos {
    // ArraysLists iniciales de nombre y hashes de los archivos
    private List<String> nombresAnteriores;
    private List<String> hashesAnteriores;
    
    private File carpeta;
    private String rutaCarpeta;
    private int cambios;
    
    public MonitorArchivos(String rutaCarpeta) {
        carpeta = new File(rutaCarpeta);
        // listFiles devuevle un array con los archivos de la carpeta
        File[] archivos = carpeta.listFiles();
        this.rutaCarpeta = rutaCarpeta;
        nombresAnteriores = new ArrayList<>();
        hashesAnteriores = new ArrayList<>();
        cambios = 0;
        // cargo los archivos iniciales
        rellenarArrays(archivos, nombresAnteriores, hashesAnteriores);
        Log.registrar("INTEGRIDAD", "Monitor iniciado, "+ nombresAnteriores.size() + " archivos encontrados: ");
        // muestro los archivos encontrados
        for (int i = 0; i < nombresAnteriores.size(); i++) {
            Log.registrar("INTEGRIDAD", "  " + (i+1) + ". " + nombresAnteriores.get(i));
        }
    }
    public void rellenarArrays(File[] archivos, List<String> nombres, List<String> hashes) {
        //recorro todos los archivos de la carpeta
        for (int i = 0; i < archivos.length; i++) {
            File archivo = archivos[i];
            //solo si es un archivo rellena los arrayslists, no una carpeta
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
        //guardo el estado actual
        rellenarArrays(archivos, nombresActuales, hashesActuales);
        //busco archivos nuevos
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);
            //compruebo si este archivo existia antes
            boolean existe = false;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    existe = true;
                }
            }
            //si no existia antes es porque es nuevo
            if (!existe) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual + " ha sido creado");
                cambios++;
            }
        }
        //busco archivos eliminados
        for (int i = 0; i < nombresAnteriores.size(); i++) {
            String nombreAnterior = nombresAnteriores.get(i);
            
            //compruebo si este archivo sigue existiendo
            boolean existe = false;
            for (int j = 0; j < nombresActuales.size(); j++) {
                if (nombresActuales.get(j).equals(nombreAnterior)) {
                    existe = true;
                }
            }
            //si ya no existe es porque se ha eliminado
            if (!existe) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreAnterior + " ha sido eliminado");
                cambios++;
            }
        }
        //busco archivos modificados
        for (int i = 0; i < nombresActuales.size(); i++) {
            String nombreActual = nombresActuales.get(i);
            String hashActual = hashesActuales.get(i);
            //busco el hash que tenia este archivo antes
            String hashAnterior = null;
            for (int j = 0; j < nombresAnteriores.size(); j++) {
                if (nombresAnteriores.get(j).equals(nombreActual)) {
                    hashAnterior = hashesAnteriores.get(j);
                }
            }
            //si el archivo existia antes comparo los hashes
            //si son diferentes el archivo se ha modificado
            if (hashAnterior != null && !hashActual.equals(hashAnterior)) {
                Log.registrar("INTEGRIDAD", "El archivo " + nombreActual + " ha sido modificado");
                cambios++;
            }
        }
        //actualizo las listas para la proxima comprobacion
        nombresAnteriores = nombresActuales;
        hashesAnteriores = hashesActuales;
    }
    public String calcularHash(File archivo) {
        try {
            //uso SHA-256 para calcular el hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(archivo);
            //leo el archivo completo
            byte[] datos = new byte[(int) archivo.length()];
            fis.read(datos);
            fis.close();
            //calculo el hash
            byte[] hash = md.digest(datos);
            //convierto a hexadecimal
            String resultado = "";
            for (int i = 0; i < hash.length; i++) {
                resultado = resultado + String.format("%02x", hash[i]);
            }
            return resultado;
        } catch (Exception e) {
            //si hay error devuelvo vacio
            return "";
        }
    }
    public int getCambios() {
        return cambios;
    }
}