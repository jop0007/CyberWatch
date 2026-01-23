/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyberwatch.integridad;

import com.cyberwatch.utilidades.Log;

/**
 * Hilo que ejecuta el monitoreo continuo de integridad
 */
public class HiloDetector extends Thread {
    private MonitorArchivos monitor;
    private boolean activo;
    
    public HiloDetector(String rutaCarpeta) {
        monitor = new MonitorArchivos(rutaCarpeta);
        activo = false;
        this.setDaemon(true);
        Log.registrar("INTEGRIDAD", "Hilo detector creado");
    }
    @Override
    public void run() {
        activo = true;
        Log.registrar("INTEGRIDAD", "Hilo detector inicia analisis de archivos");
        //Bucle de monitoreo
        while (activo) {
            try {
                Thread.sleep(3000);
                monitor.detectarCambios();
            } catch (InterruptedException e) {
                Log.registrar("INTEGRIDAD", "Analisis interrumpido");
                activo=false;
            }
        }
        Log.registrar("INTEGRIDAD", "Analisis finalizado");
        // Log del total de cambios (solo si hubo cambios)
        if (monitor.getCambios() > 0) {
            Log.registrar("INTEGRIDAD", "Cambios totales detectados: " + monitor.getCambios());
        }
    }
    public void detener() {
        activo = false;
        Log.registrar("INTEGRIDAD", "Deteniendo analisis de archivos");
    }
    public int getCantidadArchivos() {
        return monitor.getCantidadArchivos();
    }
    public int getCambios(){
        return monitor.getCambios();
    }
}