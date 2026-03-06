package io.gitub.hyscript7.drony.domain;

public class ResourceFactory {
    public Material createHlinik() {
        return new Material("Hliník");
    }

    public Material createPlast() {
        return new Material("Plast");
    }

    public Material createChips() {
        return new Material("Čipy");
    }

    public Komponenta createRam() {
        return new KomponentaBuilder().setNazev("Rám")
                .addIngredient(createHlinik(), 60)
                .build();
    }

    public Komponenta createSadaVrtuli() {
        return new KomponentaBuilder().setNazev("Sada vrtulí")
                .addIngredient(createPlast(), 30)
                .build();
    }

    public Komponenta createRidiciDeska() {
        return new KomponentaBuilder().setNazev("Řídící deska")
                .addIngredient(createChips(), 2)
                .addIngredient(createHlinik(), 10)
                .addIngredient(createPlast(), 5) // T4 - Plast
                .build();
    }

    public Komponenta createKit() {
        return new KomponentaBuilder().setNazev("Droní kit")
                .addIngredient(createRam(), 1)
                .addIngredient(createSadaVrtuli(), 1)
                .addIngredient(createRidiciDeska(), 1)
                .build();
    }
}
