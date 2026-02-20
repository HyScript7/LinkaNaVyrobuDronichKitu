package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;

public class Worker extends BaseWorker {
    private final String zpravaPriCekani;
    private final String zpravaPriVyrobeni;
    private final Counter counter;

    public Worker(Log logger, Komponenta komponenta, Sklad sklad, String zpravaPriCekani, String zpravaPriVyrobeni, Counter counter) {
        super(logger, komponenta, sklad);
        this.zpravaPriCekani = zpravaPriCekani;
        this.zpravaPriVyrobeni = zpravaPriVyrobeni;
        this.counter = counter;
    }

    @Override
    protected void work() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.debug("Thread is interrupted, returning!");
                    return;
                }
                if (sklad.procure(komponenta.getRecept())) {
                    sklad.supply(komponenta, 1);
                    counter.increment();
                    logger.info(zpravaPriVyrobeni.replaceFirst("\\{}", komponenta.getNazev()).replaceFirst("\\{}", String.valueOf(counter.getCount())));
                }
                logger.info(zpravaPriCekani.replaceFirst("\\{}", this.komponenta.getNazev()));
                Thread.sleep(Duration.ofSeconds(1));
            }
        } catch (InterruptedException e) {
            logger.error("Worker interrupted: " + e.getMessage() + " caused by " + e.getCause());
        }
    }
}
