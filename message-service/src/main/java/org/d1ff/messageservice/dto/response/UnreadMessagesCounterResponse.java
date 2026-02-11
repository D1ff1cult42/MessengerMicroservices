package org.d1ff.messageservice.dto.response;

import java.util.UUID;

public record UnreadMessagesCounterResponse(Long unreadMessagesCounter,
                                            UUID chatId,
                                            UUID forUser) {}
