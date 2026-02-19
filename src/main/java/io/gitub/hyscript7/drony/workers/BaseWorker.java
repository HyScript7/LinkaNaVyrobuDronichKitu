package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;

public abstract class BaseWorker {
    protected final Log logger;
    protected final Komponenta komponenta;
    protected final Sklad sklad;
    protected Thread thread;

    public BaseWorker(Log logger, Komponenta komponenta, Sklad sklad) {
        this.logger = logger;
        this.komponenta = komponenta;
        this.thread = null;
        this.sklad = sklad;
    }

    public void start() throws IllegalStateException {
        if (this.thread != null) {
            throw new IllegalStateException("Worker is already running!");
        }
        this.thread = new Thread(this::work);
    }

    public void stop() throws InterruptedException, IllegalStateException {
        if (this.thread == null) {
            throw new IllegalStateException("Worker is not running!");
        }
        this.thread.interrupt();
        if (this.thread.join(Duration.ofSeconds(5))) {
            this.thread = null;
        } else {
            // TODO: Vypiš hlášku o timeoutu do konzole
        }
    }

    protected abstract void work();
}
