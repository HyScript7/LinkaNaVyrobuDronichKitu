package io.gitub.hyscript7.drony.domain.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Sklad {
    private final ConcurrentMap<Material, Integer> materials;

    public Sklad() {
        this.materials = new ConcurrentHashMap<>();
    }

    public synchronized boolean procure(Map<Material, Integer> requiredMaterials) {
        for (Map.Entry<Material, Integer> required : requiredMaterials.entrySet()) {
            if (!materials.containsKey(required.getKey())) {
                return false;
            }
            if (materials.get(required.getKey()) < required.getValue()) {
                return false;
            }
        }
        for (Map.Entry<Material, Integer> required : requiredMaterials.entrySet()) {
            materials.put(required.getKey(), materials.get(required.getKey()) - required.getValue());
        }
        return true;
    }
}
