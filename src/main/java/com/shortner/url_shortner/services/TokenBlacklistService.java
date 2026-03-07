package com.shortner.url_shortner.services;

public interface TokenBlacklistService {

    void blacklistToken(String token);

    boolean isBlacklisted(String token);

}
