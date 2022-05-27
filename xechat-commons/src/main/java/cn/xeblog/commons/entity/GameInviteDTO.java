package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2022/5/25 11:44 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameInviteDTO {

    /**
     * 被邀请玩家
     */
    private String playerId;

}
