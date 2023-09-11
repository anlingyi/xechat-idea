package cn.xeblog.plugin.game.uno.application.dto;

import java.io.Serializable;
import java.util.UUID;

public class PlayerInfoDTO implements Serializable {
    private final UUID id;
    private final String name;

    public PlayerInfoDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
