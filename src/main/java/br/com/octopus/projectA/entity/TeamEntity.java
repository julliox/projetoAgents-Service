package br.com.octopus.projectA.entity;

import br.com.octopus.projectA.entity.enuns.TeamStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_TEAM")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "WORK_START_TIME", nullable = false, length = 5)
    private String workStartTime; // formato HH:mm

    @Column(name = "WORK_END_TIME", nullable = false, length = 5)
    private String workEndTime;   // formato HH:mm

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private TeamStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "team_agents",
        joinColumns = @JoinColumn(name = "team_id"),
        inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    private Set<AgentEntity> agents = new HashSet<>();

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

