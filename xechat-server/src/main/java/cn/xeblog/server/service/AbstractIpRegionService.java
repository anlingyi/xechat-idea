package cn.xeblog.server.service;

import cn.xeblog.server.config.IpRegionProperties;

/**
 * IP地区抽象服务实现
 *
 * @author nn200433
 * @date 2022年01月10日 0010 15:51:33
 */
public abstract class AbstractIpRegionService {

    protected IpRegionProperties ipRegionProperties;

    public AbstractIpRegionService(IpRegionProperties ipRegionProperties) {
        this.ipRegionProperties = ipRegionProperties;
    }

}
