package ru.practicum.client;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.client.exception.BadRequest;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final RestClient restClient;
    @Value("${stats-sever.url}")
    private String serverUrl;
    @Value("${application.name}")
    private String app;

    public StatsClient() {
        restClient = RestClient.builder().build();
    }

    public ResponseEntity<HitDto> hit(HttpServletRequest hitRequest) {
        String url = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/hit")
                .build()
                .toUriString();

        HitDto hitDto = new HitDto();
        hitDto.setIp(hitRequest.getRemoteAddr());
        hitDto.setUri(hitRequest.getRequestURI());
        hitDto.setTimestamp(LocalDateTime.now());
        hitDto.setApp(app);

        return restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toEntity(HitDto.class);
    }

    public ResponseEntity<List<StatsDto>> getStats(LocalDateTime start, LocalDateTime end,
                                                   List<String> uris, Boolean unique) {
        if (start == null || end == null) {
            throw new BadRequest("Необходимо указывать корректные даты для обработки запроса");
        }
        if (start.isAfter(end)) {
            throw new BadRequest("Дата начала не может быть позже даты окончания");
        }

        String dateFormatter = "yyyy-MM-dd HH:mm:ss";
        String strStart = DateTimeFormatter.ofPattern(dateFormatter).format(start);
        String strEnd = DateTimeFormatter.ofPattern(dateFormatter).format(end);

         UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(serverUrl)
                .path("/stats")
                .queryParam("start", strStart)
                .queryParam("end", strEnd);

        if (uris != null && !uris.isEmpty()) {
            uriBuilder.queryParam("uris", uris);
        }
        if (unique != null) {
            uriBuilder.queryParam("unique", unique);
        }

        String url = uriBuilder.build()
                .toUriString();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {
                });
    }

}
