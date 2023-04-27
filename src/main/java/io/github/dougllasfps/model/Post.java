package io.github.dougllasfps.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_text")
    private String text;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
