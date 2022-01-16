package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface PlayerService {

    //untilNextLevel не нужен для фильтрации данных, его не сообщать
    List<Player> allPlayers(String name,
                            String title,
                            Long after,
                            Long before,
                            Integer minExperience,
                            Integer maxExperience,
                            Integer minLevel,
                            Integer maxLevel,
                            Race race, Profession profession, Boolean banned);
    Player add(Player player);
    void delete(Player player);
    Player edit(Player oldPlayer, Player newPlayer)throws IllegalArgumentException;
    Player getById(Long id);
    List<Player> sortPlayers(List<Player> players, PlayerOrder order);
    //int sortPlayerCount(List<Player> players);
    List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize);
    boolean isPlayerValid(Player player);
    Integer calculateLevel(Integer experience);
    Integer calculateUntilNextLevel(Integer level, Integer experience);
}
