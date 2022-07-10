package cn.xeblog.commons.entity.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 城市信息 实体类
 *
 * @author nn200433
 * @date 2022-04-20 01:42:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityInfo implements Serializable {

    /**
     * poi唯一编号
     */
    private String locationId;

    /**
     * 区域编码（邮编）
     */
    private String adCode;

    /**
     * 市/区县
     */
    private String locationName;

    /**
     * 省
     */
    private String admName1;

    /**
     * 市/自治州
     */
    private String admName2;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

}
