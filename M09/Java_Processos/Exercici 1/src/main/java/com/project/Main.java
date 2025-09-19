package com.project;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        ConcurrentHashMap<String, Double> bankData = new ConcurrentHashMap<>();
        
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Tasca 1: Introduir dades inicials (simula recepció operació bancària)
        Runnable initTask = () -> {
            bankData.put("saldo", 1000.0);
            System.out.println("Dades inicials introduïdes: saldo = 1000.0");
        };

        // Tasca 2: Modificar dades (simula càlcul d'interessos/comissions)
        Runnable modifyTask = () -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            bankData.computeIfPresent("saldo", (k, v) -> v * 1.05 - 10); // +5% interès, -10 comissió
            System.out.println("Dades modificades: saldo actualitzat");
        };

        // Tasca 3: Llegir dades modificades i retornar resultat final
        Callable<String> resultTask = () -> {
            try { Thread.sleep(1000); } catch (InterruptedException e) {}
            Double saldo = bankData.get("saldo");
            return "Resultat final per al client: saldo = " + saldo;
        };

        // Executar les tasques
        executor.execute(initTask);
        executor.execute(modifyTask);
        Future<String> future = executor.submit(resultTask);

        try {
            String result = future.get(); // Espera i recull el resultat de la tasca Callable
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}