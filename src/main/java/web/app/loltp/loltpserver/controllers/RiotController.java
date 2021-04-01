package web.app.loltp.loltpserver.controllers;

import org.springframework.web.bind.annotation.*;
import web.app.loltp.loltpserver.dtos.SummonerDto;
import web.app.loltp.loltpserver.dtos.TeamDto;
import web.app.loltp.loltpserver.services.RiotService;

@CrossOrigin("*")
@RestController
@RequestMapping()
public class RiotController {

    private final RiotService riotService;

    public RiotController(RiotService riotService) {
        this.riotService = riotService;
    }

    @GetMapping("/summoner/{username}")
    public SummonerDto getSummoner(@PathVariable String username) {
        return this.riotService.findSummoner(username);
    }

    @PostMapping("/register/team")
    public String registerTeam(@RequestBody TeamDto teamDto) {
        this.riotService.registerTeam(teamDto.getName(), teamDto.getUsernames());
        return "Ok!";
    }
}
