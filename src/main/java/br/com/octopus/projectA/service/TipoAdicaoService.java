package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.TipoAdicaoEntity;
import br.com.octopus.projectA.repository.TipoAdicaoRepository;
import br.com.octopus.projectA.suport.dtos.TipoAdicaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoAdicaoService {

    @Autowired
    private TipoAdicaoRepository tipoAdicaoRepository;

    /**
     * Busca todos os tipos de adição e os converte para DTOs.
     * @return Lista de TipoAdicaoDTO
     */
    public List<TipoAdicaoDTO> getAllTiposAdicao() {
        return tipoAdicaoRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca um TipoAdicao por ID e converte para DTO.
     * @param id ID do TipoAdicao
     * @return TipoAdicaoDTO
     */
    public TipoAdicaoDTO getTipoAdicaoById(Long id) {
        TipoAdicaoEntity entity = findById(id);
        return toDto(entity);
    }

    /**
     * Cria um novo TipoAdicao.
     * @param dto TipoAdicaoDTO contendo os dados para criação
     * @return TipoAdicaoDTO criado
     */
    public TipoAdicaoDTO createTipoAdicao(TipoAdicaoDTO dto) {
        if (tipoAdicaoRepository.existsByDesTipoAdicao(dto.desTipoAdicao())) {
            throw new IllegalArgumentException("Tipo de adição com a descrição '" + dto.desTipoAdicao() + "' já existe.");
        }

        TipoAdicaoEntity entity = TipoAdicaoEntity.builder()
                .desTipoAdicao(dto.desTipoAdicao())
                .build();

        TipoAdicaoEntity savedEntity = tipoAdicaoRepository.save(entity);
        return toDto(savedEntity);
    }

    /**
     * Atualiza um TipoAdicao existente.
     * @param id ID do TipoAdicao a ser atualizado
     * @param dto TipoAdicaoDTO com os novos dados
     * @return TipoAdicaoDTO atualizado
     */
    public TipoAdicaoDTO updateTipoAdicao(Long id, TipoAdicaoDTO dto) {
        TipoAdicaoEntity existingEntity = findById(id);

        if (!existingEntity.getDesTipoAdicao().equals(dto.desTipoAdicao()) && tipoAdicaoRepository.existsByDesTipoAdicao(dto.desTipoAdicao())) {
            throw new IllegalArgumentException("Tipo de adição com a descrição '" + dto.desTipoAdicao() + "' já existe.");
        }

        existingEntity.setDesTipoAdicao(dto.desTipoAdicao());

        TipoAdicaoEntity updatedEntity = tipoAdicaoRepository.save(existingEntity);
        return toDto(updatedEntity);
    }

    /**
     * Deleta um TipoAdicao por ID.
     * @param id ID do TipoAdicao a ser deletado
     */
    public void deleteTipoAdicao(Long id) {
        TipoAdicaoEntity entity = findById(id);
        tipoAdicaoRepository.delete(entity);
    }

    /**
     * Busca um TipoAdicao por ID.
     * @param id ID do TipoAdicao
     * @return TipoAdicaoEntity
     */
    private TipoAdicaoEntity findById(Long id) {
        return tipoAdicaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TipoAdicao não encontrado com ID " + id));
    }

    /**
     * Converte uma entidade TipoAdicao para DTO.
     * @param entity TipoAdicaoEntity
     * @return TipoAdicaoDTO
     */
    private TipoAdicaoDTO toDto(TipoAdicaoEntity entity) {
        return new TipoAdicaoDTO(
                entity.getId(),
                entity.getDesTipoAdicao()
        );
    }
}
