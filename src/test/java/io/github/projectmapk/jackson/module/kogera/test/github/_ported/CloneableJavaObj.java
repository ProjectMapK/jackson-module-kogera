package io.github.projectmapk.jackson.module.kogera.test.github._ported;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CloneableJavaObj implements Cloneable {
    public final String id;

    @JsonCreator
    public CloneableJavaObj(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
