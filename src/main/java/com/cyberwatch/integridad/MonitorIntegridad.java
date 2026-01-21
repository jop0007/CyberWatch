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
import java.util.Map;

/**
 *
 * @author alumno
 */
public class MonitorIntegridad {
    private List<String> nombresArchivos;
    private List<String> hashesArchivos;
    private String rutaCarpeta;
    
    public MonitorIntegridad(String rutaCarpeta){
        nombresArchivos=new ArrayList<>();
        hashesArchivos=new ArrayList<>();
        this.rutaCarpeta=rutaCarpeta;
        File carpeta=new File(rutaCarpeta);
        File[] archivos=carpeta.listFiles();
        if(archivos!=null){
            for(int i=0;i<archivos.length;i++){
                if(archivos[i].isFile()){
                    String hash=calcularHash(archivos[i]);
                    nombresArchivos.add(archivos[i].getName());
                    hashesArchivos.add(hash);
                }
            }
        }
    }
    public String calcularHash(File archivo){
        try{
            MessageDigest digest=MessageDigest.getInstance("SHA–256");
            FileInputStream fis=new FileInputStream(archivo);
            byte[] buffer=new byte[1024];
            int bytesLeidos;
            while((bytesLeidos=fis.read(buffer))!=-1){
                digest.update(buffer, 0, bytesLeidos);
            }
            fis.close();
            byte[]hashBytes=digest.digest();
            StringBuilder sb=new StringBuilder();
            
        }catch(Exception e){
            
        }
        return "pene";
    }
    
}
