package com.coolfish.websocketonnetty.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author xfh
 */
@Slf4j
public class NettyConfig {
    /**
     * 定义一个channel组，管理所有的channel
     * GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup qcChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 存放用户与Chanel的对应信息，用于给指定用户发送消息 instModel:instId:username  admin  --2 channels
     * <p>
     * Collection<CopyOnWriteArrSet></> qcUserChannelMap.values()
     * qcUserChannelMap.entrySet()
     */
    private static ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> qcUserChannelMap = new ConcurrentHashMap<>();


    /**
     * 用来存放所有的channel channel.id().asShortText() --2 channels
     * allChannels.forEach(channel->{
     * pushChannel
     * })
     */
    private static ConcurrentHashMap<String, Channel> allChannels = new ConcurrentHashMap<>();


    private NettyConfig() {
    }

    public static ConcurrentHashMap<String, Channel> getAllChannels() {
        return allChannels;
    }

    /**
     * 获取channel组
     *
     * @return
     */
    public static ChannelGroup getChannelGroup() {
        return qcChannelGroup;
    }

    /**
     * 获取用户channel map
     *
     * @return
     */
    public static ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> getUserChannelMap() {
        return qcUserChannelMap;
    }

    public static void addChannel(Channel channel) {
        qcChannelGroup.add(channel);
        allChannels.put(channel.id().asShortText(), channel);
    }

    public static void addKeyChannel(String key, Channel channel) {
        qcChannelGroup.add(channel);
        ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> userChannelMap = getUserChannelMap();
        if (!userChannelMap.containsKey(key)) {
            CopyOnWriteArraySet<Channel> keyChannels = new CopyOnWriteArraySet<>();
            keyChannels.add(channel);
            userChannelMap.put(key, keyChannels);
        } else {
            CopyOnWriteArraySet<Channel> hasKeyChannels = userChannelMap.get(key);
            hasKeyChannels.add(channel);
        }
        if (!allChannels.contains(channel)) {
            allChannels.put(channel.id().asShortText(), channel);
        }
        log.info("调用addKeyChannel方法，key = {}", key);
    }

    /**
     * 删除Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        qcChannelGroup.remove(channel);
        if (allChannels.containsKey(channel.id().asShortText())) {
            allChannels.remove(channel.id().asShortText(), channel);
        }
        ConcurrentHashMap<String, CopyOnWriteArraySet<Channel>> userChannelMap = getUserChannelMap();
        Set<Map.Entry<String, CopyOnWriteArraySet<Channel>>> entries = userChannelMap.entrySet();
        if (entries.size() > 0) {
            entries.forEach(kv -> {
                CopyOnWriteArraySet<Channel> channels = kv.getValue();
                if (channels.contains(channel)) {
                    channels.removeIf(channel1 -> channel1.equals(channel));
                }
            });
        }

        //qcUserChannelMap.remove(channel.id().asShortText(), channel);
    }

    public static void removeKeyChannel(String key, Channel channel) {
        qcChannelGroup.remove(channel);
        CopyOnWriteArraySet<Channel> channelSet = qcUserChannelMap.get(key);
        if (channelSet != null && channelSet.size() > 0 && channelSet.contains(channel)) {
            channelSet.remove(channel);
            if (allChannels.contains(channel)) {
                allChannels.remove(channel.id().asShortText(), channel);
            }
        }
    }

    /**
     * 根据channelId查找Channel
     *
     * @param channelShortId
     * @return CopyOnWriteArraySet<Channel>
     */
    public static Channel findChannel(String channelShortId) {
        ConcurrentHashMap<String, Channel> allChannels = getAllChannels();
        if (allChannels.containsKey(channelShortId)) {
            return allChannels.get(channelShortId);
        }
        return null;
    }

    /**
     * 根据key查找Channel
     *
     * @param key instModel:instId:username
     * @return
     */
    public static CopyOnWriteArraySet<Channel> findChannelByKey(String key) {
        CopyOnWriteArraySet<Channel> channels = qcUserChannelMap.get(key);
        if (channels != null && channels.size() > 0) {
            return channels;
        }
        return null;
    }
}
