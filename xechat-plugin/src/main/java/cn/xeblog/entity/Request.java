package cn.xeblog.entity;

import cn.xeblog.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object body;

    private Action action;
}
