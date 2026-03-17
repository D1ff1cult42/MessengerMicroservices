package com.d1ff.fulltextsearchservice.controller;

import com.d1ff.dto.response.ErrorResponse;
import com.d1ff.fulltextsearchservice.dto.request.MessageSearchRequest;
import com.d1ff.fulltextsearchservice.dto.response.MessageSearchResponse;
import com.d1ff.fulltextsearchservice.service.interfaces.SearchService;
import com.d1ff.page.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "FullTextSearchController", description = "Controller for full-text search of messages via Elasticsearch")
public class FullTextSearchController {

    private final SearchService searchService;

    @Operation(summary = "Search messages (POST)",
            description = "Perform a full-text search for messages within a specific chat. " +
                    "Uses fuzzy matching and filters out deleted messages.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have access to this chat",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @PostMapping
    public ResponseEntity<PageResponse<MessageSearchResponse>> searchPost(
            @RequestBody @Valid MessageSearchRequest searchRequest,
            @Parameter(description = "ID of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(PageResponse.fromPage(searchService.search(searchRequest, userId, PageRequest.of(page, size))));
    }

    @Operation(summary = "Search messages (GET)",
            description = "Perform a full-text search with query parameters (no request body required).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "403", description = "User does not have access to this chat",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<PageResponse<MessageSearchResponse>> searchGet(
            @Parameter(description = "ID of the chat", required = true)
            @RequestParam UUID chatId,
            @Parameter(description = "Search query text", required = true)
            @RequestParam String query,
            @Parameter(description = "ID of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        MessageSearchRequest searchRequest = new MessageSearchRequest(chatId, query);
        return ResponseEntity.ok(PageResponse.fromPage(searchService.search(searchRequest, userId, PageRequest.of(page, size))));
    }
}
