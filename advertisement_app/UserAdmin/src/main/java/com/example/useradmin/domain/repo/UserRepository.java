package com.example.useradmin.domain.repo;

import com.example.useradmin.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository
{

    @Autowired
    private EntityManager entityManager;


    public void createUser(User user)
    {
        this.entityManager.persist(user);
    }


    public List<User> findAll()
    {
        TypedQuery<User> query = entityManager.createQuery(
                "FROM User u ", User.class
        );
        return query.getResultList();
    }

    public User findUserByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
                "FROM User u WHERE u.username = :username", User.class
        );
        query.setParameter("username", username);

        List<User> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }





    public Page<User> getUsers(Pageable pageable) {

        // 1. Get paginated results
        Query query = entityManager.createQuery("FROM User u ");
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<User> users = query.getResultList();

        // 2. Get total count (no pagination)
        Query countQuery = entityManager.createQuery("SELECT COUNT(u) FROM User u");
        Long total = (Long) countQuery.getSingleResult();

        // 3. Return a Spring Page object
        return new PageImpl<>(users, pageable, total);
    }





    @Transactional
    public User disableUser(UUID userId) {
        User user = this.entityManager.find(User.class, userId);
        int updated = entityManager.createQuery("UPDATE User u SET u.isEnabled = false WHERE u.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();

        return updated==0 ? null : user;
    }

    @Transactional
    public User enableUser(UUID userId) {
        User user = this.entityManager.find(User.class, userId);
        int updated = entityManager.createQuery("UPDATE User u SET u.isEnabled = true WHERE u.id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();

        return updated==0 ? null : user;
    }



    public Optional<User> getUserProfile(UUID userId)
    {
        return Optional.ofNullable(this.entityManager.find(User.class, userId));
    }


}
