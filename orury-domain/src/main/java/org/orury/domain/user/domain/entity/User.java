package org.orury.domain.user.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.orury.domain.base.db.AuditingField;
import org.orury.domain.global.domain.Region;
import org.orury.domain.global.domain.RegionConverter;
import org.orury.domain.global.listener.UserProfileConverter;
import org.orury.domain.user.domain.dto.UserStatus;
import org.orury.domain.user.domain.dto.UserStatusConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "user")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class User extends AuditingField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "sign_up_type", nullable = false)
    private int signUpType;

    @Column(name = "gender", nullable = false)
    private int gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Setter
    @Convert(converter = UserProfileConverter.class)
    @Column(name = "profile_image")
    private String profileImage;

    @Setter
    @Column(name = "status")
    @Convert(converter = UserStatusConverter.class)
    private UserStatus status;

    @Convert(converter = RegionConverter.class)
    @Column(name = "regions", nullable = false)
    private List<Region> regions;

    @Column(name = "self_introduction")
    private String selfIntroduction;

    private User(Long id, String email, String nickname, String password, int signUpType, int gender, LocalDate birthday, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, UserStatus status, List<Region> regions, String selfIntroduction) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.signUpType = signUpType;
        this.gender = gender;
        this.birthday = birthday;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.regions = regions;
        this.selfIntroduction = selfIntroduction;
    }

    public static User of(Long id, String email, String nickname, String password, int signUpType, int gender, LocalDate birthday, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, UserStatus status, List<Region> regions, String selfIntroduction) {
        return new User(id, email, nickname, password, signUpType, gender, birthday, profileImage, createdAt, updatedAt, status, regions, selfIntroduction);
    }
}
