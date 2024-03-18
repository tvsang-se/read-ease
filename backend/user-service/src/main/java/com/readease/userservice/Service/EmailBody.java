package com.readease.userservice.Service;

import java.util.Map;

public record EmailBody(String to, String subject, String templateName, Map<String, String> templateVars) {
}
