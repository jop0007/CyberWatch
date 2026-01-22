/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cyberwatch.integridad;

import com.cyberwatch.utilidades.Log;

/**
 * Hilo que ejecuta el monitoreo continuo de integridad
 */
public class HiloMonitor extends Thread {
    
    private MonitorIntegridad monitor;
    private int intervaloComprobacion;
    private boolean activo;
    
    public HiloMonitor(String rutaCarpeta, int intervaloSegundos) {
        this.monitor = new MonitorIntegridad(rutaCarpeta);
        this.intervaloComprobacion = intervaloSegundos * 1000;
        this.activo = false;
        this.setDaemon(true);
        
        Log.registrar("INTEGRIDAD", "HiloMonitoreo creado (intervalo: " + intervaloSegundos + "s)");
    }
    
    @Override
    public void run() {
        activo = true;
        Log.registrar("INTEGRIDAD", "Iniciando monitoreo continuo...");
        
        // Escaneo inicial
        monitor.primerEscaneo();
        
        // Bucle de monitoreo
        while (activo && !isInterrupted()) {
            try {
                Thread.sleep(intervaloComprobacion);
                
                if (activo) {
                    monitor.detectarCambios();
                }
                
            } catch (InterruptedException e) {
                Log.registrar("INTEGRIDAD", "Monitoreo interrumpido");
                activo=false;
            }
        }
        
        Log.registrar("INTEGRIDAD", "Monitoreo finalizado");
    }
    
    public void detenerMonitoreo() {
        activo = false;
        this.interrupt();
        Log.registrar("INTEGRIDAD", "Deteniendo monitoreo...");
    }
    
    public boolean estaActivo() {
        return activo && isAlive();
    }
    
    public int getCantidadArchivos() {
        return monitor.getCantidadArchivos();
    }
}