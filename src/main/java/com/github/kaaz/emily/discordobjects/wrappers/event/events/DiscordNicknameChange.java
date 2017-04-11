package com.github.kaaz.emily.discordobjects.wrappers.event.events;

import com.github.kaaz.emily.discordobjects.wrappers.Guild;
import com.github.kaaz.emily.discordobjects.wrappers.User;
import com.github.kaaz.emily.discordobjects.wrappers.event.BotEvent;
import sx.blah.discord.handle.impl.events.NickNameChangeEvent;

/**
 * Made by nija123098 on 3/31/2017.
 */
public class DiscordNicknameChange implements BotEvent {
    private NickNameChangeEvent event;
    public DiscordNicknameChange(NickNameChangeEvent event) {
        this.event = event;
    }
    public Guild getGuild(){
        return Guild.getGuild(this.event.getGuild());
    }
    public User getUser(){
        return User.getUser(this.event.getUser());
    }
    public String getOldUsername(){
        return this.event.getOldNickname().orElse(null);
    }
    public String getNewUsername(){
        return this.event.getNewNickname().orElse(null);
    }
}