package web.app.loltp.loltpserver.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import web.app.loltp.loltpserver.dtos.PlayerDto;
import web.app.loltp.loltpserver.dtos.SummonerDto;
import web.app.loltp.loltpserver.dtos.TeamDto;
import web.app.loltp.loltpserver.exceptions.LolTpException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public SummonerDto findSummoner(String summonerName) {
        String url = this.riotApiUrl + "/lol/summoner/v4/summoners/by-name/" + summonerName;

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

    public void registerTeam(TeamDto teamDto) {
        final List<String> summonerNames = teamDto.getPlayers()
                .stream()
                .map(PlayerDto::getSummonerName)
                .collect(Collectors.toList());

        if (summonerNames.size() != 5 || !areUsernamesValid(summonerNames)) {
            throw new LolTpException("INVALID_USERNAMES", HttpStatus.BAD_REQUEST);
        }

        String subject = "Registered Team - " + teamDto.getName();
        String content = CreateContent(teamDto);

        this.emailService.sendEmail(teamDto.getEmail(), subject, content);
        this.emailService.sendEmailToLoLTp(subject, content);
    }

    private String CreateContent(TeamDto teamDto) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("Таг:  " + teamDto.getTag() + "\n");
        contentBuilder.append("Име:  " + teamDto.getName() + "\n");
        contentBuilder.append("Поща: " + teamDto.getEmail() + "\n");
        contentBuilder.append("Тел:  " + teamDto.getPhone() + "\n");
        contentBuilder.append("\n");

        final String message = teamDto.getMessage() == null || teamDto.getMessage().isEmpty() ?
                "Няма съобщене." : teamDto.getMessage();

        contentBuilder.append("Допълнително съобщение: " + message + "\n");
        contentBuilder.append("\n");


        contentBuilder.append("Players:" + "\n");
        for (PlayerDto player : teamDto.getPlayers()) {
            contentBuilder.append("*  " + player.getSummonerName() + "  (" + player.getPlayerName() + ")\n");
        }

        contentBuilder.append("\n");
        contentBuilder.append("\n");

        contentBuilder.append("Регистрацията е завършена! Очаквайте графика на мачовете.");

        return contentBuilder.toString();
    }

    private boolean areUsernamesValid(List<String> summonerNames) {
        try {
            return summonerNames.stream()
                    .map(summonerName -> this.findSummoner(summonerName))
                    .allMatch(summoner -> summoner != null);
        } catch (Exception ex) {
            return false;
        }
    }
}
