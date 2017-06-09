package com.github.kaaz.emily.discordobjects.wrappers;

import com.github.kaaz.emily.discordobjects.exception.ErrorWrapper;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Made by nija123098 on 3/7/2017.
 */
public class VoiceChannel extends Channel {
    static VoiceChannel getVoiceChannel(String id){
        return (VoiceChannel) Channel.getChannel(id);
    }
    public static VoiceChannel getVoiceChannel(IVoiceChannel channel){
        return (VoiceChannel) Channel.getChannel(channel);
    }
    static List<VoiceChannel> getVoiceChannels(List<IVoiceChannel> iVoiceChannel){
        List<VoiceChannel> users = new ArrayList<>(iVoiceChannel.size());
        iVoiceChannel.forEach(iUser -> users.add(getVoiceChannel(iUser)));
        return users;
    }
    VoiceChannel(IVoiceChannel channel) {
        super(channel);
    }
    public IVoiceChannel channel(){
        return (IVoiceChannel) this.reference.get();
    }
    // THE FOLLOWING ARE WRAPPER METHODS
    public int getUserLimit() {
        return channel().getUserLimit();
    }

    public int getBitrate() {
        return channel().getBitrate();
    }

    public void edit(String name, int position, int bitrate, int userLimit) {
        ErrorWrapper.wrap(() -> channel().edit(name, position, bitrate, userLimit));
    }

    public void changeBitrate(int bitrate) {
        ErrorWrapper.wrap(() -> channel().changeBitrate(bitrate));
    }

    public void changeUserLimit(int limit) {
        ErrorWrapper.wrap(() -> channel().changeUserLimit(limit));
    }

    public void join() {
        ErrorWrapper.wrap(() -> channel().join());
    }

    public void leave() {
        channel().leave();
    }

    public boolean isConnected() {
        return channel().isConnected();
    }

    public VoiceChannel copy() {
        return getVoiceChannel(channel().copy());
    }

    public List<User> getConnectedUsers() {
        return User.getUsers(channel().getConnectedUsers());
    }
}