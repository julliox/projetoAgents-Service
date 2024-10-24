package br.com.octopus.undergroundFiber.service;

import br.com.octopus.undergroundFiber.entity.ProfileEntity;
import br.com.octopus.undergroundFiber.repository.ProfileRepository;
import br.com.octopus.undergroundFiber.suport.dtos.ProfileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    public ProfileEntity findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id " + id));
    }

    public ProfileEntity findByName(String name) {
        return profileRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with name " + name));
    }

    public ProfileDTO getProfileById(Long id) {
        return toDto(findById(id));
    }

    private ProfileDTO toDto(ProfileEntity entity) {
        return new ProfileDTO(entity.getId(), entity.getName(), entity.getStatus());
    }
}