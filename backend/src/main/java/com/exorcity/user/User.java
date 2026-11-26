package com.exorcity.user;

import com.exorcity.score.CivicScorePolicy;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    private int civicScore = CivicScorePolicy.DEFAULT_SCORE;

    /** Set for SOCIETY_ADMIN / CORPORATE_ADMIN / SHOP_OWNER. */
    private Long entityId;

    /** Set for WARD_OFFICER. */
    private String ward;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
