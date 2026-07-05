package com.electronics.dto.chat;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ChatResponse {

    private String sessionId;
    private List<ContentBlock> blocks;
    private Instant timestamp;
    private List<String> sources;
}
