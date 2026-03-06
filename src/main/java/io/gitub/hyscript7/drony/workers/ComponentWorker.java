package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;
import lombok.Getter;

import java.time.Duration;

public class ComponentWorker extends BaseWorker {
    private static final Duration DEFAULT_SLEEP_DURATION = Duration.ofSeconds(1);

    @Getter
    private final Komponenta komponenta;
    private final String zpravaPriCekani;
    private final String zpravaPriVyrobeni;
    private final Counter counter;
    private int internalCounter;
    private final int stopAfter;
    private final Duration successDelay;

    public ComponentWorker(Log logger, Komponenta komponenta, Sklad sklad, String zpravaPriCekani, String zpravaPriVyrobeni, Counter counter, int stopAfter) {
        super(logger, sklad);
        this.komponenta = komponenta;
        this.zpravaPriCekani = zpravaPriCekani;
        this.zpravaPriVyrobeni = zpravaPriVyrobeni;
        this.counter = counter;
        this.internalCounter = 0;
        this.stopAfter = stopAfter;
        this.successDelay = Duration.ofSeconds(0);
    }

    public ComponentWorker(Log logger, Komponenta komponenta, Sklad sklad, String zpravaPriCekani, String zpravaPriVyrobeni, Counter counter, int stopAfter, Duration successDelay) {
        super(logger, sklad);
        this.komponenta = komponenta;
        this.zpravaPriCekani = zpravaPriCekani;
        this.zpravaPriVyrobeni = zpravaPriVyrobeni;
        this.counter = counter;
        this.internalCounter = 0;
        this.stopAfter = stopAfter;
        this.successDelay = successDelay;
    }

    public int getInternalCounterValue() {
        return internalCounter;
    }

    private static final Log manager = new Log("Manager");

    @Override
    protected void work() {
        try {
            while (true) {
                Duration duration = DEFAULT_SLEEP_DURATION;
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
                    internalCounter++;
                    logger.info(zpravaPriVyrobeni.replaceFirst("\\[[Tt][Yy][Pp]]", komponenta.getNazev()).replaceFirst("\\{#}", String.valueOf(counter.getCount())));
                    // úspěšné sestavení
                    duration = duration.plus(successDelay);
                } else {
                    logger.info(zpravaPriCekani.replaceFirst("(\\{}|\\[[Tt][Yy][Pp]])", this.komponenta.getNazev()));
                }
                Thread.sleep(duration);
            }
        } catch (InterruptedException e) {
            if (e.getCause() == null) {
                return;
            }
            logger.error("Worker interrupted unexpectedly: " + e.getMessage() + " caused by " + e.getCause());
        }
    }
}
