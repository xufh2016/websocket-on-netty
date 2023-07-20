package com.coolfish.websocketonnetty.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xfh
 */
public class NettyConfigCopy {
    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与Chanel的对应信息，用于给指定用户发送消息
     */
    private static ConcurrentHashMap<String, Channel> userChannelMap = new ConcurrentHashMap<>();

    private NettyConfigCopy() {
    }

    /**
     * 获取channel组
     *
     * @return
     */
    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    /**
     * 获取用户channel map
     *
     * @return
     */
    public static ConcurrentHashMap<String, Channel> getUserChannelMap() {
        return userChannelMap;
    }

    public static void addChannel(Channel channel) {
        channelGroup.add(channel);
        userChannelMap.put(channel.id().asShortText(), channel);
    }

    public static void addKeyChannel(String key, Channel channel) {
        channelGroup.add(channel);
        userChannelMap.put(key , channel);
        System.out.println("key = " + key);
    }

    /**
     * 删除Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        channelGroup.remove(channel);
        userChannelMap.remove(channel.id().asShortText(), channel);
    }

    public static void removeKeyChannel(String key, Channel channel) {
        channelGroup.remove(channel);
        userChannelMap.remove(key, channel);
    }

    /**
     * 根据channelId查找Channel
     *
     * @param channelId
     * @return
     */
    public static Channel findChannel(String channelId) {
        ConcurrentHashMap<String, Channel> userChannelMap = getUserChannelMap();
        return channelGroup.find(userChannelMap.get(channelId).id());
    }

    public static Channel findChannelByKey(String key) {
        Channel channel = userChannelMap.get(key);
        if (channel!=null){
            return channelGroup.find(channel.id());
        }
        return null;
    }
}
