package io.gitub.hyscript7.drony.domain;

import java.util.HashMap;
import java.util.Map;

public class SkladBuilderImpl implements SkladBuilder {
    private final Map<Material, Integer> initialMaterials;

    public SkladBuilderImpl() {
        this.initialMaterials = new HashMap<>();
    }

    @Override
    public SkladBuilder reset() {
        this.initialMaterials.clear();
        return this;
    }

    @Override
    public SkladBuilder addInitialMaterial(Material material, int amount) {
        initialMaterials.put(material, initialMaterials.getOrDefault(material, 0) + amount);
        return this;
    }

    public Sklad build() {
        return new Sklad(initialMaterials);
    }
}
