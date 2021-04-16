package cn.xeblog.entity;

import cn.xeblog.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/6/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GobangDTO extends GameDTO {

    private int x;
    private int y;
    private int type;
    private int status;

    @Override
    public Game getGame() {
        return Game.GOBANG;
    }
}
