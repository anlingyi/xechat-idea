package cn.xeblog.entity;

import cn.xeblog.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/8/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameInviteDTO extends GameDTO {

    private String id;

    public GameInviteDTO(String id, Game game) {
        this.id = id;
        this.setGame(game);
    }

}
