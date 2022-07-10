package cn.nn200433.weather.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.nn200433.weather.service.CityService;
import cn.xeblog.commons.entity.weather.CityInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 城市信息 实现
 *
 * @author nn200433
 * @date 2022-04-19 01:55:55
 */
@Slf4j
public class HeFengCityServiceImpl implements CityService {

    private static final List<CityInfo> CITY_INFO_LIST = JSONUtil.toBean(ResourceUtil.readUtf8Str("db/China-City-List-latest.json"),
            new TypeReference<List<CityInfo>>(){}, Boolean.FALSE);

    @Override
    public List<CityInfo> getList(String keyword) {
        return CITY_INFO_LIST.parallelStream().filter(
                cityInfo -> StrUtil.contains(cityInfo.getLocationName(), keyword) ||
                        StrUtil.contains(cityInfo.getAdmName1(), keyword) ||
                        StrUtil.contains(cityInfo.getAdmName2(), keyword)
        ).collect(Collectors.toList());
    }

    @Override
    public CityInfo getOne(String keyword) {
        List<CityInfo> list = getList(keyword);
        if (CollUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

}
