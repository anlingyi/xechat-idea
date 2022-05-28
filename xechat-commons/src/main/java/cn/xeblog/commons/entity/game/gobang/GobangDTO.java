package cn.xeblog.commons.entity.game.gobang;

import cn.xeblog.commons.entity.game.GameDTO;
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

}
