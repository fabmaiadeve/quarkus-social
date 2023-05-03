package io.github.dougllasfps.dtos;

import io.github.dougllasfps.model.Post;
import io.github.dougllasfps.repository.PostRepository;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    private LocalDateTime localDateTime;

    public static PostResponse fromEntity(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setText(post.getText());
        postResponse.setLocalDateTime(post.getDateTime());

        return postResponse;
    }
}
