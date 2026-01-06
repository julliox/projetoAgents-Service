package br.com.octopus.projectA.entity;

import br.com.octopus.projectA.entity.enuns.PunchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_PUNCH")
public class PunchEntity {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    // Referencia User.id (o usuário autenticado que registra o ponto)
    // Para obter dados do colaborador, buscar EmployeeEntity pelo userId
    @Column(name = "AGENT_ID", nullable = false)
    private Long agentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private PunchType type;

    // Horário registrado no servidor em UTC
    @Column(name = "TIMESTAMP_SERVER", nullable = false)
    private Instant timestampServer;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "DEVICE_ID")
    private String deviceId;

    @Column(name = "LOCATION_LAT")
    private Double locationLat;

    @Column(name = "LOCATION_LNG")
    private Double locationLng;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @Column(name = "IDEMPOTENCY_KEY", unique = true)
    private String idempotencyKey;

    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;
}


