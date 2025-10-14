package com.project;

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
public class PR113append {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/frasesMatrix.txt";

        // Crida al mètode que afegeix les frases al fitxer
        afegirFrases(camiFitxer);
    }

    // Mètode que afegeix les frases al fitxer amb UTF-8 i línia en blanc final
    public static void afegirFrases(String camiFitxer) {

       //Frases Matrix 
         String frase1 = "I can only show you the door";
         String frase2 = "You're the one that has to walk through it";
         String liniaBlanca = ""; // Línia en blanc

        //Crear un array amb les frases i la línia en blanc
        String[] frases = {frase1, frase2, liniaBlanca};
        Path path = Paths.get(camiFitxer); // Ruta del fitxer

        try {
            //Afegeix les frases al fitxer, creant-lo si no existeix
            Files.write(path, Arrays.asList(frases), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            //Eliminar última línia en blanc
            if (Files.exists(path)) {
                java.util.List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
                if (!lineas.isEmpty() && lineas.get(lineas.size() - 1).trim().isEmpty()) {
                    lineas.remove(lineas.size() - 1);
                    Files.write(path, lineas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                }
            }
            System.out.println("Frases afegides correctament a l'arxiu.");
        } catch (IOException e) {
            System.out.println("Error en escriure al fitxer: " + e.getMessage());
        }
    }
}