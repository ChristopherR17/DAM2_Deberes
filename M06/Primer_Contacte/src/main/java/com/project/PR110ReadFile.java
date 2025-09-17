package com.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PR110ReadFile {

    public static void main(String[] args) {
        String camiFitxer = System.getProperty("user.dir") + "/data/GestioTasques.java";
        llegirIMostrarFitxer(camiFitxer);  // Només cridem a la funció amb la ruta del fitxer
    }

    // Funció que llegeix el fitxer i mostra les línies amb numeració
    public static void llegirIMostrarFitxer(String camiFitxer) {
        try (BufferedReader br = new BufferedReader(new FileReader(camiFitxer))) {
            String line;
            int num = 1;
            while ((line = br.readLine()) != null) {
                System.out.println(num+ ": " +line); 
                num++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
