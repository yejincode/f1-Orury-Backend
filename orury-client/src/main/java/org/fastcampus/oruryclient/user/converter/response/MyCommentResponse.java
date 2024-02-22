package org.fastcampus.oruryclient.user.converter.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.fastcampus.oruryclient.global.IdIdentifiable;
import org.fastcampus.orurydomain.comment.dto.CommentDto;

import java.time.LocalDateTime;

public record MyCommentResponse(
        Long id,
        String content,
        Long postId,
        String postTitle,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime updatedAt,
        int likeCount
) implements IdIdentifiable {
    public static MyCommentResponse of(CommentDto commentDto) {
        return new MyCommentResponse(
                commentDto.id(),
                commentDto.content(),
                commentDto.postDto().id(),
                commentDto.postDto().title(),
                commentDto.createdAt(),
                commentDto.updatedAt(),
                commentDto.likeCount()
        );
    }
}
