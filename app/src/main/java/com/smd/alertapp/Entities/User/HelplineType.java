package com.smd.alertapp.Entities.User;

public enum HelplineType {
    WOMEN_HELPLINE("1043"),
    RESCUE_1122("1122"),
    POLICE("15"),
    FIRE_BRIGADE("16"),
    COUNTER_TERRORISM_CTD("080011111"),
    HUMAN_RIGHTS_HELPLINE("1099");


    private final String value;

    HelplineType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
