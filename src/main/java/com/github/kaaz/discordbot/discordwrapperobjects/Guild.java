package com.github.kaaz.discordbot.discordwrapperobjects;

import com.github.kaaz.discordbot.config.ConfigLevel;
import com.github.kaaz.discordbot.config.Configurable;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IInvite;
import sx.blah.discord.handle.obj.IRegion;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.VerificationLevel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Made by nija123098 on 2/20/2017.
 */
public class Guild implements Configurable {// todo rewrite to completely match necessary Discord stuff
    private static final Map<String, Guild> MAP = new ConcurrentHashMap<>();
    public static Guild getGuild(String id){// todo replace null
        return MAP.computeIfAbsent(id, s -> null);
    }
    static Guild getGuild(IGuild guild){
        return MAP.computeIfAbsent(guild.getID(), s -> new Guild(guild));
    }
    public synchronized void update(IGuild guild){// hash is based on id, so no old channel is necessary
        MAP.get(guild.getID()).reference.set(guild);
    }
    private final AtomicReference<IGuild> reference;
    private Guild(IGuild guild) {
        this.reference = new AtomicReference<>(guild);
    }
    IGuild guild(){
        return this.reference.get();
    }
    @Override
    public ConfigLevel getConfigLevel() {
        return null;
    }
    @Override
    public String getID() {
        return guild().getID();
    }
    // THE FOLLOWING ARE MIMIC METHODS
    public DiscordClient getClient() {
        return DiscordClient.get();
    }

    public Shard getShard() {
        return Shard.getShard(guild().getShard());
    }

    public String getOwnerID() {
        return guild().getOwnerID();
    }

    public User getOwner() {
        return User.getUser(guild().getOwnerID());
    }

    public String getIcon() {
        return guild().getIcon();
    }

    public String getIconURL() {
        return guild().getIconURL();
    }

    public List<Channel> getChannels() {
        return Channel.getChannels(guild().getChannels());
    }

    public Channel getChannelByID(String s) {
        return Channel.getChannel(s);
    }

    public List<User> getUsers() {
        return User.getUsers(guild().getUsers());
    }

    public User getUserByID(String s) {
        return User.getUser(s);
    }

    public List<Channel> getChannelsByName(String s) {
        return Channel.getChannels(guild().getChannelsByName(s));
    }

    public List<VoiceChannel> getVoiceChannelsByName(String s) {
        return VoiceChannel.getVoiceChannels(guild().getVoiceChannelsByName(s));
    }

    public List<User> getUsersByName(String s) {
        return getUsersByName(s, true);
    }

    public List<User> getUsersByName(String s, boolean b) {
        return User.getUsers(guild().getUsersByName(s, b));
    }

    public List<User> getUsersByRole(Role role) {
        return User.getUsers(guild().getUsersByRole(role.role()));
    }

    public String getName() {
        return guild().getName();
    }

    public List<Role> getRoles() {
        return Role.getRoles(guild().getRoles());
    }

    public List<Role> getRolesForUser(User user) {
        return Role.getRoles(guild().getRolesForUser(user.user.get()));
    }

    public Role getRoleByID(String s) {
        return Role.getRole(s);
    }

    public List<Role> getRolesByName(String s) {
        return Role.getRoles(guild().getRolesByName(s));
    }

    public List<VoiceChannel> getVoiceChannels() {
        return VoiceChannel.getVoiceChannels(guild().getVoiceChannels());
    }

    public VoiceChannel getVoiceChannelByID(String s) {
        return VoiceChannel.getVoiceChannel(s);
    }

    public VoiceChannel getConnectedVoiceChannel() {
        return VoiceChannel.getVoiceChannel(guild().getConnectedVoiceChannel());
    }

    public VoiceChannel getAFKChannel() {
        return VoiceChannel.getVoiceChannel(guild().getAFKChannel());
    }

