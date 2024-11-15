package br.com.octopus.projectA.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_TURN")
public class TurnEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TIPO_TURNO_ID", nullable = false)
    private TipoTurnoEntity tipoTurno;

    @Column(name = "DATE")
    private LocalDate dataTurno;

    @OneToOne
    @JoinColumn(name = "AGENT_ID")
    private AgentEntity agent;

}
