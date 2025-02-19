package org.orury.client.crew.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orury.client.config.ServiceTest;
import org.orury.common.error.code.CrewErrorCode;
import org.orury.common.error.exception.BusinessException;
import org.orury.common.util.S3Folder;
import org.orury.domain.crew.domain.dto.CrewApplicationDto;
import org.orury.domain.crew.domain.dto.CrewDto;
import org.orury.domain.crew.domain.dto.CrewGender;
import org.orury.domain.crew.domain.entity.Crew;
import org.orury.domain.crew.domain.entity.CrewApplication;
import org.orury.domain.crew.domain.entity.CrewMember;
import org.orury.domain.global.constants.NumberConstants;
import org.orury.domain.user.domain.dto.UserDto;
import org.orury.domain.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.orury.domain.CrewDomainFixture.TestCrew.createCrew;
import static org.orury.domain.CrewDomainFixture.TestCrewApplication.createCrewApplication;
import static org.orury.domain.CrewDomainFixture.TestCrewDto.createCrewDto;
import static org.orury.domain.CrewDomainFixture.TestCrewMember.createCrewMember;
import static org.orury.domain.UserDomainFixture.TestUser.createUser;
import static org.orury.domain.UserDomainFixture.TestUserDto.createUserDto;

@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] 크루 ServiceImpl 테스트")
@ActiveProfiles("test")
class CrewServiceImplTest extends ServiceTest {

    @DisplayName("[getCrewDtoById] 크루 아이디로 크루 정보를 가져온다.")
    @Test
    void should_GetCrewDtoById() {
        // given
        Long crewId = 1L;
        Crew crew = createCrew()
                .id(crewId).build().get();
        List<String> tags = List.of("태그1", "태그2", "태그3");
        given(crewReader.findById(crewId))
                .willReturn(Optional.of(crew));
        given(crewTagReader.getTagsByCrewId(crewId))
                .willReturn(tags);

        // when
        crewService.getCrewDtoById(crewId);

        // then
        then(crewReader).should(only())
                .findById(anyLong());
        then(crewTagReader).should(only())
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getCrewDtoById] 존재하는 크루 아이디인 경우, NotFound 예외를 발생시킨다.")
    @Test
    void when_NotExistingCrewId_Then_NotFoundException() {
        // given
        Long crewId = 1L;
        given(crewReader.findById(crewId))
                .willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(BusinessException.class,
                () -> crewService.getCrewDtoById(crewId));

        assertEquals(CrewErrorCode.NOT_FOUND.getMessage(), exception.getMessage());
        then(crewReader).should(only())
                .findById(anyLong());
        then(crewTagReader).shouldHaveNoInteractions();
    }

    @DisplayName("[createCrew] 크루를 생성한다.")
    @Test
    void should_CreateCrew() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        MultipartFile file = mock(MultipartFile.class);
        String icon = "크루아이콘";
        given(imageStore.upload(S3Folder.CREW, file))
                .willReturn(icon);
        Crew crew = createCrew()
                .id(crewDto.id()).build().get();
        given(crewStore.save(any()))
                .willReturn(crew);

        // when
        crewService.createCrew(crewDto, file);

