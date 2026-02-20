package io.gitub.hyscript7.drony.crosscutting;

import io.gitub.hyscript7.drony.domain.Material;
import io.gitub.hyscript7.drony.domain.Sklad;
import io.gitub.hyscript7.drony.domain.SkladBuilder;

import java.util.HashMap;
import java.util.Map;

public class SkladFxBuilder implements SkladBuilder {

    private final Map<Material, Integer> initialMaterials;

    public SkladFxBuilder() {
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

    public SkladFx build() {
        return new SkladFx(initialMaterials);
    }
}
