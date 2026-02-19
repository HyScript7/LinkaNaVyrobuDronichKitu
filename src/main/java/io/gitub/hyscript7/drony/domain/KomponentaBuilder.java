package io.gitub.hyscript7.drony.domain;

import java.util.HashMap;
import java.util.Map;

public class KomponentaBuilder {
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
        if (nazev == null) {
            throw new IllegalStateException("Jméno komponenty nesmí být null!");
        }
        if (recept.isEmpty()) {
            throw new IllegalStateException("Recept komponenty nesmí být prázdný!");
        }
        return new Komponenta(nazev, Map.copyOf(recept));
    }
}
