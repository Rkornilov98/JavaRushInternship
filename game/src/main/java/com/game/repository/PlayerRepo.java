package com.game.repository;

import com.game.entity.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PlayerRepo extends CrudRepository<Player, Long>{
    /*List<Player> allPlayers();
    void add(Player player);
    void delete(Player player);
    void edit(Player player);
    Player getById(int id);
    List<Player> sortPlayers(List<Player> players, PlayerOrder order);
    int sortPlayerCount(List<Player> players);*/
}
