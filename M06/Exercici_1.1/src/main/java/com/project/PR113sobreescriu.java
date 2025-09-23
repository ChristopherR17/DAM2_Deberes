package com.project;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class PR113sobreescriu {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/frasesMatrix.txt";

        // Crida al mètode que escriu les frases sobreescrivint el fitxer
        escriureFrases(camiFitxer);
    }

    // Mètode que escriu les frases sobreescrivint el fitxer amb UTF-8 i línia en blanc final
    public static void escriureFrases(String camiFitxer) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(camiFitxer, false), StandardCharsets.UTF_8)) {
            writer.write("I can only show you the door\n");
            writer.write("You're the one that has to walk through it\n");
            writer.write("\n");
            writer.flush();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
