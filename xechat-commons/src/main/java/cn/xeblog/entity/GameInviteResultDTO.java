package cn.xeblog.entity;

import cn.xeblog.enums.InviteStatus;
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

}
