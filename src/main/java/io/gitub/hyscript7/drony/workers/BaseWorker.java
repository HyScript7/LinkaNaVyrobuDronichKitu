package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;
import lombok.Getter;

import java.time.Duration;

public abstract class BaseWorker {
    @Getter
    protected final Log logger;
    protected final Sklad sklad;
    protected Thread thread;

    public BaseWorker(Log logger, Sklad sklad) {
        this.logger = logger;
        this.thread = null;
        this.sklad = sklad;
    }

    public void start() throws IllegalStateException {
        if (this.thread != null) {
            throw new IllegalStateException("Worker is already running!");
        }
        this.thread = new Thread(this::work);
        this.thread.setName(logger.getName());
        this.thread.setDaemon(true);
        this.thread.start();
        logger.debug("Thread started!");
    }

    public void stop() throws InterruptedException, IllegalStateException {
        if (this.thread == null) {
            throw new IllegalStateException("Worker is not running!");
        }
        this.thread.interrupt();
        if (this.thread.join(Duration.ofSeconds(5))) {
            this.thread = null;
        } else {
            logger.error("Čekání na zastavení workeru překročilo časový limit.");
        }
    }

    protected abstract void work();
}
