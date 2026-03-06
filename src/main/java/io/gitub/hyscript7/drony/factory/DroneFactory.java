package io.gitub.hyscript7.drony.factory;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.workers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DroneFactory {
    private final List<BaseWorker> workers;
    private final Log logger;

    public DroneFactory(List<BaseWorker> workers) {
        this.workers = new ArrayList<>();
        this.workers.addAll(workers);
        this.logger = new Log("SYSTEM");
    }

    public List<BaseWorker> getWorkers() {
        return Collections.unmodifiableList(workers);
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
