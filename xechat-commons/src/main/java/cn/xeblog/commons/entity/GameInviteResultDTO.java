package cn.xeblog.commons.entity;

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
public class GameInviteResultDTO extends GameDTO {

    private InviteStatus status;

    public GameInviteResultDTO(GameDTO gameDTO, InviteStatus status) {
        super(gameDTO.getOpponentId(), gameDTO.getGame());
        this.status = status;
    }

}
