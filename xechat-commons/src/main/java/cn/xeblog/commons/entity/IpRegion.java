package cn.xeblog.commons.entity;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ip区域
 *
 * @author nn200433
 * @date 2022年01月10日 0010 14:53:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IpRegion {

    /**
     * ip
     */
    private String ip;

    /**
     * 国家
     */
    private String country;

    /**
     * 省
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区/县
     */
    private String area;

    /**
     * 街道/镇
     */
    private String street;

    /**
     * 村
     */
    private String village;

    /**
     * isp
     */
    private String isp;

    public IpRegion(String ip) {
        this.ip = ip;
    }

    public String getProvince() {
        return StrUtil.blankToDefault(province, "未知");
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        // sb.append(ip)
        //         .append(" ")
        sb.append((null == country || "0".equals(country)) ? "" : country)
                .append(" ")
                .append((null == province || "0".equals(province)) ? "" : province)
                .append(" ")
                .append((null == city || "0".equals(city)) ? "" : city)
                .append(" ")
                .append((null == area || "0".equals(area)) ? "" : area)
                .append(" ")
                .append((null == street || "0".equals(street)) ? "" : street)
                .append(" ")
                .append((null == village || "0".equals(village)) ? "" : village)
                .append(" ")
                .append((null == isp || "0".equals(isp)) ? "" : isp);
        return sb.toString().trim();
    }

}
