package io.github.dougllasfps.controllers;

import io.github.dougllasfps.dtos.CreatePostRequest;
import io.github.dougllasfps.model.Follower;
import io.github.dougllasfps.model.Post;
import io.github.dougllasfps.model.User;
import io.github.dougllasfps.repository.FollowerRepository;
import io.github.dougllasfps.repository.PostRepository;
import io.github.dougllasfps.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class)
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach
    @Transactional
    public void setUP() {
        // ususário padrão dos testes
        var user = new User();
        user.setAge(20);
        user.setName("Fulano");

        userRepository.persist(user);
        userId = user.getId();

        // Criada uma postagem para o usuario
        Post post = new Post();
        post.setText("Some text");
        post.setUser(user);

        postRepository.persist(post);

        // ususário que não é um follower
        var userNotFollower = new User();
        userNotFollower.setAge(30);
        userNotFollower.setName("Cicrano");

        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        // ususário que é um follower
        var userFollower = new User();
        userFollower.setAge(35);
        userFollower.setName("Fulaninha");

        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);

        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("Should create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
        .when()
                .post()
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("Should return 404 when trying to make a post for an inexistent user")
    public void postForAnInexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 404 when a user doesn't exist")
    public void listPostUserNotFoundTest() {
        var inexistentUserId = 99;

        given()
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return 400 when a followerId header is not present")
    public void listPostFollowerHeaderNotSendTest() {

        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("Should return 400 when a followerId doesn't exist")
    public void listPostFollowerNotFoundTest() {

        var inexistentFollowerId = 99;

        given()
                .pathParam("userId", userId)
                .header("followerId", inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("Should return 403 when a follower isn't a follower")
    public void listPostNotAFollowerTest() {

        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts!"));
    }

    @Test
    @DisplayName("Should list posts")
    public void listPostsTest() {

        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}