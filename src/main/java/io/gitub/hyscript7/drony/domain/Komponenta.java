package io.gitub.hyscript7.drony.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Getter
public class Komponenta extends Material {
    private final Map<Material, Integer> recept;

    public Komponenta(String nazev, Map<Material, Integer> recept) {
        super(nazev);
        this.recept = Collections.unmodifiableMap(recept);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Komponenta that = (Komponenta) o;
        return Objects.equals(recept, that.recept);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), recept);
    }
}
