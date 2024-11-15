package br.com.octopus.projectA.entity;

import br.com.octopus.projectA.entity.enuns.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "TBL_USER")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID")
    private ProfileEntity profile;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private StatusEnum status;

}
