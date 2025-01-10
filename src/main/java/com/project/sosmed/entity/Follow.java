package com.project.sosmed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

//@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "follows")
public class Follow extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_user_id", referencedColumnName = "id")
    private User followingUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_user_id", referencedColumnName = "id")
    private User followedUser;
}
