package com.exorcity.spot;

/** Each garbage type is haunted by its own monster. */
public enum GarbageType {
    ROADSIDE_PILE("Trash Zombie", "🧟"),
    OVERFLOWING_BIN("Bin Beast", "👹"),
    PLASTIC_WASTE("Plastic Phantom", "👻"),
    ILLEGAL_DUMP("Dump Demon", "🕷️"),
    LAKE_OR_DRAIN("Swamp Ghoul", "🐸");

    private final String monsterName;
    private final String emoji;

    GarbageType(String monsterName, String emoji) {
        this.monsterName = monsterName;
        this.emoji = emoji;
    }

    public String monsterName() {
        return monsterName;
    }

    public String emoji() {
        return emoji;
    }
}
