package com.example.lpservice.workers;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Worker implements Runnable {

    private final UUID uuid;
    private final AtomicInteger currentNumber;


    Function<UUID, Worker> callback;

    public Worker(UUID uuid, int currentNumber, Function<UUID, Worker> callback) {
        this.uuid = uuid;
        this.currentNumber = new AtomicInteger(currentNumber);
        this.callback = callback;
    }

    @Override
    public void run() {
        update();
    }

    private void update() {
        try {
            if (currentNumber.get() == 120) {
                callback.apply(uuid);
                return;
            }
            Thread.sleep(1000);
            currentNumber.getAndAdd(10);
            update();

        } catch (InterruptedException e) {
            callback.apply(uuid);
            throw new RuntimeException(e);
        }
    }
}
