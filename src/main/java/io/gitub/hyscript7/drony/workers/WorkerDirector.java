package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.domain.Sklad;

public class WorkerDirector {
    private final Sklad sklad;
    private final ResourceFactory resourceFactory;

    public WorkerDirector(Sklad sklad, ResourceFactory resourceFactory) {
        this.sklad = sklad;
        this.resourceFactory = resourceFactory;
    }

    public void buildRamWorker(WorkerBuilder builder) {
        builder.reset().sklad(sklad).logger(new Log("VYROBCE-RAM")).komponenta(resourceFactory.createRam()).pocitadlo(new CounterImpl()).threshold(30);
    }

    public void buildSadaVrtuliWorker(WorkerBuilder builder) {
        builder.reset().sklad(sklad).logger(new Log("VYROBCE-VRTULE")).komponenta(resourceFactory.createSadaVrtuli()).pocitadlo(new CounterImpl()).threshold(30);
    }

    public void buildRidiciDeskaWorker(WorkerBuilder builder) {
        builder.reset().sklad(sklad).logger(new Log("VYROBCE-DESKA")).komponenta(resourceFactory.createRidiciDeska()).pocitadlo(new CounterImpl()).threshold(30);
    }

    public void buildKitWorker(WorkerBuilder builder) {
        builder.reset().sklad(sklad).zpravaPriCekani("čeká na komponenty").zpravaPriSestaveni("sestavil KIT #{}").komponenta(resourceFactory.createKit());
    }
}
