package Procesos;

import Logs.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 *
 * @author Valentin
 */
public class MonitorProcesos extends Thread {
    private List<Procesos> procesos;
    private List<String> listaNegra;
    private boolean activo;
    private int procesosEliminados;
    //catalogo de 30 procesos predefinidos
    private String[] nombres = {
        //procesos normales
        "chrome.exe", "firefox.exe", "explorer.exe", "spotify.exe", "discord.exe",
        "teams.exe", "outlook.exe", "word.exe", "excel.exe", "steam.exe",
        "notepad.exe", "vlc.exe", "photoshop.exe", "vscode.exe", "zoom.exe",
        //procesos sospechosos
        "keylogger.exe", "miner.exe", "backdoor.exe", "trojan.exe", "rootkit.exe",
        "cryptolocker.exe", "ransomware.exe", "botnet.exe", "spyware.exe", "adware.exe",
        "worm.exe", "ddos.exe", "stealer.exe", "rat.exe", "exploit.exe"
    };
    private int[] cpus = {
        //cpus de procesos normales (mismo orden que nombres)
        25, 30, 15, 20, 18,
        35, 22, 28, 32, 40,
        5, 12, 45, 38, 27,
        //cpus de procesos sospechosos
        85, 95, 88, 92, 90,
        97, 94, 86, 83, 81,
        89, 96, 87, 91, 84
    };
    public MonitorProcesos() {
        procesos = new ArrayList<>();
        listaNegra = new ArrayList<>();
        activo = false;
        procesosEliminados = 0;
        this.setDaemon(true);  
    }
    public void iniciar() {
        Log.registrar("PROCESOS", "Iniciando simulacion de procesos");
        //cargo la listaNegra del archivo
        cargarListaNegra();
        //genero 5 procesos aleatorios del catalogo
        generarProcesos(5);
        //inicio todos los procesos
        for (int i = 0; i < procesos.size(); i++) {
            procesos.get(i).start();
        }
        //inicio el monitoreo
        activo = true;
        this.start();
    }
    private void cargarListaNegra() {
        File archivo = new File("listaNegra.txt");
        if (!archivo.exists()) {
            Log.registrar("PROCESOS", "No se encontro lista negra previa");
            return;
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String linea;
            
            while ((linea = reader.readLine()) != null) {
                //cada linea solo tiene el nombre del proceso
                if (!linea.trim().equals("")) {
                    listaNegra.add(linea.trim());
                }
            }
            reader.close();
            if (listaNegra.size() > 0) {
                Log.registrar("PROCESOS", "Lista negra cargada: " + listaNegra.size() + " procesos");
                //muestro los procesos en la lista negra
                for (int i = 0; i < listaNegra.size(); i++) {
                    Log.registrar("PROCESOS", "  - " + listaNegra.get(i));
                }
            }
        } catch (Exception e) {
            Log.registrar("PROCESOS", "Error al cargar lista negra: " + e.getMessage());
        }
    }
    private void generarProcesos(int cantidad) {
        Random random = new Random();
        
        Log.registrar("PROCESOS", "Generando " + cantidad + " procesos:");
        
        //genero 5 procesos aleatorios del catalogo
        for (int i = 0; i < cantidad; i++) {
            // elijo un indice aleatorio
            int indice = random.nextInt(nombres.length);
            //creo el proceso con el nombre y cpu de ese indice
            Procesos p = new Procesos(nombres[indice], cpus[indice]);
            procesos.add(p);
            //muestro el proceso creado
            Log.registrar("PROCESOS", "  " + (i+1) + ". " + nombres[indice] + " (CPU: " + cpus[indice] + "%)");
        }
    }
    @Override
    public void run() {
        // monitoreo cada 3 segundos
        while (activo) {
            try {
                Thread.sleep(3000);
                detectarAnomalias();
            } catch (InterruptedException e) {
                activo = false;
            }
        }
    }
    private void detectarAnomalias() {
        // reviso todos los procesos activos
        for (int i = 0; i < procesos.size(); i++) {
            Procesos p = procesos.get(i);
            
            if (!p.isActivo()) {
                continue;  // ya esta muerto, lo ignoro
            }
            
            //Compruebo si esta en la listaNegra
            if (estaEnBlacklist(p.getNombre())) {
                p.detener();
                Log.registrar("PROCESOS", "Proceso " + p.getNombre() + 
                             " eliminado (encontrado en lista negra)");
                procesosEliminados++;
                continue;
            }
            
            //Compruebo si CPU es alta y persistente
            if (p.getCpu() > 80 && p.getTiempoVivo() > 15) {
                p.detener();
                Log.registrar("PROCESOS", "Proceso " + p.getNombre() + 
                             " excede uso de CPU (" + p.getCpu() + "% durante " + 
                             p.getTiempoVivo() + " segundos)");
                
                //lo añado a la listaNegra
                añadirAListaNegra(p.getNombre());
                procesosEliminados++;
            }
        }
    }
    private boolean estaEnBlacklist(String nombre) {
        for (int i = 0; i < listaNegra.size(); i++) {
            if (listaNegra.get(i).equals(nombre)) {
                return true;
            }
        }
        return false;
    }
    private void añadirAListaNegra(String nombre) {
        //verifico que no este ya en la lista
        if (estaEnBlacklist(nombre)) {
            return;
        }
        listaNegra.add(nombre);
        //escribo en el archivo (solo el nombre)
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("listaNegra.txt", true));
            writer.write(nombre);
            writer.newLine();
            writer.close();
            Log.registrar("PROCESOS", "Proceso " + nombre + " añadido a lista negra");
        } catch (Exception e) {
            Log.registrar("PROCESOS", "Error al escribir en blacklist: " + e.getMessage());
        }
    }
    public void detener() {
        activo = false;
        //detengo todos los procesos
        for (int i = 0; i < procesos.size(); i++) {
            procesos.get(i).detener();
        }
        Log.registrar("PROCESOS", "Simulacion finalizada");
        Log.registrar("PROCESOS", "Procesos eliminados: " + procesosEliminados);
    }
    public int getCantidadProcesos() {
        return procesos.size();
    }
    public int getProcesosEliminados() {
        return procesosEliminados;
    }
}