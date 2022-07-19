package cn.xeblog.server.util;


import cn.hutool.core.util.ObjectUtil;
import cn.xeblog.server.constant.IpConstants;
import cn.xeblog.server.entity.IpRegion;
import cn.xeblog.server.service.IpRegionService;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

/**
 * ip地址工具类
 *
 * @author nn200433
 * @date 2022-01-10 11:32:24
 */
public class IpUtil {

    private static IpRegionService ipRegionService;

    public IpUtil(IpRegionService param) {
        ipRegionService = param;
    }

    /**
     * 获取IP地区实体
     *
     * @param ip IP地址
     * @return {@link IpRegion }
     * @author nn200433
     */
    public static IpRegion getRegionByIp(String ip) {
        if (ObjectUtil.isNull(ipRegionService)) {
            return IpRegion.builder().ip(ip).country(IpConstants.IP_UN_KNOW_DEFAULT_REGION).build();
        }
        return ipRegionService.getRegion(ip);
    }

    /**
     * 获取IP地区名字
     *
     * @param ip IP地址
     * @return {@link String }
     * @author nn200433
     */
    public static String getRegionStrByIp(String ip) {
        return getRegionByIp(ip).toString();
    }

    /**
     * 通过ctx获取ip地址
     *
     * @param ctx ctx
     * @return {@link String }
     * @author song_jx
     */
    public static String getIpByCtx(ChannelHandlerContext ctx) {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String hostAddress = ipSocket.getAddress().getHostAddress();
        return hostAddress;
    }

    /**
     * 通过上下文通道获取地址
     *
     * @param ctx ctx
     * @return {@link String }
     * @author nn200433
     */
    public static IpRegion getRegionByCtx(ChannelHandlerContext ctx) {
        return getRegionByIp(getIpByCtx(ctx));
    }

    /**
     * 通过上下文通道获取省份
     *
     * @param ctx ctx
     * @return {@link String }
     * @author nn200433
     */
    public static String getProvinceByCtx(ChannelHandlerContext ctx) {
        return getRegionByCtx(ctx).getProvince();
    }

    /**
     * 通过上下文通道获取省份简称
     *
     * @param ctx ctx
     * @return {@link String }
     * @author nn200433
     */
    public static String getShortProvinceByCtx(ChannelHandlerContext ctx) {
        return IpConstants.SHORT_PROVINCE.get(getProvinceByCtx(ctx));
    }

}
