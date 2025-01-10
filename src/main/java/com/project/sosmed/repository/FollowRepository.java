package com.project.sosmed.repository;

import com.project.sosmed.entity.Follow;
import com.project.sosmed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    @Query("SELECT f FROM Follow f WHERE f.followingUser = :followingUser AND f.followedUser = :followedUser")
    Optional<Follow> findExistingFollow(
            @Param("followedUser") User followedUser,
            @Param("followingUser") User followingUser
    );
}
