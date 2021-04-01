package web.app.loltp.loltpserver.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummonerDto {

    public String id;
    public String accountId;
    public String puuid;
    public String name;
    public Integer profileIconId;
    public String revisionDate;
    public Integer summonerLevel;
}
