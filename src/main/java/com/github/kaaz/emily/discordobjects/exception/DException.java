package com.github.kaaz.emily.discordobjects.exception;

import sx.blah.discord.util.DiscordException;

/**
 * Made by nija123098 on 3/8/2017.
 */
public class DException extends RuntimeException {
    DException(DiscordException e){
        super(e);
    }
}