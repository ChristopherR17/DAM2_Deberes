package com.project;

import java.nio.file.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
public class PR113sobreescriu {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/frasesMatrix.txt";

        // Crida al mètode que escriu les frases sobreescrivint el fitxer
        escriureFrases(camiFitxer);
    }

    // Mètode que escriu les frases sobreescrivint el fitxer amb UTF-8 i línia en blanc final
    public static void escriureFrases(String camiFitxer) {
    
         //Frases Matrix 
            String frase1 = "I can only show you the door";
            String frase2 = "You're the one that has to walk through it";
            String liniaBlanca = ""; // Línia en blanc
    
          //Crear un array amb les frases i la línia en blanc
          String[] frases = {frase1, frase2, liniaBlanca};
          Path path = Paths.get(camiFitxer); // Ruta del fitxer
    
          try {
                //Escriu les frases al fitxer, creant-lo si no existeix i sobreescrivint el contingut anterior
                Files.write(path, Arrays.asList(frases), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                //Eliminar última línia en blanc
                if (Files.exists(path)) {
                    java.util.List<String> lineas = Files.readAllLines(path, StandardCharsets.UTF_8);
                    if (!lineas.isEmpty() && lineas.get(lineas.size() - 1).trim().isEmpty()) {
                        lineas.remove(lineas.size() - 1);
                        Files.write(path, lineas, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
                    }
                }
                System.out.println("Frases escrites correctament a l'arxiu.");
          } catch (IOException e) {
                System.out.println("Error en escriure al fitxer: " + e.getMessage());
          }
    }
}