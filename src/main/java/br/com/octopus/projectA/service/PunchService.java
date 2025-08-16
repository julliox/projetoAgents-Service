package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.PunchEntity;
import br.com.octopus.projectA.entity.enuns.PunchType;
import br.com.octopus.projectA.repository.PunchRepository;
import br.com.octopus.projectA.suport.dtos.PunchDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class PunchService {

    @Autowired
    private PunchRepository punchRepository;

    public Optional<PunchEntity> findByIdempotencyKey(String key) {
        return punchRepository.findByIdempotencyKey(key);
    }

    @Transactional
    public PunchEntity registerPunch(Long agentId, PunchDtos.PunchRequest request, String idempotencyKey) {
        Instant now = Instant.now();

        // Idempotência
        if (idempotencyKey != null) {
            Optional<PunchEntity> existing = punchRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        // Estado atual
        Optional<PunchEntity> last = punchRepository.findTopByAgentIdOrderByTimestampServerDesc(agentId);
        boolean isClockedIn = last.isPresent() && last.get().getType() == PunchType.ENTRADA;

        // Regras de sequência
        if (request.getAction() == PunchType.ENTRADA && isClockedIn) {
            throw new IllegalStateException("Já existe uma ENTRADA aberta");
        }
        if (request.getAction() == PunchType.SAIDA && !isClockedIn) {
            throw new IllegalStateException("Não há ENTRADA aberta para registrar SAÍDA");
        }

        PunchEntity entity = PunchEntity.builder()
                .agentId(agentId)
                .type(request.getAction())
                .timestampServer(now)
                .source(request.getSource())
                .deviceId(request.getDeviceId())
                .locationLat(request.getLocation() != null ? request.getLocation().getLat() : null)
                .locationLng(request.getLocation() != null ? request.getLocation().getLng() : null)
                .notes(request.getNotes())
                .idempotencyKey(idempotencyKey)
                .createdAt(now)
                .build();

        return punchRepository.save(entity);
    }

    public PunchDtos.StateResponse getState(Long agentId) {
        Instant now = Instant.now();
        Optional<PunchEntity> last = punchRepository.findTopByAgentIdOrderByTimestampServerDesc(agentId);

        boolean isClockedIn = last.isPresent() && last.get().getType() == PunchType.ENTRADA;

        PunchDtos.StateResponse resp = new PunchDtos.StateResponse();
        resp.setAgentId(agentId);
        resp.setClockedIn(isClockedIn);
        if (last.isPresent()) {
            PunchDtos.PunchItemDTO item = new PunchDtos.PunchItemDTO();
            item.setId(last.get().getId());
            item.setType(last.get().getType());
            item.setTimestamp(last.get().getTimestampServer());
            item.setSource(last.get().getSource());
            item.setNotes(last.get().getNotes());
            resp.setLastPunch(item);
        }

        if (isClockedIn) {
            Optional<PunchEntity> lastEntry = punchRepository.findTopByAgentIdAndTypeOrderByTimestampServerDesc(agentId, PunchType.ENTRADA);
            if (lastEntry.isPresent()) {
                PunchDtos.SessionDTO session = new PunchDtos.SessionDTO();
                session.setEntryId(lastEntry.get().getId());
                session.setEntryTimestamp(lastEntry.get().getTimestampServer());
                session.setDurationSeconds(Duration.between(lastEntry.get().getTimestampServer(), now).getSeconds());
                resp.setActiveSession(session);
            }
        }
        resp.setServerTime(now);
        return resp;
    }

    public PunchDtos.HistoryResponse getHistory(Long agentId, Instant from, Instant to, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PunchEntity> result;
        if (from != null && to != null) {
            result = punchRepository.findByAgentIdAndTimestampServerBetween(agentId, from, to, pageable);
        } else {
            result = punchRepository.findByAgentId(agentId, pageable);
        }

        PunchDtos.HistoryResponse resp = new PunchDtos.HistoryResponse();
        resp.setPage(page);
        resp.setSize(size);
        resp.setTotal(result.getTotalElements());
        resp.setHasNext(result.hasNext());
        resp.setItems(result.getContent().stream().map(e -> {
            PunchDtos.PunchItemDTO item = new PunchDtos.PunchItemDTO();
            item.setId(e.getId());
            item.setType(e.getType());
            item.setTimestamp(e.getTimestampServer());
            item.setSource(e.getSource());
            item.setNotes(e.getNotes());
            return item;
        }).toList());
        return resp;
    }

    public PunchDtos.LastStatusResponse getLastStatus(Long agentId) {
        var now = Instant.now();
        var last = punchRepository.findTopByAgentIdOrderByTimestampServerDesc(agentId);
        PunchDtos.LastStatusResponse resp = new PunchDtos.LastStatusResponse();
        resp.setAgentId(agentId);
        resp.setLastType(last.map(PunchEntity::getType).orElse(null));
        resp.setServerTime(now);
        return resp;
    }
}


