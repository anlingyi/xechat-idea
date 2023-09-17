package cn.xeblog.commons.entity;

import cn.hutool.core.map.MapUtil;
import cn.xeblog.commons.constants.IpConstants;
import cn.xeblog.commons.enums.Permissions;
import cn.xeblog.commons.enums.Platform;
import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.Channel;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
@ToString
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * uuid
     */
    @Getter
    @Setter
    private String uuid;

    /**
     * 用户ID
     */
    @Getter
    @Setter
    private String id;

    /**
     * 用户昵称
     */
    @Getter
    @Setter
    private String username;

    /**
     * 用户状态
     */
    @Getter
    @Setter
    private UserStatus status;

    /**
     * 地区简称
     */
    @Getter
    @Setter
    private String shortRegion;

    /**
     * 用户IP
     */
    @Getter
    @Setter
    private transient String ip;

    /**
     * 用户所在区域
     */
    @Getter
    @Setter
    private transient IpRegion region;

    /**
     * 用户角色
     */
    @Getter
    @Setter
    private Role role;

    /**
     * 用户权限
     */
    @Getter
    @Setter
    private int permit;

    @Getter
    @Setter
    private Platform platform;

    /**
     * 通道
     */
    @Getter
    private transient Channel channel;

    public enum Role {
        /**
         * 管理员
         */
        ADMIN,
        /**
         * 用户
         */
        USER
    }

    public User(String id, String username, UserStatus status, String ip, IpRegion region, Channel channel) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.ip = ip;
        this.region = region;
        this.channel = channel;
        this.platform = Platform.IDEA;
        this.shortRegion = MapUtil.getStr(IpConstants.SHORT_PROVINCE, region.getProvince(), region.getCountry());
    }

    public void send(Response response) {
        if (channel == null) {
            return;
        }

        channel.writeAndFlush(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 添加权限
     *
     * @param permissions
     */
    public void addPermit(Permissions permissions) {
        this.permit |= permissions.getValue();
    }

    /**
     * 移除权限
     *
     * @param permissions
     */
    public void removePermit(Permissions permissions) {
        if (hasPermit(permissions)) {
            this.permit ^= permissions.getValue();
        }
    }

    /**
     * 是否存在权限
     *
     * @param permissions
     * @return
     */
    public boolean hasPermit(Permissions permissions) {
        int value = permissions.getValue();
        return (this.permit & value) == value;
    }

    /**
     * 是否是管理员
     *
     * @return
     */
    public boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

}
