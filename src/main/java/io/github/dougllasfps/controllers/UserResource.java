package io.github.dougllasfps.controllers;

import io.github.dougllasfps.dtos.CreateUserRequest;
import io.github.dougllasfps.dtos.ResponseError;
import io.github.dougllasfps.model.User;
import io.github.dougllasfps.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    private UserRepository userRepository;
    private Validator validator;

    @Inject
    public UserResource(UserRepository userRepository, Validator validator) {

        this.userRepository = userRepository;
        this.validator = validator;
    }

    @POST
    @Transactional
    public Response createUser(CreateUserRequest userRequest) {

        Set<ConstraintViolation<CreateUserRequest>> violentions = validator.validate(userRequest);

        if(!violentions.isEmpty()) {
            return ResponseError.createFromValidation(violentions)
                                .withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        userRepository.persist(user);

        return Response.status(Response.Status.CREATED.getStatusCode()).entity(user).build();
    }

    @GET
    public Response listAllUsers() {
        PanacheQuery<User> users = userRepository.findAll();
        return Response.ok(users.list()).build();
    }

    @GET
    @Path("{id}")
    public Response listUserById(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if(user != null) {
            return Response.ok(user).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        User user = userRepository.findById(id);

        if(user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Transactional
    @Path("{id}")
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userRequest) {
        User user = userRepository.findById(id);

        if(user != null) {
            user.setName(userRequest.getName());
            user.setAge(userRequest.getAge());

            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
