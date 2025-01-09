package com.project.sosmed.repository;

import com.project.sosmed.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {
    @Query("SELECT f FROM Follow f WHERE f.followingUser = :followingUserId AND f.followedUser = :followedUserId")
    Optional<Follow> findExistingFollow(
            @Param("followedUserId") UUID followedUserId,
            @Param("followingUserId") UUID followingUserId
    );
}
