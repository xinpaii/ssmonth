package com.github.kaaz.emily.command;

import com.github.kaaz.emily.config.ConfigHandler;
import com.github.kaaz.emily.config.Configurable;
import com.github.kaaz.emily.config.configs.guild.GuildSpecialPermsEnabledConfig;
import com.github.kaaz.emily.config.configs.role.*;
import com.github.kaaz.emily.discordobjects.helpers.MessageHelper;
import com.github.kaaz.emily.discordobjects.wrappers.*;
import com.github.kaaz.emily.discordobjects.wrappers.event.EventDistributor;
import com.github.kaaz.emily.perms.BotRole;
import com.github.kaaz.emily.service.services.MemoryManagementService;
import com.github.kaaz.emily.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author nija123098
 * @since 2.0.0
 */
public class AbstractCommand {
    private final AbstractCommand superCommand;
    private final String name;
    private final BotRole botRole;// can not be private
    private final ModuleLevel module;
    private Method method;
    private Class<?>[] argsTypes;
    private Set<String> emoticonAliases, allNames;
    private long globalUseTime, globalCoolDownTime;
    private List<Guild> guildCoolDowns;
    private List<Channel> channelCoolDowns;
    private List<User> userCoolDowns;
    private List<Configurable.GuildUser> guildUserCoolDowns;
    public AbstractCommand(AbstractCommand superCommand, String name, ModuleLevel module, BotRole botRole, String absoluteAliases, String emoticonAliases, String relativeAliases){
        this.superCommand = superCommand;
        this.name = superCommand == null ? name : superCommand.name + " " + name;
        this.module = superCommand == null ? module : module == null ? superCommand.module : module;
        this.botRole = superCommand == null ? (botRole == null ? BotRole.USER : botRole) : botRole == null ? superCommand.botRole : botRole;
        this.allNames = new HashSet<>();
        this.allNames.add(this.name);
        if (absoluteAliases != null){
            Collections.addAll(this.allNames, absoluteAliases.split(", "));
        }
        if (emoticonAliases != null){
            String[] eAliases = emoticonAliases.split(", ");
            this.emoticonAliases = new HashSet<>(eAliases.length);
            Collections.addAll(this.emoticonAliases, eAliases);
        }else{
            this.emoticonAliases = new HashSet<>(0);
        }
        this.allNames.addAll(this.emoticonAliases);
        if (relativeAliases != null){
            for (String rel : relativeAliases.split(", ")){
                this.superCommand.getNames().forEach(s -> this.allNames.add(s + " " + rel));
            }
        }
        Method[] methods = this.getClass().getMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Command.class)) {
                this.method = m;
                break;
            }
        }
        if (this.method == null){
            Log.log("No method annotated " + Command.class.getSimpleName() + " in command: " + this.getClass().getName());
            return;
        }
        this.argsTypes = this.method.getParameterTypes();
        this.globalCoolDownTime = this.getCoolDown(Configurable.GlobalConfigurable.class);
        long persistence = this.getCoolDown(Guild.class);
        if (persistence != -1){
            this.guildCoolDowns = new MemoryManagementService.ManagedList<>(persistence);
        }
        persistence = this.getCoolDown(Channel.class);
        if (persistence != -1){
            this.channelCoolDowns = new MemoryManagementService.ManagedList<>(persistence);
        }
        persistence = this.getCoolDown(User.class);
        if (persistence != -1){
            this.userCoolDowns = new MemoryManagementService.ManagedList<>(persistence);
        }
        persistence = this.getCoolDown(Configurable.GuildUser.class);
        if (persistence != -1){
            this.guildUserCoolDowns = new MemoryManagementService.ManagedList<>(persistence);
        }
        EventDistributor.register(this);
    }

    /**
     * The method to get a set of all emoticon chars
     * by which the command can be called
     *
     * @return the HashSet of the emoticon's chars
     * that can represent this command
     */
    public Set<String> getEmoticonAliases() {
        return this.emoticonAliases;
    }

    /**
     * A standard getter.
     *
     * @return the name of the command
     */
    public String getName(){
        return this.name;
    }

    /**
     * The method to get the set off all names
     * by which the command goes by
     *
     * @return the HashSet of all names
     * by which the command goes by
     */
    public Set<String> getNames() {
        return this.allNames;
    }

    /**
     * Returns the command's module
     *
     * @return the module of which the command is part of
     */
    public ModuleLevel getModule(){
        return this.module;
    }

    /**
     * A check if the user can use a command in the context
     *
     * @param user the user that is being checked for permission
     * @param guild the guild in which permissions are being checked,
     *              null if there is no guild in the context
     * @return if the user can use this command
     * in the guild, if one exists
     */
    public boolean hasPermission(User user, Guild guild) {
        boolean hasNormalPerm = BotRole.hasRequiredRole(this.botRole, user, guild);
        if (!(this.botRole.ordinal() >= BotRole.GUILD_TRUSTEE.ordinal()) || !ConfigHandler.getSetting(GuildSpecialPermsEnabledConfig.class, guild)){
            boolean disapproved = false;
            for (Role role : user.getRolesForGuild(guild)){
                if (!ConfigHandler.getSetting(SpecialPermsRoleEnable.class, role)){
                    continue;
                }
                if (ConfigHandler.getSetting(PermsCommandWhitelistConfig.class, role).contains(this.getName()) || ConfigHandler.getSetting(PermsModuleWhitelistConfig.class, role).contains(this.getName()) && !ConfigHandler.getSetting(PermsModuleWhitelistExemptionsConfig.class, role).contains(this.getName())){
                    return true;
                }
                if (ConfigHandler.getSetting(PermsCommandBlacklistConfig.class, role).contains(this.getName()) || ConfigHandler.getSetting(PermsModuleWhitelistConfig.class, role).contains(this.getName()) && !ConfigHandler.getSetting(PermsCommandBlacklistConfig.class, role).contains(this.getName())){
                    disapproved = true;
                }
            }
            return !disapproved && hasNormalPerm;
        }else{
            return hasNormalPerm;
        }
    }

    /**
     * A method to check the cool down on a command.
     *
     * @param guild the guild checked for rate limiting
     * @param channel the channel checked for rate limiting
     * @param user the user checked for rate limiting
     * @return if the command is not being rate limited
     */
    public boolean checkCoolDown(Guild guild, Channel channel, User user){
        if (this.globalUseTime != -1 && this.globalUseTime > System.currentTimeMillis()){
            return false;
        }
        if (this.guildCoolDowns != null && this.guildCoolDowns.contains(guild)){
            return false;
        }
        if (this.channelCoolDowns != null && this.channelCoolDowns.contains(channel)){
            return false;
        }
        if (this.userCoolDowns != null && this.userCoolDowns.contains(user)){
            return false;
        }
        if (this.guildUserCoolDowns != null && this.guildUserCoolDowns.contains(Configurable.getGuildUser(guild, user))){
            return false;
        }
        return true;
    }

    /**
     * Method to be called when the command is invoked
     *
     * @param guild the guild checked for rate limiting
     * @param channel the channel checked for rate limiting
     * @param user the user checked for rate limiting
     */
    public void invoked(Guild guild, Channel channel, User user){
        if (this.globalUseTime != -1){
            this.globalUseTime = System.currentTimeMillis() + this.globalCoolDownTime;
        }
        if (this.guildCoolDowns != null){
            this.guildCoolDowns.add(guild);
        }
        if (this.channelCoolDowns != null){
            this.channelCoolDowns.add(channel);
        }
        if (this.userCoolDowns != null){
            this.userCoolDowns.add(user);
        }
        if (this.guildUserCoolDowns != null){
            this.guildUserCoolDowns.add(Configurable.getGuildUser(guild, user));
        }
    }

    /**
     * Returns the cool down in millis dependent
     * on the type of configurable which is being rate limited
     *
     * @param clazz the configurable type
     * @return the cool down in millis dependent on the tyoe
     */
    protected long getCoolDown(Class<? extends Configurable> clazz){
        return -1;
    }

    /**
     *
     *
     * @param user the user that invokes the command
     * @param message the message sent or reacted to
     * @param reaction the reaction if the invocation
     *                 was caused by reacting to a message
     * @param args the user args for invocation
     * @return if the command was successful
     */
    protected boolean invoke(User user, Message message, Reaction reaction, String args){
        Object[] objects = InvocationObjectGetter.replace(this.argsTypes, new Object[this.argsTypes.length], user, message, reaction, args);
        try {
            boolean success = this.method.invoke(this, objects) == null;
            Stream.of(objects).filter(MessageHelper.class::isInstance).forEach(o -> ((MessageHelper) o).send());
            return success;
        } catch (IllegalAccessException e) {
            Log.log("Malformed command: " + getName(), e);
        } catch (InvocationTargetException e) {
            Log.log("Exception during method execution: " + getName(), e);
        }
        return false;
    }
}
