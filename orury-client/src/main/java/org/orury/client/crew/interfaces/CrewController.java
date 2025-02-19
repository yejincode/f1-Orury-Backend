package org.orury.client.crew.interfaces;

import org.orury.client.crew.application.CrewFacade;
import org.orury.client.crew.interfaces.message.CrewMessage;
import org.orury.client.crew.interfaces.request.CrewRequest;
import org.orury.client.crew.interfaces.response.CrewApplicantsResponse;
import org.orury.client.crew.interfaces.response.CrewIdResponse;
import org.orury.client.crew.interfaces.response.CrewMembersResponse;
import org.orury.client.crew.interfaces.response.CrewResponse;
import org.orury.client.crew.interfaces.response.CrewsResponse;
import org.orury.domain.base.converter.ApiResponse;
import org.orury.domain.user.domain.dto.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/crews")
@RestController
public class CrewController {
    private final CrewFacade crewFacade;

    @Operation(summary = "크루 생성", description = "크루 생성에 필요한 정보를 받아, 크루를 생성한다.")
    @PostMapping
    public ApiResponse createCrew(
            @Valid @RequestPart CrewRequest request,
            @RequestPart(required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CrewIdResponse response = crewFacade.createCrew(request, image, userPrincipal.id());
        return ApiResponse.of(CrewMessage.CREW_CREATED.getMessage(), response);
    }

    @Operation(summary = "크루 추천순 조회", description = "크루를 추천 순으로 조회한다.")
    @GetMapping("/recommend")
    public ApiResponse getCrewsByRecommendedSort(@RequestParam int page, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Page<CrewsResponse> pageResponse = crewFacade.getCrewsByRecommendedSort(page, userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "크루 인기순 조회", description = "크루를 인기 순으로 조회한다.")
    @GetMapping("/popular")
    public ApiResponse getCrewsByPopularSort(@RequestParam int page) {
        Page<CrewsResponse> pageResponse = crewFacade.getCrewsByPopularSort(page);

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "크루 활동순 조회", description = "크루를 활동 순으로 조회한다.")
    @GetMapping("/active")
    public ApiResponse getCrewsByActiveSort(@RequestParam int page) {
        Page<CrewsResponse> pageResponse = crewFacade.getCrewsByActiveSort(page);

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "크루 최신순 조회", description = "크루를 최신 순으로 조회한다.")
    @GetMapping("/latest")
    public ApiResponse getCrewsByLatestSort(@RequestParam int page) {
        Page<CrewsResponse> pageResponse = crewFacade.getCrewsByLatestSort(page);

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "가입 신청한 크루 조회", description = "내가 가입 신청한 크루를 조회한다.")
    @GetMapping("/mycrew")
    public ApiResponse getJoinedCrews(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CrewsResponse> pageResponse = crewFacade.getJoinedCrews(userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "내 크루 조회", description = "내가 가입한 크루를 조회한다.")
    @GetMapping("/myApplications")
    public ApiResponse getAppliedCrews(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CrewsResponse> pageResponse = crewFacade.getAppliedCrews(userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREWS_READ.getMessage(), pageResponse);
    }

    @Operation(summary = "크루 상세 조회", description = "크루를 상세 조회한다.")
    @GetMapping("/{crewId}")
    public ApiResponse getCrewByCrewId(@PathVariable Long crewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CrewResponse response = crewFacade.getCrewByCrewId(userPrincipal.id(), crewId);

        return ApiResponse.of(CrewMessage.CREW_READ.getMessage(), response);
    }

    @Operation(summary = "크루 정보 변경", description = "크루 정보를 변경한다.")
    @PatchMapping("/{crewId}")
    public ApiResponse updateCrewInfo(
            @PathVariable Long crewId,
            @Valid @RequestBody CrewRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.updateCrewInfo(crewId, request, userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREW_INFO_UPDATED.getMessage());
    }

    @Operation(summary = "크루 이미지 변경", description = "크루 이미지를 변경한다.")
    @PatchMapping("/{crewId}/images")
    public ApiResponse updateCrewImage(
            @PathVariable Long crewId,
            @RequestPart MultipartFile image,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.updateCrewImage(crewId, image, userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREW_IMAGE_UPDATED.getMessage());
    }

    @Operation(summary = "크루 삭제", description = "크루를 삭제한다.")
    @DeleteMapping("/{crewId}")
    public ApiResponse deleteCrew(@PathVariable Long crewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        crewFacade.deleteCrew(crewId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.CREW_DELETED.getMessage());
    }

    @Operation(summary = "크루 가입신청", description = "크루 가입 신청한다.")
    @PostMapping("/{crewId}/applications")
    public ApiResponse applyCrew(
            @PathVariable Long crewId,
            @RequestBody(required = false) String answer,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CrewMessage result = crewFacade.applyCrew(crewId, userPrincipal.id(), answer);

        return ApiResponse.of(result.getMessage());
    }

    @Operation(summary = "크루신청 철회", description = "크루 가입신청을 철회한다.")
    @DeleteMapping("/{crewId}/withdrawals")
    public ApiResponse withdrawApplication(
            @PathVariable Long crewId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.withdrawApplication(crewId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.APPLICATION_WITHDRAWN.getMessage());
    }

    @Operation(summary = "크루신청 수락", description = "크루 가입신청을 수락한다.")
    @PostMapping("/{crewId}/approvals/{applicantId}")
    public ApiResponse approveApplication(
            @PathVariable Long crewId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.approveApplication(crewId, applicantId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.APPLICATION_APPROVED.getMessage());
    }

    @Operation(summary = "크루신청 거절", description = "크루 가입신청을 거절한다.")
    @PostMapping("/{crewId}/disapprovals/{applicantId}")
    public ApiResponse disapproveApplication(
            @PathVariable Long crewId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.disapproveApplication(crewId, applicantId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.APPLICATION_DISAPPROVED.getMessage());
    }

    @Operation(summary = "크루 탈퇴", description = "크루원이 크루를 탈퇴한다.")
    @DeleteMapping("/{crewId}/leaves")
    public ApiResponse leaveCrew(
            @PathVariable Long crewId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.leaveCrew(crewId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.MEMBER_LEAVED.getMessage());
    }

    @Operation(summary = "크루원 강퇴", description = "크루 운영자가 크루 멤버를 강퇴시킨다.")
    @DeleteMapping("/{crewId}/expulsions/{memberId}")
    public ApiResponse expelMember(
            @PathVariable Long crewId,
            @PathVariable Long memberId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        crewFacade.expelMember(crewId, memberId, userPrincipal.id());

        return ApiResponse.of(CrewMessage.MEMBER_EXPELLED.getMessage());
    }

    @Operation(summary = "크루멤버 목록 조회", description = "크루id에 따른 크루멤버 목록을 조회한다.")
    @GetMapping("{crewId}/members")
    public ApiResponse getCrewMembers(@PathVariable Long crewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CrewMembersResponse> response = crewFacade.getCrewMembers(crewId, userPrincipal.id());
        return ApiResponse.of(CrewMessage.CREW_MEMBERS_READ.getMessage(), response);
    }

    @Operation(summary = "크루 지원자 목록 조회", description = "크루id에 따른 크루 지원자 목록을 조회한다.")
    @GetMapping("{crewId}/applicants")
    public ApiResponse getCrewApplicants(@PathVariable Long crewId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CrewApplicantsResponse> response = crewFacade.getCrewApplicants(crewId, userPrincipal.id());
        return ApiResponse.of(CrewMessage.CREW_APPLICANTS_READ.getMessage(), response);
    }
}
