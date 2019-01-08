package com.xianglin.game.web.landlords.load.balance;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author yefei
 */
@Component
@ConfigurationProperties(prefix="landlord.room.server")
public class RandomLoadBalancer implements LoadBalancer {

    private List<String> address;

    public void setAddress(List<String> address) {
        this.address = address;
    }

    private List<Channel> getChannels() {
        List<Channel> channels = new ArrayList<>();
        for (String s : address) {
            Channel channel = new Channel();
            channel.setAddress(s);
            channel.setWeight(50);
            channels.add(channel);
        }
        return channels;
    }

    @Override
    public Channel select() {
        List<Channel> list = getChannels();
        if (list == null || list.size() == 0) {
            return null;
        }
        Channel[] channelGroups = new Channel[list.size()];
        list.toArray(channelGroups);

        if (channelGroups.length == 1) {
            return channelGroups[0];
        }

        boolean sameWeight = true;

        for (int i = 1; i < channelGroups.length && sameWeight; i++) {
            sameWeight = (channelGroups[0].getWeight() == channelGroups[i].getWeight());
        }

        int sumWeight = 0;
        for (int i = 0; i < channelGroups.length; i++) {
            sumWeight += channelGroups[i].getWeight();
        }

        Random random = ThreadLocalRandom.current();

        if (sameWeight) {
            return channelGroups[random.nextInt(channelGroups.length)];
        } else {
            int offset = random.nextInt(sumWeight);
            for (Channel channelGroup : channelGroups) {
                offset -= channelGroup.getWeight();
                if (offset < 0) {
                    return channelGroup;
                }
            }
        }
        return channelGroups[random.nextInt(channelGroups.length)];
    }
}
