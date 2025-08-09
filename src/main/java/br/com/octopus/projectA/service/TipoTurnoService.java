package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.TipoTurnoEntity;
import br.com.octopus.projectA.repository.TipoTurnoRepository;
import br.com.octopus.projectA.suport.dtos.TipoTurnoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoTurnoService {

    @Autowired
    private TipoTurnoRepository tipoTurnoRepository;

    /**
     * Busca todos os tipos de turno e os converte para DTOs.
     * @return Lista de TipoTurnoDTO
     */
    public List<TipoTurnoDTO> getAllTipoTurnos() {
        return tipoTurnoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca um TipoTurno por ID e converte para DTO.
     * @param id ID do TipoTurno
     * @return TipoTurnoDTO
     */
    public TipoTurnoDTO getTipoTurnoById(Long id) {
        TipoTurnoEntity entity = findById(id);
        return toDto(entity);
    }

    /**
     * Cria um novo TipoTurno.
     * @param dto TipoTurnoDTO contendo os dados para criação
     * @return TipoTurnoDTO criado
     */
    public TipoTurnoDTO createTipoTurno(TipoTurnoDTO dto) {
        if (tipoTurnoRepository.existsByDescricao(dto.descricao())) {
            throw new IllegalArgumentException("Tipo de turno com a descrição '" + dto.descricao() + "' já existe.");
        }

        TipoTurnoEntity entity = TipoTurnoEntity.builder()
                .descricao(dto.descricao())
                .cod(dto.cod())
                .valorJunior(dto.valorJunior())
                .valorSenior(dto.valorSenior())
                .build();

        TipoTurnoEntity savedEntity = tipoTurnoRepository.save(entity);
        return toDto(savedEntity);
    }

    /**
     * Atualiza um TipoTurno existente.
     * @param id ID do TipoTurno a ser atualizado
     * @param dto TipoTurnoDTO com os novos dados
     * @return TipoTurnoDTO atualizado
     */
    public TipoTurnoDTO updateTipoTurno(Long id, TipoTurnoDTO dto) {
        TipoTurnoEntity existingEntity = findById(id);

        if (!existingEntity.getDescricao().equals(dto.descricao()) && tipoTurnoRepository.existsByDescricao(dto.descricao())) {
            throw new IllegalArgumentException("Tipo de turno com a descrição '" + dto.descricao() + "' já existe.");
        }

        existingEntity.setDescricao(dto.descricao());
        existingEntity.setCod(dto.cod());
        existingEntity.setValorJunior(dto.valorJunior());
        existingEntity.setValorSenior(dto.valorSenior());


        TipoTurnoEntity updatedEntity = tipoTurnoRepository.save(existingEntity);
        return toDto(updatedEntity);
    }

    /**
     * Deleta um TipoTurno por ID.
     * @param id ID do TipoTurno a ser deletado
     */
    public void deleteTipoTurno(Long id) {
        TipoTurnoEntity entity = findById(id);
        tipoTurnoRepository.delete(entity);
    }

    /**
     * Busca um TipoTurno por ID.
     * @param id ID do TipoTurno
     * @return TipoTurnoEntity
     */
    private TipoTurnoEntity findById(Long id) {
        return tipoTurnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoTurno não encontrado com ID " + id));
    }

    /**
     * Converte uma entidade TipoTurno para DTO.
     * @param entity TipoTurnoEntity
     * @return TipoTurnoDTO
     */
    private TipoTurnoDTO toDto(TipoTurnoEntity entity) {
        return new TipoTurnoDTO(
                entity.getId(),
                entity.getDescricao(),
                entity.getCod(),
                entity.getValorJunior(),
                entity.getValorSenior()
        );
    }
}
