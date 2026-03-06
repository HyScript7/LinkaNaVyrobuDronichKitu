package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;

public class ComponentWorkerBuilderImpl implements ComponentWorkerBuilder {
    private static final String DEFAULTNI_ZPRAVA = "čeká na materiál pro: [TYP]";
    private static final String DEFAULTNI_ZPRAVA_VYROBA = "vyrobil KOMPONENTU: [TYP] (celkem={#})";

    private Sklad sklad;
    private Komponenta komponenta;
    private String zpravaPriCekani;
    private String zpravaPriVyrobeni;
    private Counter counter;
    private Log log;
    private int upperBoundThreshold;
    private Duration successDelay;

    @Override
    public ComponentWorkerBuilder reset() {
        this.sklad = null;
        this.komponenta = null;
        this.zpravaPriCekani = DEFAULTNI_ZPRAVA;
        this.zpravaPriVyrobeni = DEFAULTNI_ZPRAVA_VYROBA;
        this.counter = null;
        this.log = null;
        this.upperBoundThreshold = Integer.MAX_VALUE;
        this.successDelay = null;
        return this;
    }

    @Override
    public ComponentWorkerBuilder sklad(Sklad sklad) {
        this.sklad = sklad;
        return this;
    }

    @Override
    public ComponentWorkerBuilder komponenta(Komponenta komponenta) {
        this.komponenta = komponenta;
        return this;
    }

    @Override
    public ComponentWorkerBuilder zpravaPriCekani(String zprava) {
        this.zpravaPriCekani = zprava;
        return this;
    }

    @Override
    public ComponentWorkerBuilder zpravaPriSestaveni(String zprava) {
        this.zpravaPriVyrobeni = zprava;
        return this;
    }

    @Override
    public ComponentWorkerBuilder pocitadlo(Counter counter) {
        this.counter = counter;
        return this;
    }

    @Override
    public ComponentWorkerBuilder logger(Log log) {
        this.log = log;
        return this;
    }

    @Override
    public ComponentWorkerBuilder threshold(int threshold) {
        this.upperBoundThreshold = threshold;
        return this;
    }

    @Override
    public ComponentWorkerBuilder successDelay(Duration delay) {
        this.successDelay = delay;
        return this;
    }

    public BaseWorker getResult() {
        if (sklad == null) {
            throw new IllegalStateException("Sklad musí být určen!");
        }
        if (komponenta == null) {
            throw new IllegalStateException("Typ komponenty musí být určen!");
        }
        if (counter == null) {
            throw new IllegalStateException("Počítadlo musí být určeno!");
        }
        if (log == null) {
            throw new IllegalStateException("Logger musí být určen!");
        }
        if (successDelay != null) {
            return new ComponentWorker(log, komponenta, sklad, zpravaPriCekani, zpravaPriVyrobeni, counter, upperBoundThreshold, successDelay);
        } else {
            return new ComponentWorker(log, komponenta, sklad, zpravaPriCekani, zpravaPriVyrobeni, counter, upperBoundThreshold);
        }
    }
}
