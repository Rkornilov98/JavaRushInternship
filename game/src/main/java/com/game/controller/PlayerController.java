package com.game.controller;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class PlayerController {

PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    public PlayerController() {

    }

    @RequestMapping(value = "/rest/players", method = RequestMethod.GET)
    public List<Player> getPlayers(@RequestParam(value = "name", required = false)String name,
                                   @RequestParam(value = "title", required = false)String title,
                                   @RequestParam(value = "race", required = false)Race race,
                                   @RequestParam(value = "profession", required = false)Profession profession,
                                   @RequestParam(value = "after", required = false)Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false)Boolean banned,
                                   @RequestParam(value = "minExperience", required = false)Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false)Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false)Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false)Integer maxLevel,
                                   @RequestParam(value = "order", required = false) PlayerOrder order,
                                   @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                   @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        final List<Player> players = playerService.allPlayers(name,
                title, after,
                before,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,race,
                profession, banned);
        final List<Player> sortedPlayers = playerService.sortPlayers(players, order);//прописать реализацию
        return playerService.getPage(sortedPlayers, pageNumber, pageSize);
    }

    @RequestMapping(path="/rest/players/count", method = RequestMethod.GET)
    public Integer getPlayersCount(@RequestParam(value = "name", required = false)String name,
                                   @RequestParam(value = "title", required = false)String title,
                                   @RequestParam(value = "race", required = false)Race race,
                                   @RequestParam(value = "profession", required = false)Profession profession,
                                   @RequestParam(value = "after", required = false) Long after,
                                   @RequestParam(value = "before", required = false) Long before,
                                   @RequestParam(value = "banned", required = false)Boolean banned,
                                   @RequestParam(value = "minExperience", required = false)Integer minExperience,
                                   @RequestParam(value = "maxExperience", required = false)Integer maxExperience,
                                   @RequestParam(value = "minLevel", required = false)Integer minLevel,
                                   @RequestParam(value = "maxLevel", required = false)Integer maxLevel)
    {

        return playerService.allPlayers(name,
                title,
                after,
                before,
                minExperience,
                maxExperience,
                minLevel,
                maxLevel,race,
                profession, banned).size();
    }

    @RequestMapping(path = "/rest/players", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Player> addPlayer(@RequestBody Player player){
        if (!playerService.isPlayerValid(player)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (player.getBanned() == null) player.setBanned(false);
        final Integer level = playerService.calculateLevel(player.getExperience());
        player.setLevel(level);
        final Integer untilNextLevel = playerService.calculateUntilNextLevel(player.getLevel(), player.getExperience());
        player.setUntilNextLevel(untilNextLevel);
        final Player savedPlayer = playerService.add(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.GET)
    public ResponseEntity<Player> getPlayer(@PathVariable(value = "id") String pathId) {
        final Long id = convertIdToLong(pathId);
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final Player player = playerService.getById(id);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }


    @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(
            @PathVariable(value = "id") String pathId,
            @RequestBody Player player
    ) {
        final Long id = convertIdToLong(pathId);
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final ResponseEntity<Player> entity = getPlayer(pathId);
        final Player savedPlayer = entity.getBody();
        if (savedPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //or return entity;
        }

        final Player result;
        try {
            result = playerService.edit(savedPlayer, player);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(path = "/rest/players/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Player> deletePlayer(@PathVariable(value = "id") String pathId) {
        final ResponseEntity<Player> entity = getPlayer(pathId);

        final Long id = convertIdToLong(pathId);
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Player savedPlayer = entity.getBody();
        if (savedPlayer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); //or return entity
        }
        playerService.delete(savedPlayer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Long convertIdToLong(String pathId) {
        if (pathId == null) {
            return null;
        } else try {
            return Long.parseLong(pathId);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

