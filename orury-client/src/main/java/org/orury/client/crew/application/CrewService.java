package org.orury.client.crew.application;

import org.orury.client.crew.interfaces.message.CrewMessage;
import org.orury.domain.crew.domain.dto.CrewApplicationDto;
import org.orury.domain.crew.domain.dto.CrewDto;
import org.orury.domain.crew.domain.dto.CrewMemberDto;
import org.orury.domain.crew.domain.entity.CrewMember;
import org.orury.domain.user.domain.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface CrewService {
    CrewDto getCrewDtoById(Long crewId);

    Long createCrew(CrewDto crewDto, MultipartFile image);

    Page<CrewDto> getCrewDtosByRecommendedSort(Pageable pageable, UserDto userDto);

    Page<CrewDto> getCrewDtosByPopularSort(Pageable pageable);

    Page<CrewDto> getCrewDtosByActiveSort(Pageable pageable);

    Page<CrewDto> getCrewDtosByLatestSort(Pageable pageable);

    List<CrewDto> getJoinedCrewDtos(Long userId);

    List<CrewDto> getAppliedCrewDtos(Long userId);

    List<String> getUserImagesByCrew(CrewDto crewDto, int maximumCount);

    boolean existCrewMember(Long crewId, Long userId);

    void updateCrewInfo(CrewDto oldCrew, CrewDto newCrew, Long userId);

    void updateCrewImage(CrewDto crewDto, MultipartFile image, Long userId);

    void deleteCrew(CrewDto crewDto, Long userId);

    CrewMessage applyCrew(CrewDto crewDto, UserDto userDto, String answer);

    void withdrawApplication(CrewDto crewDto, Long userId);

    void approveApplication(CrewDto crewDto, Long applicantId, Long userId);

    void disapproveApplication(CrewDto crewDto, Long applicantId, Long userId);

    void leaveCrew(CrewDto crewDto, Long userId);

    void expelMember(CrewDto crewDto, Long memberId, Long userId);

    LocalDateTime getJoinedAt(Long crewId, Long userId);

    LocalDateTime getAppliedAt(Long crewId, Long userId);

    List<UserDto> getMembersByCrew(Long crewId, Long userId);

    List<CrewApplicationDto> getApplicantsByCrew(Long crewId, Long userId);

    CrewMember getCrewMemberByCrewIdAndUserId(Long crewId, Long userId);

    void updateMeetingViewed(CrewMemberDto crewMemberDto);
}
