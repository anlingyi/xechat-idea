package cn.xeblog.server.config;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * IP区域配置
 *
 * @author nn200433
 * @date 2021年12月14日 0014 19:48:59
 */
@Getter
@Setter
@Builder
public class IpRegionProperties {

    /**
     * ip2region数据库地址
     * <p>
     * 下载IP地址库 https://gitee.com/lionsoul/ip2region/tree/master/data
     */
    private String ip2regionDbPath;

    /**
     * 纯真数据库路径
     * <p>
     * 下载IP地址库 https://www.cz88.net/
     * 下载后解压并安装，数据库在安装目录下：qqwry.dat
     */
    private String ip2QQWryDbPath;

}
