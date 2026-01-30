package Archivos;

import Logs.Log;
/**
 *
 * @author Valentin
 */
public class HiloDetector extends Thread {
    private MonitorArchivos monitor;
    private boolean activo;
    
    public HiloDetector(String rutaCarpeta) {
        monitor = new MonitorArchivos(rutaCarpeta);
        activo = false;
        this.setDaemon(true);
    }
    
    @Override
    public void run() {
        activo = true;
        Log.registrar("INTEGRIDAD", "Detectando cambios... ");
        while (activo) {
            try {
                Thread.sleep(3000); //reviso cada 3 segundos
                monitor.detectarCambios();
            } catch (InterruptedException e) {
                activo=false;
                Log.registrar("INTEGRIDAD", "Deteccion de cambios interrumpida");
            }
        }
        Log.registrar("INTEGRIDAD", "Deteccion de cambios finalizada");
        Log.registrar("INTEGRIDAD", "Cambios totales detectados: " + monitor.getCambios());
    }
    //cambia es estado
    public void detener() {
        activo = false;
    }
    public int getCantidadArchivos() {
        return monitor.getCantidadArchivos();
    }
    public int getCambios() {
        return monitor.getCambios();
    }
}