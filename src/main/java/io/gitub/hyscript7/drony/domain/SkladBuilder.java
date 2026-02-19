package io.gitub.hyscript7.drony.domain;

import java.util.HashMap;
import java.util.Map;

public class SkladBuilder {
    private final Map<Material, Integer> initialMaterials;

    public SkladBuilder() {
        this.initialMaterials = new HashMap<>();
    }

    public SkladBuilder reset() {
        this.initialMaterials.clear();
        return this;
    }

    public SkladBuilder addInitialMaterial(Material material, int amount) {
        initialMaterials.put(material, initialMaterials.getOrDefault(material, 0) + amount);
        return this;
    }

    public Sklad build() {
        return new Sklad(initialMaterials);
    }
}
