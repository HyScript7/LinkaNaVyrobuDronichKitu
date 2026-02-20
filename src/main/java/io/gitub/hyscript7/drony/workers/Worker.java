package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;

public class Worker extends BaseWorker {
    private final String zpravaPriCekani;
    private final String zpravaPriVyrobeni;
    private final Counter counter;
    private final int stopAfter;

    public Worker(Log logger, Komponenta komponenta, Sklad sklad, String zpravaPriCekani, String zpravaPriVyrobeni, Counter counter, int stopAfter) {
        super(logger, komponenta, sklad);
        this.zpravaPriCekani = zpravaPriCekani;
        this.zpravaPriVyrobeni = zpravaPriVyrobeni;
        this.counter = counter;
        this.stopAfter = stopAfter;
    }

    private static final Log manager = new Log("Manager");

    @Override
    protected void work() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.debug("Thread is interrupted, returning!");
                    return;
                }
                if (sklad.getAmount(komponenta) >= stopAfter) {
                    manager.info("pozastavil výrobu: " + komponenta);
                }
                else if (sklad.procure(komponenta.getRecept())) {
                    sklad.supply(komponenta, 1);
                    counter.increment();
                    logger.info(zpravaPriVyrobeni.replaceFirst("\\[[Tt][Yy][Pp]]", komponenta.getNazev()).replaceFirst("\\{#}", String.valueOf(counter.getCount())));
                } else {
                    logger.info(zpravaPriCekani.replaceFirst("(\\{}|\\[[Tt][Yy][Pp]])", this.komponenta.getNazev()));
                }
                Thread.sleep(Duration.ofSeconds(1));
            }
        } catch (InterruptedException e) {
            if (e.getCause() == null) {
                return;
            }
            logger.error("Worker interrupted unexpectedly: " + e.getMessage() + " caused by " + e.getCause());
        }
    }
}
