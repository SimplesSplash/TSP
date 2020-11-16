package com.company.tsp.core;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RequestSenderName implements EnumClass<String> {

    DISPATCHING("dispatching");

    private String id;

    RequestSenderName(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static RequestSenderName fromId(String id) {
        for (RequestSenderName at : RequestSenderName.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}