package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;

public interface ComponentWorkerBuilder {
    ComponentWorkerBuilder reset();

    ComponentWorkerBuilder sklad(Sklad sklad);

    ComponentWorkerBuilder komponenta(Komponenta komponenta);

    ComponentWorkerBuilder zpravaPriCekani(String zprava);

    ComponentWorkerBuilder zpravaPriSestaveni(String zprava);

    ComponentWorkerBuilder pocitadlo(Counter counter);

    ComponentWorkerBuilder logger(Log logger);

    ComponentWorkerBuilder threshold(int threshold);

    ComponentWorkerBuilder successDelay(Duration delay);

    BaseWorker getResult();
}
