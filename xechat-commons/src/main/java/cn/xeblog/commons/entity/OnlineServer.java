package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 鱼塘服务器
 *
 * @author nn200433
 * @date 2022-07-12 012 08:11:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineServer {

    /**
     * 鱼塘名字
     */
    private String name;

    /**
     * 鱼塘ip
     */
    private String ip;

    /**
     * 鱼塘端口
     */
    private Integer port;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OnlineServer that = (OnlineServer) o;
        return Objects.equals(ip, that.ip) && Objects.equals(port, that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
