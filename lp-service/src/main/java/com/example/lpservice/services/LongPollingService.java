package com.example.lpservice.services;

import com.example.lpservice.workers.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LongPollingService {

    private final ConcurrentHashMap<UUID, Worker> workers = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final String serviceId = ManagementFactory.getRuntimeMXBean().getName();
    private final Logger logger = LoggerFactory.getLogger(LongPollingService.class);

    public UUID submitRequest() {
        try {
            UUID uuid = UUID.randomUUID();
            logger.error("Worker {} on machine {} has received request", uuid, serviceId);
            Worker worker = new Worker(uuid, 0, (uuid1 -> {
                logger.error("Worker {} on machine {} has finished", uuid1, serviceId);
                return workers.remove(uuid);
            }));
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
        logger.info("Polling worker {} on machine {}", uuid, serviceId);
        while (workers.get(uuid) != null);
        return String.format("Worker %s has finished", uuid);
    }
}