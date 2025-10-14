package com.project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

public class PR114linies {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/numeros.txt";

        // Crida al mètode que genera i escriu els números aleatoris
        generarNumerosAleatoris(camiFitxer);
    }

    // Mètode per generar 10 números aleatoris i escriure'ls al fitxer
    public static void generarNumerosAleatoris(String camiFitxer) {
        Random rd = new Random();
        ArrayList<String> linies = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            linies.add(String.valueOf(rd.nextInt(100)));
        }
        try {
            // Escribir todas las líneas menos la última con salto de línea, la última sin salto
            Path path = Paths.get(camiFitxer);
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (int i = 0; i < linies.size(); i++) {
                    writer.write(linies.get(i));
                    if (i < linies.size() - 1) {
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
