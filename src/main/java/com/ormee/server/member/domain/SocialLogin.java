package com.ormee.server.member.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class SocialLogin {
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialProvider provider;

    protected SocialLogin() {}

    public SocialLogin(String socialId, SocialProvider provider) {
        this.socialId = socialId;
        this.provider = provider;
    }
}

