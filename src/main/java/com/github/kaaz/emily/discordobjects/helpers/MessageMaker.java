package com.github.kaaz.emily.discordobjects.helpers;

import com.github.kaaz.emily.config.ConfigHandler;
import com.github.kaaz.emily.config.configs.guild.GuildLanguageConfig;
import com.github.kaaz.emily.config.configs.user.UserLanguageConfig;
import com.github.kaaz.emily.discordobjects.exception.ErrorWrapper;
import com.github.kaaz.emily.discordobjects.wrappers.*;
import com.github.kaaz.emily.exeption.BotException;
import com.github.kaaz.emily.util.ImageColorHelper;
import com.github.kaaz.emily.util.LangString;
import javafx.util.Pair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageBuilder;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Made by nija123098 on 4/7/2017.
 */
public class MessageMaker {
    private static final int CHAR_LIMIT = 2000;
    private TextPart authorName = new TextPart(this), title = new TextPart(this), header = new TextPart(this), footer = new TextPart(this), note = new TextPart(this);
    private List<TextPart> textList = new ArrayList<>();
    private String[] textVals;
    private List<FieldPart> fieldList = new ArrayList<>();
    private Triple<String, String, Boolean>[][] fieldIndices;
    private EmbedBuilder embed = new EmbedBuilder();
    private MessageBuilder builder = new MessageBuilder(DiscordClient.client());
    private String lang;
    private User user;
    private Channel channel;
    private AtomicInteger currentPage = new AtomicInteger();
    private List<Pair<String, ReactionBehavior>> reactionBehaviors = new ArrayList<>();
    private IMessage message;
    public MessageMaker(Channel channel, User user) {
        this.channel = channel;
        this.user = user;
    }
    // setup methods
    public MessageMaker asNormalMessage(){
        this.embed = null;
        return this;
    }
    public MessageMaker withChannel(Channel channel){
        this.channel = channel;
        return this;
    }
    public MessageMaker withReactionBehavior(String reactionName, ReactionBehavior behavior){
        this.reactionBehaviors.add(new Pair<>(reactionName, behavior));
        return this;
    }
    public MessageMaker withDM(){
        this.channel = this.user.getOrCreatePMChannel();
        return this;
    }
    public MessageMaker ensureListSize(int size){
        List<TextPart> textList = new ArrayList<>(size);
        textList.addAll(this.textList);
        this.textList = textList;
        return this;
    }
    public MessageMaker ensureFieldSize(int size){
        List<FieldPart> fieldList = new ArrayList<>(size);
        fieldList.addAll(this.fieldList);
        this.fieldList = fieldList;
        return this;
    }
    // text methods
    public TextPart getAuthorName(){
        return this.authorName;
    }
    public TextPart getTitle(){
        return this.title;
    }
    public TextPart getHeader(){
        return this.header;
    }
    public TextPart getFooter(){
        return this.footer;
    }
    public TextPart getNote(){
        return this.note;
    }
    public FieldPart getNewFieldPart(){
        return new FieldPart(this);// adds self in the constructor
    }
    public TextPart getNewListPart(){
        TextPart part = new TextPart(this);
        this.textList.add(part);
        return part;
    }
    public MessageMaker appendRaw(String s){
        this.header.appendRaw(s);
        return this;
    }
    public MessageMaker appendContent(String s){
        this.header.appendContent(s);
        return this;
    }
    public MessageMaker appendAlternate(boolean raw, String...s){
        this.header.appendAlternate(raw, s);
        return this;
    }
    public MessageMaker append(boolean raw, String s){
        this.header.append(!raw, s);
        return this;
    }
    public MessageMaker asExceptionMessage(BotException cause) {
        this.withColor(Color.RED).getHeader().appendContent(cause.getMessage()).getMessageProducer().getTitle().appendRaw(cause.getClass().getSimpleName());
        return this;
    }
    // embed methods
    public MessageMaker withColor(Color color){
        this.embed.withColor(color);
        return this;
    }
    public MessageMaker withUserColor(){
        this.embed.withColor(ImageColorHelper.getUserColor(this.user));
        return this;
    }
    public MessageMaker withUserColor(User user){
        this.embed.withColor(ImageColorHelper.getUserColor(user));
        return this;
    }
    public MessageMaker withFooterIcon(String url){
        this.embed.withFooterIcon(url);
        return this;
    }
    public MessageMaker withAuthorIcon(String url){
        this.embed.withAuthorName(url);
        return this;
    }
    public MessageMaker withTumb(String url){
        this.embed.withThumbnail(url);
        return this;
    }
    public MessageMaker withImage(String url){
        this.embed.withImage(url);
        return this;
    }
    public MessageMaker withTimestamp(LocalDateTime time){
        this.embed.withTimestamp(time);
        return this;
    }
    public MessageMaker withTimestamp(long millis){
        this.embed.withTimestamp(millis);
        return this;
    }
    // building
    public void send(){
        send(0);
    }
    private void send(int page){
        compile();
        if (page >= fieldIndices.length){
            throw new RuntimeException("Attempted to get a page that doesn't exit");
        }
        if (this.embed != null){
            this.embed.clearFields().withDesc(this.header.langString.translate(lang) + "\n\n" + (page >= textVals.length ? "" : textVals[page]) + "\n\n" + this.footer.langString.translate(lang));
            for (Triple<String, String, Boolean> ind : fieldIndices[page]){
                this.embed.appendField(ind.getLeft(), ind.getMiddle(), ind.getRight());
            }
            this.builder.withEmbed(this.embed.build());
        }
        if (this.message == null){
            this.message = ErrorWrapper.wrap((ErrorWrapper.Request<IMessage>) () -> this.builder.withChannel(channel.channel()).send());
            this.reactionBehaviors.forEach(pair -> ReactionBehavior.registerListener(Message.getMessage(this.message), pair.getKey(), pair.getValue()));
        } else if (this.embed != null){
            ErrorWrapper.wrap(() -> this.message.edit(this.embed.build()));
        }
    }
    private void compile(){
        // multi use
        if (lang == null){
            if (this.user != null){
                lang = ConfigHandler.getSetting(UserLanguageConfig.class, this.user);
            }
            if (!this.channel.isPrivate() && lang == null){
                lang = ConfigHandler.getSetting(GuildLanguageConfig.class, this.channel.getGuild());
            }
            if (lang == null){
                lang = "en";
            }
        }else{
            return;
        }
        // message
        if (!this.channel.getModifiedPermissions(DiscordClient.getOurUser()).contains(DiscordPermission.SEND_MESSAGES)){
            if (this.channel.isPrivate()){
                throw new RuntimeException("Could not send message to user due to lacking permissions: " + this.user.getName());
            } else {
                this.channel = this.user.getOrCreatePMChannel();
                this.compile();
                return;
            }
        }
        if (this.embed == null){
            this.builder.withContent(this.header.langString.translate(this.lang));
        } else {// embed
            this.embed.withAuthorName(authorName.langString.translate(lang));
            this.embed.withTitle(title.langString.translate(lang));
            this.embed.withFooterText(note.langString.translate(lang));
            int starterChars = this.embed.getTotalVisibleCharacters() + header.langString.translate(lang).length() + footer.langString.translate(lang).length();
            if (CHAR_LIMIT < starterChars){
                throw new RuntimeException("Header and footer are too big.");
            }
            if (textList.size() != 0){
                int index = -1;
                String s = "";
                List<String> strings = new ArrayList<>();
                while (true) {
                    if (++index >= textList.size()){
                        strings.add(s);
                        break;
                    }
                    if (starterChars + s.length() + textList.get(index).langString.translate(lang).length() > CHAR_LIMIT){
                        --index;
                        strings.add(s);
                        s = "";
                    } else {
                        s += "\n" + textList.get(index).langString.translate(lang);
                    }
                }
                textVals = new String[strings.size()];
                for (int i = 0; i < strings.size(); i++) {
                    textVals[i] = strings.get(i);
                }
            } else {
                textVals = new String[]{""};
            }
            textList = null;
            if (fieldList.size() != 0){
                int index = -1, size = 0, page = 0;
                List<List<Triple<String, String, Boolean>>> vals = new ArrayList<>(this.fieldList.size());
                vals.add(new ArrayList<>());
                while (true) {
                    if (++index >= fieldList.size()){
                        break;
                    }
                    if (starterChars + size > CHAR_LIMIT || vals.get(page).size() > 21){
                        --index;
                        size = 0;
                        ++page;
                        vals.add(new ArrayList<>());
                    } else {
                        size = fieldList.get(index).title.langString.translate(lang).length() + fieldList.get(index).value.langString.translate(lang).length();
                        vals.get(page).add(new ImmutableTriple<>(fieldList.get(index).title.langString.translate(lang), fieldList.get(index).value.langString.translate(lang), fieldList.get(index).inline));
                    }
                }
                fieldIndices = new Triple[textVals.length + vals.size()][];
                for (int i = 0; i < textVals.length; i++) {
                    fieldIndices[i] = new Triple[0];
                }
                for (int i = textVals.length; i < fieldIndices.length; i++) {
                    fieldIndices[i] = new Triple[vals.get(i - textVals.length).size()];
                    for (int j = 0; j < fieldIndices[i].length; j++) {
                        fieldIndices[i][j] = vals.get(i - textVals.length).get(j);
                    }
                }
            } else {
                fieldIndices = new Triple[textVals.length][];
                Arrays.fill(fieldIndices, new Triple[0]);
            }
            fieldList = null;
        }
        if (this.fieldIndices.length > 1){
            this.withReactionBehavior("arrow_left", (add, reaction) -> {
                if (currentPage.get() == 0){
                    return;
                }
                this.send(currentPage.decrementAndGet());
            });
            this.withReactionBehavior("arrow_right", (add, reaction) -> {
                if (currentPage.get() == fieldIndices.length - 1){
                    return;
                }
                this.send(currentPage.incrementAndGet());
            });
        }
    }
    public class FieldPart {
        private MessageMaker producer;
        private FieldTextPart title = new FieldTextPart(producer, this), value = new FieldTextPart(producer, this);
        private boolean inline;

