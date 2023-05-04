package io.github.dougllasfps.repository;

import io.github.dougllasfps.model.Follower;
import io.github.dougllasfps.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        // 1° Forma de implementar o método
        /**
        Map<String, Object> params = new HashMap<>();
        params.put("follower", follower);
        params.put("user", user);
        find("follower = :follower and user = :user", params);
        */
        // 2° Forma de implementar o método
        var params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Follower> query = find("follower = :follower and user = :user", params);
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }
}
