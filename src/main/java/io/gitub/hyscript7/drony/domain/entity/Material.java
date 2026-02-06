package io.gitub.hyscript7.drony.domain.entity;

import lombok.Getter;

@Getter
public class Material {
    private final String nazev;

    public Material(String nazev) {
        this.nazev = nazev;
    }
}
