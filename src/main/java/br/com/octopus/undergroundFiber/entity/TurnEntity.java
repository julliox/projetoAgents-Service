package br.com.octopus.undergroundFiber.entity;

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

    @Column(name = "TIP_TURN")
    private String typeTurno;

    @Column(name = "DATE")
    private LocalDate dataTurno;

    @OneToOne
    @JoinColumn(name = "AGENT_ID")
    private AgentEntity client;

}
