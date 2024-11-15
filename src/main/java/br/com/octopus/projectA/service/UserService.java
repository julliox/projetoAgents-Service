package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.ProfileEntity;
import br.com.octopus.projectA.entity.UserEntity;
import br.com.octopus.projectA.repository.UserRepository;
import br.com.octopus.projectA.suport.dtos.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    public UserEntity findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email " + email));
    }

    public UserDTO getUserById(Long id) {
        return toDto(findById(id));
    }

    public UserDTO getUserByEmail(String email) {
        return toDto(findByEmail(email));
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserDTO create(UserDTO dto) {
        ProfileEntity profileEntity = profileService.findByName(dto.profile());
        UserEntity entity = new UserEntity(null, dto.name(), dto.email(), dto.email(), profileEntity, dto.status());
        UserEntity savedEntity = userRepository.save(entity);
        return toDto(savedEntity);
    }

    public UserDTO update(Long id, UserDTO dto) {
        UserEntity entity = findById(id);
        ProfileEntity profileEntity = profileService.findByName(dto.profile());
        entity = new UserEntity(id, dto.name(), dto.email(), entity.getPassword(), profileEntity, dto.status());
        UserEntity updatedEntity = userRepository.save(entity);
        return toDto(updatedEntity);
    }

    public void delete(Long id) {
        UserEntity entity = findById(id);
        userRepository.delete(entity);
    }

    private UserDTO toDto(UserEntity entity) {
        return new UserDTO(entity.getId(), entity.getName(), entity.getEmail(), entity.getProfile().getName(), entity.getStatus());
    }

}
