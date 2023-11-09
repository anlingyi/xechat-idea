package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.CountEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author 鼓励师
 * @date 2023/11/8 17:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsMsgDTO implements Serializable {

    private CountEnum countEnum;

    private boolean toAll;
    private boolean showDetail;
    private int showSize;

}
