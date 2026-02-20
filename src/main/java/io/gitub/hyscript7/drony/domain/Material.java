package io.gitub.hyscript7.drony.domain;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Material {
    private final String nazev;

    public Material(String nazev) {
        this.nazev = nazev;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return Objects.equals(nazev, material.nazev);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nazev);
    }

    @Override
    public String toString() {
        return nazev;
    }
}
