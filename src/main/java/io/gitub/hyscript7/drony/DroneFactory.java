package io.gitub.hyscript7.drony;

import io.gitub.hyscript7.drony.workers.*;

import java.util.ArrayList;
import java.util.List;

public class DroneFactory {
    private final List<BaseWorker> workers;
    private final Log logger;

    public DroneFactory(WorkerDirector workerDirector, WorkerBuilder workerBuilder, int numberOfAssemblers) {
        this.workers = new ArrayList<>();
        this.logger = new Log("SYSTEM");
        workerDirector.buildRamWorker(workerBuilder);
        this.workers.add(workerBuilder.getResult());
        workerDirector.buildSadaVrtuliWorker(workerBuilder);
        this.workers.add(workerBuilder.getResult());
        workerDirector.buildRidiciDeskaWorker(workerBuilder);
        this.workers.add(workerBuilder.getResult());
        workerDirector.buildKitWorker(workerBuilder);

        Counter assemblerCounter = new CounterImpl();

        for (int assemblerNumber = 1; assemblerNumber <= numberOfAssemblers; assemblerNumber++) {
            workerBuilder.logger(new Log("SESTAVITEL-" + assemblerNumber)).pocitadlo(assemblerCounter);
            this.workers.add(workerBuilder.getResult());
        }
        logger.debug("Created " + workers.size() + " workers!");
    }

    public void start() {
        logger.info("START");
        for (BaseWorker worker : workers) {
            logger.debug("Starting worker " + worker.toString());
            worker.start();
        }
    }

    public void stop() {
        logger.info("STOP");
        for (BaseWorker worker : workers) {
            try {
                logger.debug("Stopping worker " + worker.toString());
                worker.stop();
            } catch (InterruptedException e) {
                logger.error("Stopping worker failed: " + e.getMessage());
            }
        }
    }
}
