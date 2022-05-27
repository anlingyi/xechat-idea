package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2022/5/25 10:22 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameRoomDTO implements Serializable {

    /**
     * 当前游戏
     */
    private Game game;

    /**
     * 几人房
     */
    private int nums;

}
