package com.exorcity.score;

public enum Rating {
    GHOST_FREE("Ghost Free"),
    CALM_SPIRITS("Calm Spirits"),
    HAUNTED("Haunted"),
    CURSED("Cursed");

    private final String label;

    Rating(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
