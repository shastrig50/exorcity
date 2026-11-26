package com.exorcity.user;

public enum Role {
    CITIZEN,
    SOCIETY_ADMIN,
    CORPORATE_ADMIN,
    SHOP_OWNER,
    WARD_OFFICER;

    public boolean isEntityAdmin() {
        return this == SOCIETY_ADMIN || this == CORPORATE_ADMIN || this == SHOP_OWNER;
    }
}
