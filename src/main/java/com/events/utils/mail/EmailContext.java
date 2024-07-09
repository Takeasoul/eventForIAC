package com.events.utils.mail;

import java.util.Map;

public class EmailContext extends AbstractEmailContext{
    public EmailContext(String from, String to, String subject, String email, String attachment, String fromDisplayName, String emailLanguage, String displayName, String templateLocation, Map<String, Object> context) {
        super(from, to, subject, email, attachment, fromDisplayName, emailLanguage, displayName, templateLocation, context);
    }

    public EmailContext() {
    }
}
