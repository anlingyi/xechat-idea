package cn.xeblog.server.config;

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
public class IpRegionProperties {

    /**
     * ip2region数据库地址
     * <p>
     * 下载IP地址库 https://gitee.com/lionsoul/ip2region/tree/master/data
     */
    private String ip2regionDbPath = "D:\\idea_hengfeng\\xechat-idea\\xechat-server\\src\\main\\resources\\db\\ip2region.xdb";

    /**
     * ip2region算法
     *
     * <pre>
     * binary：基于二分查找，基于ip2region.db文件，不需要载入内存，单次查询在0.x毫秒级别。
     * memory：整个数据库全部载入内存，单次查询都在0.1x毫秒内，C语言的客户端单次查询在0.00x毫秒级别
     * btree：基于btree算法，基于ip2region.db文件，不需要载入内存，单词查询在0.x毫秒级别，比binary算法更快。
     * </pre>
     */
    private String ip2regionAlgorithm;

    /**
     * 纯真数据库路径
     * <p>
     * 下载IP地址库 https://www.cz88.net/
     * 下载后解压并安装，数据库在安装目录下：qqwry.dat
     */
    private String ip2QQWryDbPath;

}
