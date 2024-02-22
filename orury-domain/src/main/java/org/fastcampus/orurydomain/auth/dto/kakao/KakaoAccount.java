package org.fastcampus.orurydomain.auth.dto.kakao;

public record KakaoAccount(
        Profile profile,
        String email
) {
    public static KakaoAccount of(
            Profile profile,
            String email
    ) {
        return new KakaoAccount(
                profile,
                email
        );
    }
}
