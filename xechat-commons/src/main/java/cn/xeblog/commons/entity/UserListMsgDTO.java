package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author anlingyi
 * @date 2022/5/26 4:27 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListMsgDTO implements Serializable {

    /**
     * 在线用户列表
     */
    List<User> userList;

}
