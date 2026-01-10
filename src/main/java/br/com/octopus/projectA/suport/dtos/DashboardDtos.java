package br.com.octopus.projectA.suport.dtos;

import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class DashboardDtos {

    @Data
    public static class OnlineAgentsCountResponse {
        private long onlineCount;
        private long totalAgents;
        private long offlineCount;
        private Instant lastUpdated;
    }

    @Data
    public static class AgentStatusHistoryItem {
        private UUID id;
        private Long agentId;
        private String agentName;
        private String status; // "ONLINE" ou "OFFLINE"
        private Instant statusDate;
        private Instant timestamp;
    }

    @Data
    public static class AgentStatusHistoryPage {
        private List<AgentStatusHistoryItem> content;
        private PageableInfo pageable;
        private long totalElements;
        private int totalPages;
        private boolean last;
        private boolean first;
        private int numberOfElements;
        private int size;
        private int number;
        private SortInfo sort;
        private boolean empty;
    }

    @Data
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
        private SortInfo sort;
    }

    @Data
    public static class SortInfo {
        private boolean sorted;
        private boolean unsorted;
        private boolean empty;
    }

    @Data
    public static class ErrorResponse {
        private String error;
        private String message;
    }
}
