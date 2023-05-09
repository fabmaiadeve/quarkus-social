package io.github.dougllasfps.controllers;

import groovy.json.JsonBuilder;
import io.github.dougllasfps.dtos.CreateUserRequest;
import io.github.dougllasfps.dtos.ResponseError;
import io.github.dougllasfps.model.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import javax.json.bind.JsonbBuilder;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {

    @TestHTTPResource("/users")
    URL apiURL;

    @Test
    @DisplayName("Should create user successfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(20);

        var response =
            given()
                .contentType(ContentType.JSON)
                .body(user)
            .when()
                .post(apiURL)
            .then()
                .extract()
                .response();

        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("Should return error when json is not valid")
    @Order(2)
    public void createUserValidationUserTest() {
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        var response =
                given()
                        .contentType(ContentType.JSON)
                        .body(user)
                .when()
                        .post(apiURL)
                        .then()
                        .extract()
                        .response();

        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error: ", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
/*
        assertEquals("Age is required!", errors.get(0).get("message"));
        assertEquals("Name is required!", errors.get(1).get("message"));
*/
    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
                .contentType(ContentType.JSON)
        .when()
                .get(apiURL)
        .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));
    }
}