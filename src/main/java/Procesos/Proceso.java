package Procesos;

public class Proceso extends Thread {
    private String nombre;
    private int cpu;
    private int tiempoVivo;
    private boolean activo;
    
    public Proceso(String nombre, int cpu) {
        this.nombre = nombre;
        this.cpu = cpu;
        this.tiempoVivo = 0;
        this.activo = true;
    }
    
    @Override
    public void run() {
        // el proceso se ejecuta mientras este activo
        while (activo) {
            try {
                Thread.sleep(1000);  // espero 1 segundo
                tiempoVivo++;  // incremento el tiempo que lleva vivo
            } catch (InterruptedException e) {
                // si me interrumpen salgo del bucle
                activo = false;
            }
        }
    }
    
    public void detener() {
        activo = false;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getCpu() {
        return cpu;
    }
    
    public int getTiempoVivo() {
        return tiempoVivo;
    }
    
    public boolean isActivo() {
        return activo;
    }
}