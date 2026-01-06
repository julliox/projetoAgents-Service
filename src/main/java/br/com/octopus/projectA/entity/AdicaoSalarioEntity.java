package br.com.octopus.projectA.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_ADICAO_SALARIO")
public class AdicaoSalarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "TIPO_ADICAO_ID", nullable = false)
    private TipoAdicaoEntity tipoAdicao;

    @Column(name = "QTY_ADICAO")
    private BigDecimal qtyAdicao;

    @Column(name = "MES_ADICAO")
    private YearMonth mesAdicao;

    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private EmployeeEntity employee;
}
