package web.app.loltp.loltpserver.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import web.app.loltp.loltpserver.dtos.SummonerDto;
import web.app.loltp.loltpserver.exceptions.LolTpException;

import java.util.Arrays;
import java.util.Collections;

@Service
public class RiotService {

    private final RestTemplate restTemplate;
    private final EmailService emailService;

    @Value("${riot.api.url}")
    private String riotApiUrl;

    @Value("${RIOT_API_KEY}")
    private String riotApiKey;

    public RiotService(RestTemplateBuilder restTemplateBuilder, EmailService emailService) {
        this.restTemplate = restTemplateBuilder.build();
        this.emailService = emailService;
    }

    public SummonerDto findSummoner(String username) {
        String url = this.riotApiUrl + "/lol/summoner/v4/summoners/by-name/" + username;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-Riot-Token", this.riotApiKey);

        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<SummonerDto> response = null;
        try {
            response = this.restTemplate
                    .exchange(url, HttpMethod.GET, request, SummonerDto.class, 1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

        throw new LolTpException("INVALID_USERNAME", HttpStatus.BAD_REQUEST);
    }

    public void registerTeam(String teamName, String[] usernames) {
        if (usernames.length != 5 || !areUsernamesValid(usernames)) {
            throw new LolTpException("INVALID_USERNAMES", HttpStatus.BAD_REQUEST);
        }

        String subject = "Registered Team - " + teamName;
        String content = teamName + "\n\nSummoners:\n* " + String.join("\n* ", usernames);
        this.emailService.sendEmailToLoLTp(subject, content);
    }

    private boolean areUsernamesValid(String[] usernames) {
        try {
            return Arrays.stream(usernames)
                    .map(username -> this.findSummoner(username))
                    .allMatch(summoner -> summoner != null);
        } catch (Exception ex) {
            return false;
        }
    }
}
