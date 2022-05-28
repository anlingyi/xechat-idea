package cn.xeblog.commons.entity.game;

import cn.xeblog.commons.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2020/8/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roomId;

    private Game game;

}
