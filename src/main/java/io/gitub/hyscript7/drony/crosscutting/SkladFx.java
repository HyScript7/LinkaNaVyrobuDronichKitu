package io.gitub.hyscript7.drony.crosscutting;

import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.Sklad;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkladFx extends Sklad implements Observable {
    private final List<InvalidationListener> listeners = new ArrayList<>();

    public SkladFx(Map<Material, Integer> initialMaterials) {
        super(initialMaterials);
    }

    public SkladFx() {
    }

    @Override
    public synchronized void supply(Material material, int amount) {
        super.supply(material, amount);
        listeners.forEach(listener -> listener.invalidated(this));
    }

    @Override
    public synchronized boolean procure(Map<Material, Integer> requiredMaterials) {
        try {
            return super.procure(requiredMaterials);
        } finally {
            listeners.forEach(listener -> listener.invalidated(this));
        }
    }

    @Override
    public void addListener(InvalidationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener) {
        listeners.remove(listener);
    }
}
