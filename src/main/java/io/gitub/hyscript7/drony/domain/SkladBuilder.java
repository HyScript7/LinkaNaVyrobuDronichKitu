package io.gitub.hyscript7.drony.domain;

public interface SkladBuilder {
    SkladBuilder reset();

    SkladBuilder addInitialMaterial(Material material, int amount);
}
