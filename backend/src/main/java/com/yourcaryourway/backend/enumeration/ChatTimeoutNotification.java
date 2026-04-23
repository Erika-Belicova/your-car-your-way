package com.yourcaryourway.backend.enumeration;

import lombok.Getter;

@Getter
public enum ChatTimeoutNotification {

    AGENTS_BUSY("Our support agents are currently busy. A support agent will respond as soon as possible."),
    ALL_AGENTS_OCCUPIED("We are sorry, all our agents are occupied at this time. Please contact support at a later time."),
    AGENT_VERIFYING("The support agent is verifying information, they will be with you shortly."),
    AGENT_UNABLE("We are sorry, the support agent is unable to assist you at this time. " +
            "Please start a new chat session later and our team will do their best to assist you.");

    private final String message;

    ChatTimeoutNotification(String message) {
        this.message = message;
    }

}