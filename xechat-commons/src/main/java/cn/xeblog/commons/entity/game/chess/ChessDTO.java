package cn.xeblog.commons.entity.game.chess;

import cn.xeblog.commons.entity.game.GameDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/6/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChessDTO extends GameDTO {

    private int x;
    private int y;
    private int type;

}
