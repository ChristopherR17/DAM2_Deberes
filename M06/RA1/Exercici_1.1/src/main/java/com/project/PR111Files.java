package com.project;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class PR111Files {

    public static void main(String[] args) {
        String camiFitxer = System.getProperty("user.dir") + "/data/pr111";
        gestionarArxius(camiFitxer);
    }

    public static void gestionarArxius(String camiFitxer) {
        Path directory = Path.of(camiFitxer);
        try {
            // Crear el directorio si no existe
            Files.createDirectories(directory);

            // Crear subcarpeta 'myFiles' dentro del directorio dado
            Path carpeta = directory.resolve("myFiles");
            Files.createDirectories(carpeta);

            // Crear file1.txt y file2.txt dentro de 'myFiles'
            Path file1 = carpeta.resolve("file1.txt");
            Path file2 = carpeta.resolve("file2.txt");
            Files.createFile(file1);
            Files.createFile(file2);

            // Renombrar file2.txt a renamedFile.txt
            Path renamedFile = carpeta.resolve("renamedFile.txt");
            Files.move(file2, renamedFile);

            // Eliminar file1.txt
            Files.deleteIfExists(file1);

            // Al final, solo debe quedar renamedFile.txt en la carpeta
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
