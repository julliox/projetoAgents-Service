package br.com.octopus.projectA.service;

import br.com.octopus.projectA.entity.EmployeeEntity;
import br.com.octopus.projectA.entity.PunchEntity;
import br.com.octopus.projectA.entity.enuns.PunchType;
import br.com.octopus.projectA.entity.enuns.StatusEnum;
import br.com.octopus.projectA.repository.EmployeeRepository;
import br.com.octopus.projectA.repository.PunchRepository;
import br.com.octopus.projectA.suport.dtos.DashboardDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PunchRepository punchRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    /**
     * Retorna a contagem de agentes online, offline e total.
     */
    @Transactional(readOnly = true)
    public DashboardDtos.OnlineAgentsCountResponse getOnlineAgentsCount() {
        Instant now = Instant.now();

        // Buscar todos os agentes ativos (employees com status ACTIVE)
        List<EmployeeEntity> allActiveAgents = employeeRepository.findAll().stream()
                .filter(emp -> emp.getStatus() == StatusEnum.ACTIVE && emp.getUser() != null)
                .collect(Collectors.toList());

        long totalAgents = allActiveAgents.size();

        // Buscar agentes online (último registro é ENTRADA)
        List<Long> onlineAgentIds = punchRepository.findOnlineAgentIds("ENTRADA");
        long onlineCount = onlineAgentIds.size();
        long offlineCount = totalAgents - onlineCount;

        DashboardDtos.OnlineAgentsCountResponse response = new DashboardDtos.OnlineAgentsCountResponse();
        response.setOnlineCount(onlineCount);
        response.setTotalAgents(totalAgents);
        response.setOfflineCount(offlineCount);
        response.setLastUpdated(now);

        return response;
    }

    /**
     * Retorna o histórico de mudanças de status dos agentes com paginação e filtros.
     */
    @Transactional(readOnly = true)
    public DashboardDtos.AgentStatusHistoryPage getAgentStatusHistory(
            Integer page,
            Integer size,
            String status,
            Long agentId,
            Instant startDate,
            Instant endDate
    ) {
        // Validação e defaults
        int pageNumber = page != null ? Math.max(0, page) : 0;
        int pageSize = size != null ? Math.min(Math.max(1, size), 50) : 10; // máximo 50

        // Converter status para PunchType
        PunchType punchType = null;
        if (status != null) {
            if ("ONLINE".equalsIgnoreCase(status)) {
                punchType = PunchType.ENTRADA;
            } else if ("OFFLINE".equalsIgnoreCase(status)) {
                punchType = PunchType.SAIDA;
            }
            // Se status inválido, punchType permanece null e não filtra por tipo
        }

        // Criar Pageable com ordenação por timestamp descendente
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "timestampServer"));

        // Construir Specification dinamicamente para evitar problemas com NULL no PostgreSQL
        Specification<PunchEntity> spec = buildStatusHistorySpecification(agentId, punchType, startDate, endDate);

        // Buscar registros paginados usando Specification
        Page<PunchEntity> punchPage = punchRepository.findAll(spec, pageable);

        // Mapear para DTOs com nomes dos agentes
        Map<Long, String> agentNameCache = new HashMap<>();
        
        List<DashboardDtos.AgentStatusHistoryItem> content = punchPage.getContent().stream()
                .map(punch -> {
                    String agentName = agentNameCache.computeIfAbsent(
                            punch.getAgentId(),
                            id -> resolveAgentName(id)
                    );

                    DashboardDtos.AgentStatusHistoryItem item = new DashboardDtos.AgentStatusHistoryItem();
                    item.setId(punch.getId());
                    item.setAgentId(punch.getAgentId());
                    item.setAgentName(agentName);
                    item.setStatus(punch.getType() == PunchType.ENTRADA ? "ONLINE" : "OFFLINE");
                    item.setStatusDate(punch.getTimestampServer());
                    item.setTimestamp(punch.getTimestampServer());

                    return item;
                })
                .collect(Collectors.toList());

        // Construir resposta com estrutura Page do Spring
        DashboardDtos.AgentStatusHistoryPage response = new DashboardDtos.AgentStatusHistoryPage();
        response.setContent(content);
        response.setTotalElements(punchPage.getTotalElements());
        response.setTotalPages(punchPage.getTotalPages());
        response.setLast(punchPage.isLast());
        response.setFirst(punchPage.isFirst());
        response.setNumberOfElements(punchPage.getNumberOfElements());
        response.setSize(punchPage.getSize());
        response.setNumber(punchPage.getNumber());
        response.setEmpty(punchPage.isEmpty());

        // Pageable info
        DashboardDtos.PageableInfo pageableInfo = new DashboardDtos.PageableInfo();
        pageableInfo.setPageNumber(punchPage.getPageable().getPageNumber());
        pageableInfo.setPageSize(punchPage.getPageable().getPageSize());

        DashboardDtos.SortInfo sortInfo = new DashboardDtos.SortInfo();
        sortInfo.setSorted(punchPage.getSort().isSorted());
        sortInfo.setUnsorted(!punchPage.getSort().isSorted());
        sortInfo.setEmpty(punchPage.getSort().isEmpty());

        pageableInfo.setSort(sortInfo);
        // Não temos acesso direto ao pageable do response, então não incluímos aqui
        // O frontend já tem informações suficientes com os campos do Page

        // Sort info para o response
        response.setSort(sortInfo);

        return response;
    }

    /**
     * Constroi uma Specification dinamicamente baseada nos filtros fornecidos.
     * Isso evita problemas com tipos de parâmetros NULL no PostgreSQL.
     */
    private Specification<PunchEntity> buildStatusHistorySpecification(
            Long agentId,
            PunchType punchType,
            Instant startDate,
            Instant endDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por agentId (se fornecido)
            if (agentId != null) {
                predicates.add(cb.equal(root.get("agentId"), agentId));
            }

            // Filtro por tipo (ENTRADA/SAIDA) (se fornecido)
            if (punchType != null) {
                predicates.add(cb.equal(root.get("type"), punchType));
            }

            // Filtro por data inicial (se fornecida)
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestampServer"), startDate));
            }

            // Filtro por data final (se fornecida)
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("timestampServer"), endDate));
            }

            // Se não houver predicados, retorna condição sempre verdadeira (retorna todos)
            if (predicates.isEmpty()) {
                return cb.conjunction(); // condição sempre verdadeira
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Resolve o nome do agente a partir do userId (agentId no contexto de ponto).
     */
    private String resolveAgentName(Long agentId) {
        try {
            Optional<EmployeeEntity> employeeOpt = employeeRepository.findByUser_Id(agentId);
            if (employeeOpt.isPresent()) {
                EmployeeEntity employee = employeeOpt.get();
                String name = employee.getName();
                return name != null && !name.isEmpty() ? name : "Colaborador " + agentId;
            }
            return "Colaborador " + agentId;
        } catch (Exception e) {
            return "Colaborador " + agentId;
        }
    }
}