        public FieldPart(MessageMaker producer) {
            this.producer = producer;
            this.producer.fieldList.add(this);
            this.inline = true;
        }

        public FieldPart withInline(boolean inline){
            this.inline = inline;
            return this;
        }

        public FieldTextPart getTitle(){
            return this.title;
        }
        public FieldTextPart getValue(){
            return this.value;
        }
        public MessageMaker getMessageProducer(){
            return this.producer;
        }
    }
    public class TextPart {
        private MessageMaker producer;
        LangString langString;
        public TextPart(MessageMaker producer) {
            this.producer = producer;
            this.langString = new LangString();
        }

        public TextPart appendRaw(String s){
            this.langString.appendRaw(s);
            return this;
        }

        public TextPart appendContent(String s){
            this.langString.appendTranslation(s);
            return this;
        }

        public TextPart appendAlternate(boolean raw, String...s){
            this.langString.appendToggle(raw, s);
            return this;
        }

        public TextPart append(boolean raw, String s){
            this.langString.append(!raw, s);
            return this;
        }
        public MessageMaker getMessageProducer(){
            return this.producer;
        }
    }
    public class FieldTextPart extends TextPart {
        private FieldPart part;
        private FieldTextPart(MessageMaker helper, FieldPart part) {
            super(helper);
            this.part = part;
        }

        public FieldPart getFieldPart(){
            return this.part;
        }

        public FieldTextPart appendRaw(String s){
            super.appendRaw(s);
            return this;
        }

        public FieldTextPart appendContent(String s){
            super.appendContent(s);
            return this;
        }

        public FieldTextPart appendAlternate(boolean raw, String...s){
            super.appendAlternate(raw, s);
            return this;
        }

        public FieldTextPart append(boolean raw, String s){
            super.append(!raw, s);
            return this;
        }
    }
}