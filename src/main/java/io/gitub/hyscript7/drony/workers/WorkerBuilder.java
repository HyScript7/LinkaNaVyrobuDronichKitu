package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Sklad;

public interface WorkerBuilder {
    WorkerBuilder reset();

    WorkerBuilder sklad(Sklad sklad);

    WorkerBuilder komponenta(Komponenta komponenta);

    WorkerBuilder zpravaPriCekani(String zprava);

    WorkerBuilder zpravaPriSestaveni(String zprava);

    WorkerBuilder pocitadlo(Counter counter);

    WorkerBuilder logger(Log logger);

    WorkerBuilder threshold(int threshold);

    BaseWorker getResult();
}
