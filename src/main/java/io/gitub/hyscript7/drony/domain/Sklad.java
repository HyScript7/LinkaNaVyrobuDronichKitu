package io.gitub.hyscript7.drony.domain;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Sklad {
    private final ConcurrentMap<Material, Integer> materials;

    public Sklad(Map<Material, Integer> initialMaterials) {
        this();
        this.materials.putAll(initialMaterials);
    }

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

    public synchronized void supply(Material material, int amount) {
        this.materials.put(material, this.materials.getOrDefault(material, 0) + amount);
    }

    /**
     * @return Vrátí neměnnou mapu (<b>View</b>) se skladem
     */
    public Map<Material, Integer> getStatus() {
        return Collections.unmodifiableMap(materials);
    }
}
