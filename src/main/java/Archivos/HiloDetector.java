package Archivos;

import Logs.Log;

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
        Log.registrar("INTEGRIDAD", "Iniciando monitoreo continuo");
        
        // bucle principal de monitoreo
        while (activo) {
            try {
                Thread.sleep(3000); // reviso cada 3 segundos
                monitor.detectarCambios();
            } catch (InterruptedException e) {
                Log.registrar("INTEGRIDAD", "Monitoreo interrumpido");
            }
        }
        
        Log.registrar("INTEGRIDAD", "Monitoreo finalizado");
        Log.registrar("INTEGRIDAD", "Total de cambios detectados: " + monitor.getCambios());
    }
    
    public void detener() {
        activo = false;
        Log.registrar("INTEGRIDAD", "Deteniendo monitoreo...");
    }
    
    public int getCantidadArchivos() {
        return monitor.getCantidadArchivos();
    }
    
    public int getCambios() {
        return monitor.getCambios();
    }
}