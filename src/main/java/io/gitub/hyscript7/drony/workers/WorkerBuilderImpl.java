package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

public class WorkerBuilderImpl implements WorkerBuilder {
    private static final String DEFAULTNI_ZPRAVA = "čeká na materiál pro: {}";
    private static final String DEFAULTNI_ZPRAVA_VYROBA = "vyrobil KOMPONENTU: {} (celkem={})";

    private Sklad sklad;
    private Komponenta komponenta;
    private String zpravaPriCekani;
    private String zpravaPriVyrobeni;
    private Counter counter;
    private Log log;

    @Override
    public WorkerBuilder reset() {
        this.sklad = null;
        this.komponenta = null;
        this.zpravaPriCekani = DEFAULTNI_ZPRAVA;
        this.zpravaPriVyrobeni = DEFAULTNI_ZPRAVA_VYROBA;
        this.counter = null;
        this.log = null;
        return this;
    }

    @Override
    public WorkerBuilder sklad(Sklad sklad) {
        this.sklad = sklad;
        return this;
    }

    @Override
    public WorkerBuilder komponenta(Komponenta komponenta) {
        this.komponenta = komponenta;
        return this;
    }

    @Override
    public WorkerBuilder zpravaPriCekani(String zprava) {
        this.zpravaPriCekani = zprava;
        return this;
    }

    @Override
    public WorkerBuilder zpravaPriSestaveni(String zprava) {
        this.zpravaPriVyrobeni = zprava;
        return this;
    }

    @Override
    public WorkerBuilder pocitadlo(Counter counter) {
        this.counter = counter;
        return this;
    }

    @Override
    public WorkerBuilder logger(Log log) {
        this.log = log;
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
        return new Worker(log, komponenta, sklad, zpravaPriCekani, zpravaPriVyrobeni, counter);
    }
}