    public int getAFKTimeout() {
        return guild().getAFKTimeout();
    }

    public Role createRole() {
        return RequestBuffer.request((RequestBuffer.IRequest<Role>) () -> Role.getRole(guild().createRole())).get();
    }

    public List<User> getBannedUsers() {
        return RequestBuffer.request((RequestBuffer.IRequest<List<User>>) () -> User.getUsers(guild().getBannedUsers())).get();
    }

    public void banUser(User user) {
        RequestBuffer.request(() -> guild().banUser(user.user.get()));
    }

    public void banUser(User user, int i) {
        RequestBuffer.request(() -> guild().banUser(user.user.get(), i));
    }

    public void banUser(String s) {
        RequestBuffer.request(() -> guild().banUser(s));
    }

    public void banUser(String s, int i) {
        RequestBuffer.request(() -> guild().banUser(s, i));
    }

    public void pardonUser(String s) {
        RequestBuffer.request(() -> guild().pardonUser(s));
    }

    public void kickUser(User user) {
        RequestBuffer.request(() -> guild().kickUser(user.user.get()));
    }

    public void editUserRoles(IUser iUser, IRole[] iRoles) {

    }

    public void setDeafenUser(IUser iUser, boolean b) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void setMuteUser(IUser iUser, boolean b) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void setUserNickname(IUser iUser, String s) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void edit(String s, IRegion iRegion, VerificationLevel verificationLevel, Image image, IVoiceChannel iVoiceChannel, int i) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void changeName(String s) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void changeRegion(IRegion iRegion) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void changeVerificationLevel(VerificationLevel verificationLevel) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void changeIcon(Image image) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void changeAFKChannel(VoiceChannel voiceChannel) {
        RequestBuffer.request(() -> guild().changeAFKChannel(voiceChannel.channel()));
    }

    public void changeAFKTimeout(int i) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void deleteGuild() throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public void leaveGuild() throws DiscordException, RateLimitException {

    }

    public void leave() throws DiscordException, RateLimitException {
        RequestBuffer.request(() -> {}).get();
    }

    public IChannel createChannel(String s) throws DiscordException, RateLimitException, MissingPermissionsException {
        return null;
    }

    public IVoiceChannel createVoiceChannel(String s) throws DiscordException, RateLimitException, MissingPermissionsException {
        return null;
    }

    public IRegion getRegion() {
        return null;
    }

    public VerificationLevel getVerificationLevel() {
        return null;
    }

    public IRole getEveryoneRole() {
        return null;
    }

    public Channel getGeneralChannel() {
        return Channel.getChannel(guild().getGeneralChannel());
    }

    public List<IInvite> getInvites() throws DiscordException, RateLimitException, MissingPermissionsException {
        return null;
    }

    public void reorderRoles(IRole... iRoles) throws DiscordException, RateLimitException, MissingPermissionsException {

    }

    public int getUsersToBePruned(int i) throws DiscordException, RateLimitException {
        return 0;
    }

    public int pruneUsers(int i) throws DiscordException, RateLimitException {
        return 0;
    }

    public boolean isDeleted() {
        return guild().isDeleted();
    }

    public IAudioManager getAudioManager() {
        return null;
    }

    public LocalDateTime getJoinTimeForUser(User user) throws DiscordException {
        return guild().getJoinTimeForUser(user.user.get());
    }

    public Message getMessageByID(String s) {
        return Message.getMessage(s);
    }

    public int getTotalMemberCount() {
        return guild().getTotalMemberCount();
    }
    /*
    @Override
    public String getID() {
        return null;
    }
    @Override
    public ConfigLevel getConfigLevel() {
        return ConfigLevel.GUILD;
    }
    public User getOwner() {
        return new User(channel.get().getOwner());// temp
    }
    public EnumSet<DiscordPermission> getPermissionsForGuild(User user){
        return EnumSet.allOf(DiscordPermission.class);// temp
    }
    */
}
