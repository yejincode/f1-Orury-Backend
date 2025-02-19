package org.orury.client.crew.application;

import static org.orury.domain.global.constants.NumberConstants.CREW_PAGINATION_SIZE;
import static org.orury.domain.global.constants.NumberConstants.MAXIMUM_OF_CREW_DETAIL_THUMBNAILS;
import static org.orury.domain.global.constants.NumberConstants.MAXIMUM_OF_CREW_LIST_THUMBNAILS;

import org.apache.commons.lang3.function.TriFunction;
import org.orury.client.crew.interfaces.message.CrewMessage;
import org.orury.client.crew.interfaces.request.CrewRequest;
import org.orury.client.crew.interfaces.response.CrewApplicantsResponse;
import org.orury.client.crew.interfaces.response.CrewIdResponse;
import org.orury.client.crew.interfaces.response.CrewMembersResponse;
import org.orury.client.crew.interfaces.response.CrewResponse;
import org.orury.client.crew.interfaces.response.CrewsResponse;
import org.orury.client.user.application.UserService;
import org.orury.domain.crew.domain.dto.CrewApplicationDto;
import org.orury.domain.crew.domain.dto.CrewDto;
import org.orury.domain.user.domain.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrewFacade {
    private final CrewService crewService;
    private final UserService userService;

    public CrewIdResponse createCrew(CrewRequest request, MultipartFile image, Long userId) {
        var userDto = userService.getUserDtoById(userId);
        var crewDto = request.toDto(userDto);
        var crewId = crewService.createCrew(crewDto, image);

        return CrewIdResponse.of(crewId);
    }

    public Page<CrewsResponse> getCrewsByRecommendedSort(int page, Long userId) {
        var pageRequest = PageRequest.of(page, CREW_PAGINATION_SIZE);
        var userDto = userService.getUserDtoById(userId);
        Page<CrewDto> crewDtos = crewService.getCrewDtosByRecommendedSort(pageRequest, userDto);
        return convertCrewDtosToCrewsResponses(crewDtos);
    }

    public Page<CrewsResponse> getCrewsByPopularSort(int page) {
        var pageRequest = PageRequest.of(page, CREW_PAGINATION_SIZE);
        Page<CrewDto> crewDtos = crewService.getCrewDtosByPopularSort(pageRequest);
        return convertCrewDtosToCrewsResponses(crewDtos);
    }

    public Page<CrewsResponse> getCrewsByActiveSort(int page) {
        var pageRequest = PageRequest.of(page, CREW_PAGINATION_SIZE);
        Page<CrewDto> crewDtos = crewService.getCrewDtosByActiveSort(pageRequest);
        return convertCrewDtosToCrewsResponses(crewDtos);
    }

    public Page<CrewsResponse> getCrewsByLatestSort(int page) {
        var pageRequest = PageRequest.of(page, CREW_PAGINATION_SIZE);
        Page<CrewDto> crewDtos = crewService.getCrewDtosByLatestSort(pageRequest);
        return convertCrewDtosToCrewsResponses(crewDtos);
    }

    public List<CrewsResponse> getJoinedCrews(Long userId) {
        List<CrewDto> crewDtos = crewService.getJoinedCrewDtos(userId);
        return convertCrewDtosToCrewsResponses(crewDtos, userId, crewService::getJoinedAt, CrewsResponse::ofWithJoinedTime);
    }

    public List<CrewsResponse> getAppliedCrews(Long userId) {
        List<CrewDto> crewDtos = crewService.getAppliedCrewDtos(userId);
        return convertCrewDtosToCrewsResponses(crewDtos, userId, crewService::getAppliedAt, CrewsResponse::ofWithAppliedTime);
    }

    public CrewResponse getCrewByCrewId(Long userId, Long crewId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        boolean isMember = crewService.existCrewMember(crewId, userId);
        List<String> userImages = crewService.getUserImagesByCrew(crewDto, MAXIMUM_OF_CREW_DETAIL_THUMBNAILS);
        return CrewResponse.of(crewDto, isMember, userImages, userId);
    }

    public void updateCrewInfo(Long crewId, CrewRequest request, Long userId) {
        var oldCrewDto = crewService.getCrewDtoById(crewId);
        var newCrewDto = request.toDto(oldCrewDto);
        crewService.updateCrewInfo(oldCrewDto, newCrewDto, userId);
    }

    public void updateCrewImage(Long crewId, MultipartFile image, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.updateCrewImage(crewDto, image, userId);
    }

    public void deleteCrew(Long crewId, Long userId) { // TODO: 삭제 유예(약 7일?)는 구현 필요.
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.deleteCrew(crewDto, userId);
    }

    public CrewMessage applyCrew(Long crewId, Long userId, String answer) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        UserDto userDto = userService.getUserDtoById(userId);
        return crewService.applyCrew(crewDto, userDto, answer);
    }

    public void withdrawApplication(Long crewId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.withdrawApplication(crewDto, userId);
    }

    public void approveApplication(Long crewId, Long applicantId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.approveApplication(crewDto, applicantId, userId);
    }

    public void disapproveApplication(Long crewId, Long applicantId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.disapproveApplication(crewDto, applicantId, userId);
    }

    public void leaveCrew(Long crewId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.leaveCrew(crewDto, userId);
    }

    public void expelMember(Long crewId, Long memberId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        crewService.expelMember(crewDto, memberId, userId);
    }

    private Page<CrewsResponse> convertCrewDtosToCrewsResponses(Page<CrewDto> crewDtos) {
        return crewDtos.map(crewDto -> {
            List<String> userImages = crewService.getUserImagesByCrew(crewDto, MAXIMUM_OF_CREW_LIST_THUMBNAILS);
            return CrewsResponse.of(crewDto, userImages);
        });
    }

    public List<CrewMembersResponse> getCrewMembers(Long crewId, Long userId) {
        CrewDto crewDto = crewService.getCrewDtoById(crewId);
        List<UserDto> userDtos = crewService.getMembersByCrew(crewId, userId);
        return userDtos.stream()
                .map(userDto -> CrewMembersResponse.of(userDto, userId, crewDto.userDto().id()))
                .toList();
    }

    private List<CrewsResponse> convertCrewDtosToCrewsResponses(
            List<CrewDto> crewDtos,
            Long userId, BiFunction<Long, Long, LocalDateTime> biFunction,
            TriFunction<CrewDto, List<String>, LocalDateTime, CrewsResponse> triFunction
    ) {
        return crewDtos.stream()
                .map(crewDto -> {
                    List<String> userImages = crewService.getUserImagesByCrew(crewDto, MAXIMUM_OF_CREW_LIST_THUMBNAILS);
                    LocalDateTime time = biFunction.apply(crewDto.id(), userId);
                    return triFunction.apply(crewDto, userImages, time);
                }).toList();
    }

    public List<CrewApplicantsResponse> getCrewApplicants(Long crewId, Long userId) {
        List<CrewApplicationDto> userDtos = crewService.getApplicantsByCrew(crewId, userId);
        return userDtos.stream()
                .map(CrewApplicantsResponse::of)
                .toList();
    }
}