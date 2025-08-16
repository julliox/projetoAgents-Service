package br.com.octopus.projectA.suport.dtos;

import br.com.octopus.projectA.entity.enuns.PunchType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PunchDtos {

    @Data
    public static class LocationDTO {
        private Double lat;
        private Double lng;
    }

    @Data
    public static class PunchRequest {
        @NotNull
        private PunchType action;
        private Instant clientTimestamp;
        private String clientTimezone;
        // Para ADMIN; ignorado para AGENT
        private Long agentId;
        private String source;
        private String deviceId;
        private LocationDTO location;
        private String notes;
    }

    @Data
    public static class SessionDTO {
        private UUID entryId;
        private Instant entryTimestamp;
        private long durationSeconds;
    }

    @Data
    public static class PunchItemDTO {
        private UUID id;
        private PunchType type;
        private Instant timestamp;
        private String source;
        private String notes;
    }

    @Data
    public static class PunchResponse {
        private UUID id;
        private Long agentId;
        private PunchType type;
        private Instant timestampServer;
        private Instant timestampEffective;
        private String status;
        private boolean isClockedIn;
        private SessionDTO session;
        private Instant serverTime;
    }

    @Data
    public static class StateResponse {
        private Long agentId;
        private boolean isClockedIn;
        private PunchItemDTO lastPunch;
        private SessionDTO activeSession;
        private Instant serverTime;
    }

    @Data
    public static class HistoryResponse {
        private List<PunchItemDTO> items;
        private int page;
        private int size;
        private long total;
        private boolean hasNext;
    }

    @Data
    public static class LastStatusResponse {
        private Long agentId;
        private PunchType lastType; // pode ser null quando não houver registros
        private Instant serverTime;
    }
}


