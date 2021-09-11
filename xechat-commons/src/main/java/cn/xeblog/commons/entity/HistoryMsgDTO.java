package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author anlingyi
 * @date 2021/9/11 7:00 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryMsgDTO implements Serializable {

    private List<Response> msgList;

}
