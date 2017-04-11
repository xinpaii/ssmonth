package com.github.kaaz.emily.config;

import com.github.kaaz.emily.discordobjects.wrappers.*;
import com.github.kaaz.emily.exeption.DevelopmentException;

/**
 * The enum to represent a type of configurable object.
 * Objects that can be configured must have a type.
 *
 * @author nija123098
 * @since 2.0.0
 */
public enum ConfigLevel {
    /** The type for audio tracks */
    TRACK(Track.class),
    /** The type for any playlist type */
    PLAYLIST(Playlist.class),
    /** The type for a user's config */
    USER(User.class),
    /** The type for a channel's config */
    CHANNEL(Channel.class),
    /** The type for a user's config within a guild */
    GUILD_USER(GuildUser.class),
    /** The type for a role within a guild */
    ROLE(Role.class),
    /** The type for a guild's config */
    GUILD(Guild.class),
    /** The type for global config */
    GLOBAL(GlobalConfigurable.class),
    /** The type for a config that applies to all configurable types */
    ALL(Configurable.class),;
    private Class<? extends Configurable> clazz;
    ConfigLevel(Class<? extends Configurable> clazz) {
        this.clazz = clazz;
    }
    public Class<? extends Configurable> getType(){
        return this.clazz;
    }
    public boolean isAssignableFrom(ConfigLevel level){
        return this == ALL || this == level;
    }
    public static ConfigLevel getLevel(Class<? extends Configurable> clazz){
        for (ConfigLevel level : values()){
            if (level.clazz.isAssignableFrom(clazz)){
                return level;
            }
        }
        throw new DevelopmentException("Class does not have a type: " + clazz.getName());
    }
    public static ConfigLevel getLevel(String args){
        String arg = args.split(" ")[0].toUpperCase();
        try{return ConfigLevel.valueOf(arg);
        }catch(Exception ignored){}
        arg = arg.toUpperCase();
        if (arg.equals("GUILDUSER")){
            return ConfigLevel.GUILD_USER;
        }
        if (arg.equals("GUILD USER")){
            return ConfigLevel.GUILD_USER;
        }
        throw new RuntimeException("No such configurable type");
    }
}