package cn.xeblog.plugin.game.uno.domain.common;

import java.util.UUID;

public abstract class Entity {
    private final UUID id;

    protected Entity(){
        this(UUID.randomUUID());
    }

    protected Entity(UUID id){
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
