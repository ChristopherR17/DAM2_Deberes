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
            Files.createDirectories(directory);

            Path file1 = directory.resolve("file1.txt");
            Path file2 = directory.resolve("file2.txt");

            Path renamedFile = directory.resolve("renamedFile.txt");
            Files.move(file2, renamedFile);

            System.out.println("Els arxius de la carpeta són: ");
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            for (Path path : stream) {
                System.out.println(path.getFileName());
            }

            Files.deleteIfExists(file1);

            System.out.println("Els arxius de la carpeta són: ");
            DirectoryStream<Path> stream = Files.newDirectoryStream(directory);
            for (Path path : stream) {
                System.out.println(path.getFileName());
            }  
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
