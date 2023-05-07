package io.github.dougllasfps.controllers;

import io.github.dougllasfps.dtos.FollowerRequest;
import io.github.dougllasfps.dtos.FollowerResponse;
import io.github.dougllasfps.dtos.FollowersPerUserResponse;
import io.github.dougllasfps.model.Follower;
import io.github.dougllasfps.repository.FollowerRepository;
import io.github.dougllasfps.repository.UserRepository;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.stream.Collectors;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    @Inject
    public FollowerResource(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    @PUT
    @Transactional
    public Response followerUse(@PathParam("userId") Long userId, FollowerRequest request) {

        if(userId.equals(request.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself!").build();
        }

        var user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var follower = userRepository.findById(request.getFollowerId());
        if(follower == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        boolean follows = followerRepository.follows(follower, user);

        if(!follows) {
            var entity = new Follower();
            entity.setUser(user);
            entity.setFollower(follower);

            followerRepository.persist(entity);
        }

        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {

        var user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        var list = followerRepository.findByUser(userId);
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream()
                                                  .map(FollowerResponse::new)
                                                  .collect(Collectors.toList());
        responseObject.setContent(followerList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId,
                                 @QueryParam("followerId") Long followerId) {

        var user = userRepository.findById(userId);
        if(user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        followerRepository.deleteFollowerAndUser(userId, followerId);

        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
