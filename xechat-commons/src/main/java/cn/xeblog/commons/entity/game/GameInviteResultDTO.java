package cn.xeblog.commons.entity.game;

import cn.xeblog.commons.enums.InviteStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/6/14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameInviteResultDTO {

    private InviteStatus status;

    private GameRoom gameRoom;

    private String playerId;

    public GameInviteResultDTO(InviteStatus status) {
        this.status = status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = InviteStatus.valueOf(status);
    }

}
