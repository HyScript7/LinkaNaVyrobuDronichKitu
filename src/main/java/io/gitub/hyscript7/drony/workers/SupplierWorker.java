package io.gitub.hyscript7.drony.workers;

import io.gitub.hyscript7.drony.Log;
import io.gitub.hyscript7.drony.domain.Komponenta;
import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.domain.Sklad;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

// Skladník
public class SupplierWorker extends BaseWorker {
    private static final ResourceFactory rf = new ResourceFactory();
    private static final List<Material> outputOrderPriority = List.of(
            rf.createHlinik(),
            rf.createPlast(),
            rf.createChips()
    );

    public static record SupplyRange(int min, int max, String unitSuffix) {
    }

    private final Map<Material, SupplyRange> supplySubjects;

    public SupplierWorker(Log logger, Sklad sklad, Map<Material, SupplyRange> supplySubjects) {
        super(logger, sklad);
        this.supplySubjects = supplySubjects;
    }

    @Override
    protected void work() {
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.debug("Thread is interrupted, returning!");
                    return;
                }
                StringBuilder msg = new StringBuilder("doplnil: ");
                // Dobrá zpráva je, že už vím jak funguje ta tabulka
                // Špatná zpráva je, že tady je ta stejná logika a je ošklivá
                for (Map.Entry<Material, SupplyRange> entry : supplySubjects.entrySet().stream().sorted(Comparator.comparingInt(es -> outputOrderPriority.indexOf(es.getKey()))).toList()) {
                    SupplyRange range = entry.getValue();
                    int amount = ThreadLocalRandom.current().nextInt(range.min(), range.max()+1);
                    sklad.supply(entry.getKey(), amount);
                    msg.append(entry.getKey().getNazev()).append("+").append(amount).append(range.unitSuffix()).append(", ");
                }
                logger.info(msg.toString().replaceAll(",\\s$", ""));
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
