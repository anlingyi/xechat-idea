package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.WeatherType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 天气
 *
 * @author nn200433
 * @date 2022/07/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO implements Serializable {

    /**
     * 类型
     */
    private WeatherType type;

    /**
     * 位置
     */
    private String location;

    public void setType(WeatherType type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = WeatherType.build(type);
    }

}
