package com.project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;


public class PR110ReadFile {

    public static void main(String[] args) {
        String camiFitxer = System.getProperty("user.dir") + "/data/GestioTasques.java";
        llegirIMostrarFitxer(camiFitxer);  // Només cridem a la funció amb la ruta del fitxer
    }

    // Funció que llegeix el fitxer i mostra les línies amb numeració
    public static void llegirIMostrarFitxer(String camiFitxer) {
        try (BufferedReader br = new BufferedReader(new FileReader(camiFitxer))) {
            String linia;
            int numeroLinia = 1;
            while ((linia = br.readLine()) != null) {
                System.out.println(numeroLinia + ": " + linia);
                numeroLinia++;
            }
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer: " + e.getMessage());
        }
    }
}