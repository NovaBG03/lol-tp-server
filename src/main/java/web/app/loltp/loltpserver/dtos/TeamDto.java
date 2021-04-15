package web.app.loltp.loltpserver.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamDto {
    private String tag;
    private String name;
    private String email;
    private String phone;
    private List<PlayerDto> players;
    private String message;
}
