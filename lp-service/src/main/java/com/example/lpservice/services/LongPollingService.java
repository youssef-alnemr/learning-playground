package com.example.lpservice.services;

import com.example.lpservice.workers.Worker;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LongPollingService {

    private final ConcurrentHashMap<UUID, Worker> workers = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public UUID submitRequest() {
        try {
            UUID uuid = UUID.randomUUID();
            Worker worker = new Worker(uuid, 0, (uuid1 -> workers.remove(uuid)));
            Worker previousWorker = workers.putIfAbsent(uuid, worker);
            if (worker.equals(previousWorker)) {
                throw new IllegalStateException("Cannot create two workers with the same UUID");
            } else if (previousWorker != null) {
                throw new IllegalStateException("A previous process is already running");
            }

            executorService.submit(worker);
            return uuid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStatus(UUID uuid) {
        while (workers.get(uuid) != null);
        return String.format("Worker %s has finished", uuid);
    }
}
