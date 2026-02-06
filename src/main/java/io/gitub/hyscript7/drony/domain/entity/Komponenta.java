package io.gitub.hyscript7.drony.domain.entity;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
public class Komponenta extends Material {
    private final Map<Material, Integer> recept;

    public Komponenta(String nazev, Map<Material, Integer> recept) {
        super(nazev);
        this.recept = Collections.unmodifiableMap(recept);
    }

    public static KomponentaBuilder builder() {
        return new KomponentaBuilder();
    }

    public static class KomponentaBuilder {
        private String nazev;
        private final Map<Material, Integer> recept;

        public KomponentaBuilder(String nazev) {
            this();
            this.nazev = nazev;
        }

        public KomponentaBuilder() {
            this.recept = new HashMap<>();
        }

        public KomponentaBuilder setNazev(String nazev) {
            this.nazev = nazev;
            return this;
        }

        public KomponentaBuilder addIngredient(Material material, int amount) {
            this.recept.put(material, amount);
            return this;
        }

        public KomponentaBuilder reset() {
            this.nazev = null;
            this.recept.clear();
            return this;
        }

        public Komponenta build() {
            return new Komponenta(nazev, recept);
        }
    }
}
