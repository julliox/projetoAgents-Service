package br.com.octopus.projectA.service;

import br.com.octopus.projectA.suport.dtos.FolhaPagementoDTO;
import br.com.octopus.projectA.suport.dtos.PaymentSlipDTO;
import br.com.octopus.projectA.repository.TurnRepository;
import br.com.octopus.projectA.suport.dtos.SalarioDTO;
import br.com.octopus.projectA.suport.dtos.TurnoFolhaPagamentoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentSlipService {

    @Autowired
    private TurnRepository turnRepository;

    @Autowired
    private SalarioService salarioService;

    public FolhaPagementoDTO geraDadosFolhaDePagamentoPorAgenteId(Long agentId) {
        LocalDate currentDate = LocalDate.now();
        //TODO ARRUMAR
        YearMonth currentYearMonth = YearMonth.now().minusMonths(1);
        LocalDate startOfMonth = currentYearMonth.atDay(1);
        LocalDate endOfMonth = currentYearMonth.atEndOfMonth();
        LocalDate oneYearAgo = currentDate.minusYears(1);

        List<Object[]> results = turnRepository.findPaymentSlipsByAgentAndMonthNative(agentId, oneYearAgo, startOfMonth, endOfMonth);
        List<PaymentSlipDTO> paymentSlips = new ArrayList<>();

        for (Object[] row : results) {
            PaymentSlipDTO dto = new PaymentSlipDTO(
                    ((Number) row[0]).longValue(), // agentId
                    (String) row[1],               // agentName
                    (String) row[2],               // email
                    (String) row[3],               // phoneNumber
                    (String) row[4],               // desInfo
                    ((Date) row[5]).toLocalDate(), // admissionDate
                    (String) row[6],               // status
                    ((Number) row[7]).longValue(), // turnId
                    ((Date) row[8]).toLocalDate(), // turnDate
                    (String) row[9],               // turnDescription
                    (BigDecimal) row[10]           // turnValue
            );
            paymentSlips.add(dto);
        }

        SalarioDTO salarioDTO = new SalarioDTO();
        salarioDTO = salarioService.calcularSalarioPorAgente(agentId);

        FolhaPagementoDTO folhaPagementoDTO = new FolhaPagementoDTO();
        folhaPagementoDTO.setAgentId(agentId);
        folhaPagementoDTO.setEmail(paymentSlips.get(0).getEmail());
        folhaPagementoDTO.setAdmissionDate(paymentSlips.get(0).getAdmissionDate());
        folhaPagementoDTO.setStatus(paymentSlips.get(0).getStatus());
        folhaPagementoDTO.setDesInfo(paymentSlips.get(0).getDesInfo());
        folhaPagementoDTO.setAgentName(paymentSlips.get(0).getAgentName());
        folhaPagementoDTO.setSalarioBase(salarioDTO.getSalarioBase());
        ArrayList<TurnoFolhaPagamentoDTO> turnoFolhaPagamentoDTOs = new ArrayList<>();
        for (PaymentSlipDTO dto : paymentSlips) {
            TurnoFolhaPagamentoDTO turnoFolhaPagamentoDTO = new TurnoFolhaPagamentoDTO();
            turnoFolhaPagamentoDTO.setTurnDescription(dto.getTurnDescription());
            turnoFolhaPagamentoDTO.setTurnValue(dto.getTurnValue());
            turnoFolhaPagamentoDTO.setTurnDate(dto.getTurnDate());
            turnoFolhaPagamentoDTOs.add(turnoFolhaPagamentoDTO);

        }
        folhaPagementoDTO.setTotalTurns(turnoFolhaPagamentoDTOs.size());
        folhaPagementoDTO.setTurnos(turnoFolhaPagamentoDTOs);

        return folhaPagementoDTO;
    }
}
