package br.com.octopus.projectA.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_TIPO_TURNO")
public class TipoTurnoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRICAO", nullable = false, unique = true)
    private String descricao;

    @Column(name = "VALOR_JUNIOR", nullable = false)
    private BigDecimal valorJunior;

    @Column(name = "VALOR_SENIOR", nullable = false)
    private BigDecimal valorSenior;
}
