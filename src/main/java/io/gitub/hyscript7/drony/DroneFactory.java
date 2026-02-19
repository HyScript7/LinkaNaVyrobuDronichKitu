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
    }

    public void start() {
        logger.info("START");
        for (BaseWorker worker : workers) {
            worker.start();
        }
    }

    public void stop() {
        logger.info("STOP");
        for (BaseWorker worker : workers) {
            try {
                worker.stop();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
