package com.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PR115cp {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: Has d'indicar dues rutes d'arxiu.");
            System.out.println("Ús: PR115cp <origen> <destinació>");
            return;
        }

        // Ruta de l'arxiu origen
        String rutaOrigen = args[0];
        // Ruta de l'arxiu destinació
        String rutaDesti = args[1];

        // Crida al mètode per copiar l'arxiu
        copiarArxiu(rutaOrigen, rutaDesti);
    }

    // Mètode per copiar un arxiu de text de l'origen al destí
    public static void copiarArxiu(String rutaOrigen, String rutaDesti) {
        Path origen = Paths.get(rutaOrigen);
        Path desti = Paths.get(rutaDesti);
        try {
            if (!Files.exists(origen) || !Files.isRegularFile(origen)) {
                System.out.println("Error: L'arxiu origen no existeix o no és un fitxer de text.");
                return;
            }
            if (Files.exists(desti)) {
                System.out.println("Advertència: L'arxiu de destinació ja existeix i serà sobreescrit.");
            } else {
                Files.createDirectories(desti.getParent());
            }
            // Leer todo el contenido como bytes para mantener saltos de línea finales
            byte[] contingut = Files.readAllBytes(origen);
            Files.write(desti, contingut);
            System.out.println("Còpia realitzada correctament.");
        } catch (Exception e) {
            System.out.println("Error en copiar l'arxiu: " + e.getMessage());
        }
    }
}
