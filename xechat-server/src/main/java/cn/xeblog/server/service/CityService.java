package cn.xeblog.server.service;


import cn.xeblog.commons.entity.weather.CityInfo;

import java.util.List;

/**
 * 城市信息 接口
 *
 * @author nn200433
 * @date 2022-04-19 10:56:15
 */
public interface CityService {

    /**
     * 查找所有可能存在的列表
     *
     * @param keyword 关键字
     * @return {@link List }<{@link CityInfo }>
     * @author nn200433
     */
    public List<CityInfo> getList(String keyword);

    /**
     * 查找并返回一个结果
     *
     * @param keyword 关键字
     * @return {@link CityInfo }
     * @author nn200433
     */
    public CityInfo getOne(String keyword);

}
