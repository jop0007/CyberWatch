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
        //cpus de procesos normales
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
        //cargo la lista negra del archivo
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
                listaNegra.add(linea.trim());
            }
            reader.close();
            Log.registrar("PROCESOS", "Lista negra cargada: " + listaNegra.size() + " procesos");
        } catch (Exception e) {
        }
    }
    private void generarProcesos(int cantidad) {
        Random random = new Random();
        Log.registrar("PROCESOS", "Generando " + cantidad + " procesos:");
        //genero 5 procesos aleatorios del catalogo
        for (int i = 0; i < cantidad; i++) {
            int indice = random.nextInt(nombres.length);
            Procesos p = new Procesos(nombres[indice], cpus[indice]);
            procesos.add(p);
            //muestro el proceso creado
            Log.registrar("PROCESOS", " - " + nombres[indice] + " (CPU: " + cpus[indice] + "%)");
        }
    }
    @Override
    public void run() {
        //monitoreo cada 3 segundos
        while (activo) {
            try {
                Thread.sleep(3000);
                detectarAnomalias();
            } catch (Exception e) {
                activo = false;
            }
        }
    }
    private void detectarAnomalias() {
        //reviso todos los procesos activos
        for (int i = 0; i < procesos.size(); i++) {
            Procesos p = procesos.get(i);
            if (!p.isActivo()) {
                continue;  //ya esta muerto lo ignoro
            }
            //compruebo si esta en la lista negra
            if (estaEnListaNegra(p.getNombre())) {
                p.detener();
                Log.registrar("PROCESOS", "Proceso " + p.getNombre() + " eliminado (encontrado en lista negra)");
                procesosEliminados++;
                continue;
            }
            //compruebo si CPU es alta y persistente
            if (p.getCpu() > 80 && p.getTiempoVivo() > 15) {
                p.detener();
                Log.registrar("PROCESOS", "Proceso " + p.getNombre() + " excede uso de CPU (" + p.getCpu() + "% durante " + p.getTiempoVivo() + " segundos)");
                añadirAListaNegra(p.getNombre());
                procesosEliminados++;
            }
        }
    }
    private boolean estaEnListaNegra(String nombre) {
        for (int i = 0; i < listaNegra.size(); i++) {
            if (listaNegra.get(i).equals(nombre)) {
                return true;
            }
        }
        return false;
    }
    private void añadirAListaNegra(String nombre) {
        listaNegra.add(nombre);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("listaNegra.txt", true));
            writer.write(nombre);
            writer.newLine();
            writer.close();
            Log.registrar("PROCESOS", "Proceso " + nombre + " añadido a lista negra");
        } catch (Exception e) {
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
}