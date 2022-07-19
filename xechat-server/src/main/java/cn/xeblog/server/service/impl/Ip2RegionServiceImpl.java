package cn.xeblog.server.service.impl;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.server.config.IpRegionProperties;
import cn.xeblog.server.constant.IpConstants;
import cn.xeblog.server.entity.IpRegion;
import cn.xeblog.server.service.AbstractIpRegionService;
import cn.xeblog.server.service.IpRegionService;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;

import java.util.regex.Pattern;

/**
 * ip地址查询 离线1 服务实现
 * <p>
 * https://gitee.com/lionsoul/ip2region
 * ip2region - 准确率99.9%的离线IP地址定位库，0.0x毫秒级查询，ip2region.db数据库只有数MB，提供了java,php,c,python,nodejs,golang,c#等查询绑定和Binary,B树,内存三种查询算法。
 *
 * @author nn200433
 * @date 2022-02-18 09:27:37
 */
@Slf4j
public class Ip2RegionServiceImpl extends AbstractIpRegionService implements IpRegionService {

    /**
     * 正则表达式解析区域
     */
    private static final Pattern REGEX_REGION_PARSE = Pattern.compile("^(?<country>[\\u4e00-\\u9fa5]+|\\d)\\|(?<areaCode>\\d)\\|(?<province>[\\u4e00-\\u9fa5a-zA-Z0-9]+|\\d)\\|(?<city>[\\u4e00-\\u9fa5a-zA-Z0-9]+)\\|(?<ISP>[\\u4e00-\\u9fa5a-zA-Z0-9]+)$");

    /**
     * 未知区域判断
     */
    private static final String UN_KNOW_AREA = "0";

    public Ip2RegionServiceImpl(IpRegionProperties ipRegionProperties) {
        super(ipRegionProperties);
    }

    @Override
    public IpRegion getRegion(String ip) {
        IpRegion ipRegion = new IpRegion(ip);
        try {
            // if (NetUtil.isValidIpV4Address(ip)) {
            //     throw new RuntimeException("无效的IP地址");
            // }

            // 查询IP
            String region = getDataSearcher(ipRegionProperties.getIp2regionDbPath()).search(ip);

            if (StrUtil.isBlank(region)) {
                throw new RuntimeException("解析地理位置异常");
            }

            log.debug("---> ip = {} 结果 = {}", ip, region);

            // 解析装入实体类
            ipRegion = IpRegion.builder()
                    .ip(ip)
                    .country(ReUtil.get(REGEX_REGION_PARSE, region, "country"))
                    .province(ReUtil.get(REGEX_REGION_PARSE, region, "province"))
                    .city(ReUtil.get(REGEX_REGION_PARSE, region, "city"))
                    .isp(ReUtil.get(REGEX_REGION_PARSE, region, "ISP"))
                    .build();

            if (StrUtil.equals(ipRegion.getCountry(), UN_KNOW_AREA)) {
                return IpRegion.builder().ip(ip).country(IpConstants.IP_UN_KNOW_DEFAULT_REGION).build();
            }
            return ipRegion;
        } catch (Exception e) {
            ipRegion.setCountry(IpConstants.IP_UN_KNOW_DEFAULT_REGION);
            log.error("---> ip2region解析 {} 出错: {}", ip, e);
        }

        return ipRegion;
    }

    /**
     * 获取数据
     *
     * @param dbPath 数据库路径
     * @return {@link Searcher }
     * @author nn200433
     */
    private Searcher getDataSearcher(String dbPath) {
        return Singleton.get(Searcher.class.getName(), () -> Searcher.newWithVectorIndex(dbPath, Searcher.loadVectorIndexFromFile(dbPath)));
    }

}
