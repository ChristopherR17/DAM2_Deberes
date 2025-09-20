package com.project;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        CompletableFuture<Void> future = CompletableFuture
            // Primera tasca: validació de dades (supplyAsync)
            .supplyAsync(() -> {
                System.out.println("Validant sol·licitud...");
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                int valorInicial = 100;
                System.out.println("Dades validades: " + valorInicial);
                return valorInicial;
            })
            // Segona tasca: processar dades (thenApply)
            .thenApply(dades -> {
                System.out.println("Processant dades...");
                try { Thread.sleep(500); } catch (InterruptedException e) {}
                int resultat = dades * 2 + 50;
                System.out.println("Resultat calculat: " + resultat);
                return resultat;
            })
            // Tercera tasca: mostrar resultat (thenAccept)
            .thenAccept(resultatFinal -> {
                System.out.println("Resposta a l'usuari: El resultat final és " + resultatFinal);
            });

        // Esperar que totes les operacions asíncrones acabin
        future.join();
    }
}