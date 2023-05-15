package io.github.dougllasfps.controllers;

import io.github.dougllasfps.dtos.FollowerRequest;
import io.github.dougllasfps.model.Follower;
import io.github.dougllasfps.model.User;
import io.github.dougllasfps.repository.FollowerRepository;
import io.github.dougllasfps.repository.UserRepository;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;


    Long userId;
    Long followerId;

    /*@Inject
    FollowerRepository followerRepository;*/

    @BeforeEach
    @Transactional
    public void setUP() {
        // ususário padrão dos testes
        var user = new User();
        user.setAge(20);
        user.setName("Fulano");

        userRepository.persist(user);
        userId = user.getId();

        var follower = new User();
        follower.setAge(25);
        follower.setName("Cicrano");

        userRepository.persist(follower);
        followerId = follower.getId();

        //criando um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);

        followerRepository.persist(followerEntity);


    }

    @Test
    @DisplayName("Should return 409 when followerId is equal to userId")
    public void sameUserAsFollowerTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(409)
                .body(Matchers.is("You can't follow yourself!"));
    }

    @Test
    @DisplayName("Should return 404 on follow a user when userId doesn't exist")
    public void userNotFoundWhenTryingToFollowTest() {

        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var inexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", inexistentUserId)
        .when()
                .put()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should follow a user")
    public void followUserTest() {

        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
        .when()
                .put()
        .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should return 404 on list of user followers and userId doesn't exist")
    public void userNotFoundWhenListingFollowersTest() {
        var inexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should return a list of user's followers")
    public void listFollowersTest() {
        var response =
            given()
                    .contentType(ContentType.JSON)
                    .pathParam("userId", userId)
            .when()
                    .get()
            .then()
                    .extract()
                    .response();
        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    @Test
    @DisplayName("Should return 404 on unfollow user and userId doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest() {
        var inexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", inexistentUserId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Should unfollow an user")
    public void unfollowUserTest() {
        var inexistentUserId = 99;

        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
        .when()
                .delete()
        .then()
                .statusCode(204);
    }



}