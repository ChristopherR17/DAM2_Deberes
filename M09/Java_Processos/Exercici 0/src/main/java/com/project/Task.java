package com.project;

public class Task implements Runnable {
    private int type;

    public Task(int type) {
        this.type = type;
    }

    @Override
    public void run() {
        switch (type) {
            case 0:
                System.out.println("Registrando esdeveniments de sistema...");
                break;
            default:
                System.out.println("Comprovant l'estat de la xarxa...");
        }
    }
}