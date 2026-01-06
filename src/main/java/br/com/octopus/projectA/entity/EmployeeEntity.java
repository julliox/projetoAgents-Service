package br.com.octopus.projectA.entity;

import br.com.octopus.projectA.entity.enuns.StatusEnum;
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
@Table(name = "TBL_EMPLOYEE")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @OneToOne
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "DES_INFO")
    private String desInfo;

    @Column(name = "ADMISSION_DATE")
    private LocalDate admissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private StatusEnum status;

    // Métodos auxiliares para facilitar acesso aos dados do User
    public String getName() {
        return user != null ? user.getName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}