        // then
        then(crewCreatePolicy).should(times(1))
                .validate(crewDto);
        then(imageStore).should(only())
                .upload(any(S3Folder.class), any(MultipartFile.class));
        then(crewStore).should(only())
                .save(any());
        then(crewTagStore).should(only())
                .addTags(any(), anyList());
        then(crewMemberStore).should(only())
                .addCrewMember(anyLong(), anyLong());
    }

    @DisplayName("[getCrewDtosByRecommendedSort] 추천순으로 크루 목록을 가져온다.")
    @Test
    void should_GetCrewDtosByRecommendedSort() {
        // given
        Pageable pageable = mock(Pageable.class);
        UserDto userDto = createUserDto().build().get();
        Page<Crew> crews = new PageImpl<>(List.of(
                createCrew(1L).build().get(),
                createCrew(2L).build().get(),
                createCrew(3L).build().get()
        ));
        given(crewReader.getCrewsByRecommendedSort(any(Pageable.class), any(CrewGender.class), anyInt()))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getCrewDtosByRecommendedSort(pageable, userDto);

        // then
        then(crewReader).should(only())
                .getCrewsByRecommendedSort(any(Pageable.class), any(CrewGender.class), anyInt());
        then(crewTagReader).should(times(crews.getContent().size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getCrewDtosByPopularSort] 인기순으로 크루 목록을 가져온다.")
    @Test
    void should_GetCrewDtosByPopularSort() {
        // given
        Pageable pageable = mock(Pageable.class);
        Page<Crew> crews = new PageImpl<>(List.of(
                createCrew(1L).build().get(),
                createCrew(2L).build().get(),
                createCrew(3L).build().get()
        ));
        given(crewReader.getCrewsByPopularSort(pageable))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getCrewDtosByPopularSort(pageable);

        // then
        then(crewReader).should(only())
                .getCrewsByPopularSort(any());
        then(crewTagReader).should(times(crews.getContent().size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getCrewDtosByActiveSort] 활동순으로 크루 목록을 가져온다.")
    @Test
    void should_GetCrewDtosByActiveSort() {
        // given
        Pageable pageable = mock(Pageable.class);
        Page<Crew> crews = new PageImpl<>(List.of(
                createCrew(1L).build().get(),
                createCrew(2L).build().get(),
                createCrew(3L).build().get()
        ));
        given(crewReader.getCrewsByActiveSort(pageable))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getCrewDtosByActiveSort(pageable);

        // then
        then(crewReader).should(only())
                .getCrewsByActiveSort(any());
        then(crewTagReader).should(times(crews.getContent().size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getCrewDtosByNewestSort] 최신순으로 크루 목록을 가져온다.")
    @Test
    void should_GetCrewDtosByNewestSort() {
        // given
        Pageable pageable = mock(Pageable.class);
        Page<Crew> crews = new PageImpl<>(List.of(
                createCrew(1L).build().get(),
                createCrew(2L).build().get(),
                createCrew(3L).build().get()
        ));
        given(crewReader.getCrewsByLatestSort(pageable))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getCrewDtosByLatestSort(pageable);

        // then
        then(crewReader).should(only())
                .getCrewsByLatestSort(any());
        then(crewTagReader).should(times(crews.getContent().size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getJoinedCrewDtos] 유저 아이디에 따른 가입된 크루 목록을 가져온다.")
    @Test
    void should_GetJoinedCrewDtos() {
        // given
        Long userId = 2771L;
        List<Crew> crews = List.of(
                createCrew(2354L).build().get(),
                createCrew(1325L).build().get(),
                createCrew(2246L).build().get()
        );
        given(crewReader.getJoinedCrewsByUserId(userId))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getJoinedCrewDtos(userId);

        // then
        then(crewReader).should(only())
                .getJoinedCrewsByUserId(anyLong());
        then(crewTagReader).should(times(crews.size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getAppliedCrewDtos] 유저 아이디에 따른 가입신청한 크루 목록을 가져온다.")
    @Test
    void should_GetAppliedCrewDtos() {
        // given
        Long userId = 259L;
        List<Crew> crews = List.of(
                createCrew(28416L).build().get(),
                createCrew(620L).build().get(),
                createCrew(738L).build().get()
        );
        given(crewReader.getAppliedCrewsByUserId(userId))
                .willReturn(crews);
        given(crewTagReader.getTagsByCrewId(anyLong()))
                .willReturn(mock(List.class));

        // when
        crewService.getAppliedCrewDtos(userId);

        // then
        then(crewReader).should(only())
                .getAppliedCrewsByUserId(anyLong());
        then(crewTagReader).should(times(crews.size()))
                .getTagsByCrewId(anyLong());
    }

    @DisplayName("[getUserImagesByCrew] 크루에 따른 유저이미지를 크루장을 가장 처음으로 설정하여 Maximum만큼 가져온다.")
    @Test
    void should_GetUserImagesByCrew() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        int maximumCount = 4;
        List<CrewMember> otherMembers = List.of(
                createCrewMember(crewDto.id(), 1L).build().get(),
                createCrewMember(crewDto.id(), 2L).build().get(),
                createCrewMember(crewDto.id(), 3L).build().get()
        );
        given(crewMemberReader.getOtherCrewMembersByCrewIdMaximum(anyLong(), anyLong(), anyInt()))
                .willReturn(otherMembers);
        given(userReader.getUserById(anyLong()))
                .willReturn(
                        createUser(1L).build().get(),
                        createUser(2L).build().get(),
                        createUser(3L).build().get()
                );

        // when
        List<String> userImages = crewService.getUserImagesByCrew(crewDto, maximumCount);

        // then
        assertEquals(1 + otherMembers.size(), userImages.size());
        assertEquals(crewDto.userDto().profileImage(), userImages.get(0));
        then(crewMemberReader).should(only())
                .getOtherCrewMembersByCrewIdMaximum(anyLong(), anyLong(), anyInt());
        then(userReader).should(times(otherMembers.size()))
                .getUserById(anyLong());
    }

    @DisplayName("[existCrewMember] 크루원의 존재여부를 가져온다.")
    @Test
    void should_ExistCrewMember() {
        // given
        Long crewId = 148L;
        Long userId = 26729L;
        given(crewMemberReader.existsByCrewIdAndUserId(crewId, userId))
                .willReturn(true);

        // when
        boolean isExist = crewService.existCrewMember(crewId, userId);

        // then
        assertTrue(isExist);
        then(crewMemberReader).should(only())
                .existsByCrewIdAndUserId(anyLong(), anyLong());
    }

    @DisplayName("[updateCrewInfo] 크루 정보를 업데이트한다.")
    @Test
    void should_UpdateCrewInfo() {
        // given
        CrewDto oldCrew = createCrewDto().build().get();
        CrewDto newCrew = createCrewDto().build().get();
        Long userId = oldCrew.userDto().id();
        Crew crew = createCrew().build().get();
        given(crewStore.save(any()))
                .willReturn(crew);

        // when
        crewService.updateCrewInfo(oldCrew, newCrew, userId);

        // then
        then(crewUpdatePolicy).should(only())
                .validateUpdateCrewInfo(any(), any(), anyLong());
        then(crewStore).should(only())
                .save(any());
        then(crewTagStore).should(only())
                .updateTags(anyList(), anyList(), any());
    }

    @DisplayName("[updateCrewImage] 크루 이미지를 업데이트한다.")
    @Test
    void should_UpdateCrewImage() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        MultipartFile file = mock(MultipartFile.class);
        Long userId = crewDto.userDto().id();
        String newImage = "크루아이콘";
        given(imageStore.upload(S3Folder.CREW, file))
                .willReturn(newImage);

        // when
        crewService.updateCrewImage(crewDto, file, userId);

        // then
        then(imageStore).should(times(1))
                .upload(any(), any(MultipartFile.class));
        then(crewStore).should(only())
                .save(any());
        then(imageStore).should(times(1))
                .delete(any(), anyString());
    }

    @DisplayName("[deleteCrew] 크루를 삭제한다.")
    @Test
    void should_DeleteCrew() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long userId = crewDto.userDto().id();

        // when
        crewService.deleteCrew(crewDto, userId);

        // then
        then(crewStore).should(only())
                .delete(any());
        then(imageStore).should(only())
                .delete(any(), anyString());
    }

    @DisplayName("[applyCrew] 가입수락이 필요한 크루면, 크루에 가입신청을 한다.")
    @Test
    void when_CrewNeedPermission_Then_ApplyCrew() {
        // given
        CrewDto crewDto = createCrewDto()
                .id(23L)
                .minAge(15)
                .maxAge(30)
                .gender(CrewGender.ANY)
                .permissionRequired(true)
                .answerRequired(true).build().get();
        UserDto userDto = createUserDto()
                .gender(NumberConstants.MALE)
                .birthday(LocalDate.now().minusYears(20)).build().get();

        String answer = "가입신청 답변";

        // when
        crewService.applyCrew(crewDto, userDto, answer);

        // then
        then(crewApplicationPolicy).should(only())
                .validateApplyCrew(crewDto, userDto, answer);
        then(crewMemberStore).shouldHaveNoInteractions();
        then(crewApplicationStore).should(only())
                .save(any(), any(), anyString());
    }

    @DisplayName("[applyCrew] 가입수락이 필요 없는 크루면, 크루에 가입한다.")
    @Test
    void when_CrewNeedPermission_Then_JoinCrew() {
        // given
        CrewDto crewDto = createCrewDto()
                .id(23L)
                .minAge(15)
                .maxAge(30)
                .gender(CrewGender.ANY)
                .permissionRequired(false).build().get();
        UserDto userDto = createUserDto()
                .gender(NumberConstants.MALE)
                .birthday(LocalDate.now().minusYears(20)).build().get();

        String answer = "가입신청 답변";

        // when
        crewService.applyCrew(crewDto, userDto, answer);

        // then
        then(crewApplicationPolicy).should(only())
                .validateApplyCrew(crewDto, userDto, answer);
        then(crewMemberStore).should(only())
                .addCrewMember(anyLong(), anyLong());
        then(crewApplicationStore).shouldHaveNoInteractions();
    }

    @DisplayName("[withdrawApplication] 크루 가입신청을 취소한다.")
    @Test
    void should_WithdrawApplication() {
        // given
        Long userId = 1L;
        CrewDto crewDto = createCrewDto().build().get();

        // when
        crewService.withdrawApplication(crewDto, userId);

        // then
        then(crewApplicationPolicy).should(only())
                .validateApplication(anyLong(), anyLong());
        then(crewApplicationStore).should(only())
                .delete(any(), any());
    }

    @DisplayName("[approveApplication] 크루 가입신청을 승인한다.")
    @Test
    void should_ApproveApplication() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long applicantId = 2L;

        // when
        crewService.approveApplication(crewDto, applicantId, crewDto.userDto().id());

        // then
        then(crewPolicy).should(only())
                .validateCrewCreator(anyLong(), anyLong());
        then(crewApplicationPolicy).should(only())
                .validateApplication(anyLong(), anyLong());
        then(crewApplicationStore).should(only())
                .approve(anyLong(), anyLong());
    }


    @DisplayName("[disapproveApplication] 크루 가입신청을 거절한다.")
    @Test
    void should_DisapproveApplication() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long applicantId = 2L;

        // when
        crewService.disapproveApplication(crewDto, applicantId, crewDto.userDto().id());

        // then
        then(crewPolicy).should(only())
                .validateCrewCreator(anyLong(), anyLong());
        then(crewApplicationPolicy).should(only())
                .validateApplication(anyLong(), anyLong());
        then(crewApplicationStore).should(only())
                .delete(anyLong(), anyLong());
    }

    @DisplayName("[leaveCrew] 크루를 탈퇴한다.")
    @Test
    void should_LeaveCrew() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long userId = 11L;

        // when
        crewService.leaveCrew(crewDto, userId);

        // then
        then(crewPolicy).should(only())
                .validateCrewMember(anyLong(), anyLong());
        then(meetingMemberStore).should(only())
                .removeAllByUserIdAndCrewId(anyLong(), anyLong());
        then(meetingStore).should(only())
                .deleteAllByUserIdAndCrewId(anyLong(), anyLong());
        then(crewMemberStore).should(only())
                .subtractCrewMember(anyLong(), anyLong());
    }

    @DisplayName("[leaveCrew] 탈퇴하려는 크루원이 크루장일 경우, CreatorDeleteForbidden 예외를 발생시킨다.")
    @Test
    void when_CrewCreator_Then_CreatorDeleteForbiddenException() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long userId = crewDto.userDto().id();

        // when & then
        Exception exception = assertThrows(BusinessException.class,
                () -> crewService.leaveCrew(crewDto, userId));

        assertEquals(CrewErrorCode.CREATOR_DELETE_FORBIDDEN.getMessage(), exception.getMessage());
        then(crewMemberReader).shouldHaveNoInteractions();
        then(meetingMemberStore).shouldHaveNoInteractions();
        then(meetingStore).shouldHaveNoInteractions();
        then(crewMemberStore).shouldHaveNoInteractions();
    }

    @DisplayName("크루원을 추방한다.")
    @Test
    void should_ExpelMember() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long memberId = 11L;

        // when
        crewService.expelMember(crewDto, memberId, crewDto.userDto().id());

        // then
        then(crewPolicy).should(times(1))
                .validateCrewCreator(anyLong(), anyLong());
        then(crewPolicy).should(times(1))
                .validateCrewMember(anyLong(), anyLong());
        then(meetingMemberStore).should(only())
                .removeAllByUserIdAndCrewId(anyLong(), anyLong());
        then(meetingStore).should(only())
                .deleteAllByUserIdAndCrewId(anyLong(), anyLong());
        then(crewMemberStore).should(only())
                .subtractCrewMember(anyLong(), anyLong());
    }

    @DisplayName("추방하려는 크루원이 크루장일 경우, CreatorDeleteForbidden 예외를 발생시킨다.")
    @Test
    void when_CrewCreator_Then_CreatorExpelForbiddenException() {
        // given
        CrewDto crewDto = createCrewDto().build().get();
        Long userId = crewDto.userDto().id();

        // when & then
        Exception exception = assertThrows(BusinessException.class,
                () -> crewService.expelMember(crewDto, userId, userId));

        assertEquals(CrewErrorCode.CREATOR_DELETE_FORBIDDEN.getMessage(), exception.getMessage());
        then(crewMemberReader).shouldHaveNoInteractions();
        then(meetingMemberStore).shouldHaveNoInteractions();
        then(meetingStore).shouldHaveNoInteractions();
        then(crewMemberStore).shouldHaveNoInteractions();
    }

    @DisplayName("[getJoinedAt] 크루 가입일을 가져온다.")
    @Test
    void should_GetJoinedAt() {
        // given
        Long crewId = 1L;
        Long userId = 2L;
        given(crewMemberReader.getCrewMemberByCrewIdAndUserId(crewId, userId))
                .willReturn(createCrewMember(crewId, userId).build().get());

        // when
        crewService.getJoinedAt(crewId, userId);

        // then
        then(crewMemberReader).should(only())
                .getCrewMemberByCrewIdAndUserId(anyLong(), anyLong());
    }

    @DisplayName("[getAppliedAt] 크루 가입신청일을 가져온다.")
    @Test
    void should_GetAppliedAt() {
        // given
        Long crewId = 1L;
        Long userId = 2L;
        given(crewApplicationReader.getCrewApplicationByCrewIdAndUserId(crewId, userId))
                .willReturn(createCrewApplication(crewId, userId).build().get());

        // when
        crewService.getAppliedAt(crewId, userId);

        // then
        then(crewApplicationReader).should(only())
                .getCrewApplicationByCrewIdAndUserId(anyLong(), anyLong());
    }

    @DisplayName("[getUserDtosByCrew] 크루에 속한 유저들을 가져온다.")
    @Test
    void should_GetUserDtosByCrew() {
        // given
        Long crewId = 726L;
        Long userId = 326L;
        List<User> members = List.of(
                createUser(1L).build().get(),
                createUser(2L).build().get(),
                createUser(3L).build().get()
        );
        given(crewMemberReader.getMembersByCrewId(crewId))
                .willReturn(members);

        // when
        List<UserDto> userDtos = crewService.getMembersByCrew(crewId, userId);

        // then
        assertEquals(members.size(), userDtos.size());
        then(crewPolicy).should(only())
                .validateCrewMember(anyLong(), anyLong());
        then(crewMemberReader).should(only())
                .getMembersByCrewId(anyLong());
    }

    @DisplayName("[getApplicantsByCrew] 크루에 가입신청한 유저들을 가져온다.")
    @Test
    void should_GetApplicantsByCrew() {
        // given
        Long crewId = 726L;
        Long userId = 326L;
        List<CrewApplication> applicants = List.of(
                createCrewApplication(crewId, 111L).build().get(),
                createCrewApplication(crewId, 222L).build().get(),
                createCrewApplication(crewId, 333L).build().get()
        );
        given(crewApplicationReader.getApplicantsByCrewId(crewId))
                .willReturn(applicants);
        given(userReader.getUserById(anyLong()))
                .willReturn(
                        createUser(111L).build().get(),
                        createUser(222L).build().get(),
                        createUser(333L).build().get()
                );

        // when
        List<CrewApplicationDto> applicationDtos = crewService.getApplicantsByCrew(crewId, userId);

        // then
        assertEquals(applicants.size(), applicationDtos.size());
        then(crewPolicy).should(only())
                .validateCrewCreator(anyLong(), anyLong());
        then(crewApplicationReader).should(only())
                .getApplicantsByCrewId(anyLong());
        then(userReader).should(times(applicants.size()))
                .getUserById(anyLong());
    }
}