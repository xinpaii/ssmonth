package novaz.event;

import novaz.core.AbstractEventListener;
import novaz.db.model.OGuildMember;
import novaz.db.table.TGuildMember;
import novaz.guildsettings.defaults.SettingPMUserEvents;
import novaz.guildsettings.defaults.SettingWelcomeNewUsers;
import novaz.handler.GuildSettings;
import novaz.handler.TextHandler;
import novaz.main.NovaBot;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

import java.sql.Timestamp;

/**
 * A user joins a guild
 */
public class UserJoinListener extends AbstractEventListener<UserJoinEvent> {
	public UserJoinListener(NovaBot novaBot) {
		super(novaBot);
	}

	@Override
	public boolean listenerIsActivated() {
		return true;
	}

	@Override
	public void handle(UserJoinEvent event) {
		IUser user = event.getUser();
		IGuild guild = event.getGuild();
		if ("true".equals(GuildSettings.get(guild).getOrDefault(SettingPMUserEvents.class))) {
			novaBot.out.sendPrivateMessage(guild.getOwner(), String.format("[user-event] **%s** joined the guild **%s**", user.mention(), guild.getName()));
		}
		if ("true".equals(GuildSettings.get(guild).getOrDefault(SettingWelcomeNewUsers.class))) {
			novaBot.out.sendMessage(guild.getChannels().get(0), String.format(TextHandler.get("welcome_new_user"), user.mention()));
		}
		OGuildMember guildMember = TGuildMember.findBy(guild.getID(), user.getID());
		guildMember.joinDate = new Timestamp(System.currentTimeMillis());
		TGuildMember.insertOrUpdate(guildMember);

	}
}