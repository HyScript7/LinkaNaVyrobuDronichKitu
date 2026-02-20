package io.gitub.hyscript7.drony.factory;

import io.gitub.hyscript7.drony.domain.ResourceFactory;
import io.gitub.hyscript7.drony.domain.SkladBuilder;

public class SkladDirector {
    private final ResourceFactory resourceFactory;

    public SkladDirector(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    public void createDefaultSklad(SkladBuilder skladBuilder) {
        skladBuilder.reset()
                .addInitialMaterial(resourceFactory.createHlinik(), 10000)
                .addInitialMaterial(resourceFactory.createPlast(), 10000)
                .addInitialMaterial(resourceFactory.createChips(), 1000)
                .addInitialMaterial(resourceFactory.createRam(), 0)
                .addInitialMaterial(resourceFactory.createSadaVrtuli(), 0)
                .addInitialMaterial(resourceFactory.createRidiciDeska(), 0)
                .addInitialMaterial(resourceFactory.createKit(), 0);
    }
}
