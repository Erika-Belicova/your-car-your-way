package com.yourcaryourway.backend.enumeration;

import lombok.Getter;

@Getter
public enum ChatTimeoutNotification {

    AGENTS_BUSY("Our agents are currently helping other customers, wait in the queue or contact support at a later time."),
    ALL_AGENTS_OCCUPIED("We are sorry, all our agents are occupied at this time. Please contact support at a later time."),
    AGENT_VERIFYING("The support agent is verifying information, they will be with you shortly."),
    AGENT_UNABLE("The support agent was unable to retrieve the information needed to assist you at this time. " +
            "Please start a new chat session later and our team will do their best to assist you.");

    private final String message;

    ChatTimeoutNotification(String message) {
        this.message = message;
    }

}