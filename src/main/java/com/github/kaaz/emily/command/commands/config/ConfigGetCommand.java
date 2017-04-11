package com.github.kaaz.emily.command.commands.config;

import com.github.kaaz.emily.command.AbstractCommand;
import com.github.kaaz.emily.command.InvocationObjectGetter;
import com.github.kaaz.emily.command.anotations.Command;
import com.github.kaaz.emily.command.anotations.Context;
import com.github.kaaz.emily.command.anotations.Convert;
import com.github.kaaz.emily.config.*;
import com.github.kaaz.emily.discordobjects.helpers.MessageMaker;
import com.github.kaaz.emily.discordobjects.wrappers.*;
import com.github.kaaz.emily.exeption.ArgumentException;
import com.github.kaaz.emily.perms.BotRole;

/**
 * Made by nija123098 on 4/2/2017.
 */
public class ConfigGetCommand extends AbstractCommand {
    public ConfigGetCommand(ConfigCommand command) {
        super(command, "get", null, BotRole.USER, null, null, null);
    }
    @Command
    public <T extends Configurable> void command(@Convert AbstractConfig<?, T> config, @Convert(optional = true) T target, MessageMaker maker, @Context(softFail = true) Track track, @Context(softFail = true) Playlist playlist, User user, Channel channel, @Context(softFail = true) GuildUser guildUser, @Context(softFail = true) Guild guild){
        if (config.getConfigLevel() == ConfigLevel.GLOBAL){
            target = (T) GlobalConfigurable.GLOBAL;
        }
        if (target == null){
            if (config.getConfigLevel() == ConfigLevel.ALL || config.getConfigLevel() == ConfigLevel.ROLE){
                throw new ArgumentException("Can not infer a argument from a config that effects a " + config.getConfigLevel().name() + " type");
            }
            if (config.getConfigLevel() == ConfigLevel.TRACK && track == null){// might not be used
                throw new ArgumentException("Can not infer a track when no track is playing");
            }
            target = (T) InvocationObjectGetter.getTypeOf(config.getConfigLevel().getType(), track, playlist, user, channel, guildUser, guild);
            if (target == null){
                throw new ArgumentException("No inferred argument type: " + config.getConfigLevel().name());
            }
        }else if (!config.getConfigLevel().isAssignableFrom(target.getConfigLevel())){
            if (config.getConfigLevel() == ConfigLevel.USER && target.getConfigLevel() == ConfigLevel.GUILD_USER){
                target = (T) ((GuildUser) target).getUser();
            }else if (config.getConfigLevel() == ConfigLevel.GUILD_USER && target.getConfigLevel() == ConfigLevel.USER){
                if (guild == null){
                    throw new ArgumentException("Configs that effect guild users must be used within a guild");
                }
                target = (T) GuildUser.getGuildUser(guild, user);
            }else{
                throw new ArgumentException("The command and the configurable are not of the same type");
            }
        }
        maker.appendRaw(config.getExteriorValue(target));
    }
}