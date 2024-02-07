package org.fastcampus.oruryclient.review.converter.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import org.fastcampus.orurydomain.review.dto.ReviewDto;

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ReviewUpdateRequest(
        String content,
        float score
) {
    public static ReviewUpdateRequest of(
            String content,
            float score
    ) {
        return new ReviewUpdateRequest(
                content,
                score
        );
    }

    public ReviewDto toDto(ReviewDto reviewDto) {

        return ReviewDto.of(
                reviewDto.id(),
                content,
                reviewDto.images(),
                score,
                reviewDto.interestCount(),
                reviewDto.likeCount(),
                reviewDto.helpCount(),
                reviewDto.thumbCount(),
                reviewDto.angryCount(),
                reviewDto.userDto(),
                reviewDto.gymDto(),
                reviewDto.createdAt(),
                null
        );
    }

}
