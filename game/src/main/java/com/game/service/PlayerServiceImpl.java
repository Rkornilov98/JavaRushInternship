package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
@Transactional
public class PlayerServiceImpl implements PlayerService{
    private PlayerRepo playerRepo;

    public PlayerServiceImpl(){

    }
    @Autowired
    public PlayerServiceImpl(PlayerRepo playerRepo) {
        super();
        this.playerRepo = playerRepo;
    }

    @Override
    public List<Player> allPlayers(String name,
                                   String title,
                                   Long after,
                                   Long before,
                                   Integer minExperience,
                                   Integer maxExperience,
                                   Integer minLevel,
                                   Integer maxLevel,
                                   Race race, Profession profession, Boolean banned)
    {
        //untilNextLevel не нужен для фильтрации данных, его не сообщать
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        final List<Player> list = new ArrayList<>();
        playerRepo.findAll().forEach((player) -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession!=null && player.getProfession()!=profession) return;
            if (afterDate != null && player.getBirthday().before(afterDate)) return;
            if (beforeDate != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned().booleanValue() != banned.booleanValue()) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            list.add(player);
        });
        return list;
    }

    @Override
    public Player add(Player player) {
        return playerRepo.save(player);
    }

    @Override
    public void delete(Player player) {
        playerRepo.delete(player);
    }

    @Override
    public Player edit(Player oldPlayer, Player newPlayer) throws IllegalArgumentException  {
        boolean changeExp = false;

        final String name = newPlayer.getName();//name
        if (name != null) {
            if (isNameValid(name)) {
                oldPlayer.setName(name);
            } else {
                throw new IllegalArgumentException();
            }
        }

        final String title = newPlayer.getTitle();//title
        if (title != null) {
            if (isTitleValid(title)) {
                oldPlayer.setTitle(title);
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (newPlayer.getRace() != null) {
            oldPlayer.setRace(newPlayer.getRace());
        }

        if (newPlayer.getProfession()!=null){
            oldPlayer.setProfession((newPlayer.getProfession()));
        }

        final Date birthday = newPlayer.getBirthday();//Long date or Date date?
        if (birthday != null) {
            if (isBirthdayValid(birthday)) {
                oldPlayer.setBirthday(birthday);
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (newPlayer.getBanned() != null) {     //Banned
            oldPlayer.setBanned(newPlayer.getBanned());
        }

        final Integer experience = newPlayer.getExperience();//Experience
        if (experience != null) {
            if (isExpValid(experience)) {
                oldPlayer.setExperience(experience);
                changeExp = true;
            } else {
                throw new IllegalArgumentException();
            }
        }

        if (changeExp) {
            final Integer level = calculateLevel(oldPlayer.getExperience());
            oldPlayer.setLevel(level);
            final Integer untilNextLevel = calculateUntilNextLevel(oldPlayer.getLevel(), oldPlayer.getExperience());
            oldPlayer.setUntilNextLevel(untilNextLevel);
        }
        playerRepo.save(oldPlayer);
        return oldPlayer;
    }

    @Override
    public Player getById(Long id) {
        return playerRepo.findById(id).orElse(null);
    }

    @Override
    public List<Player> sortPlayers(List<Player> players, PlayerOrder order) {
        if (order != null) {
            players.sort((player1, player2) -> {
                /*
                ID("id"), // default
                NAME("name"),
                EXPERIENCE("experience"),
                BIRTHDAY("birthday"),
                LEVEL("level");*/
                switch (order) {
                    case ID: return player1.getId().compareTo(player2.getId());
                    case NAME: return player1.getName().compareTo(player2.getName());
                    case EXPERIENCE: return player1.getExperience().compareTo(player2.getExperience());
                    case BIRTHDAY: return player1.getBirthday().compareTo(player2.getBirthday());
                    case LEVEL: return player1.getLevel().compareTo(player2.getLevel());
                    default: return 0;
                }
            });
        }
        return players;
    }

    @Override
    public List<Player> getPage(List<Player> players, Integer pageNumber, Integer pageSize) {
        final Integer page = pageNumber == null ? 0 : pageNumber;
        final Integer size = pageSize == null ? 3 : pageSize;
        final int from = page * size;
        int to = from + size;
        if (to > players.size()) to = players.size();
        return players.subList(from, to);
    }

    @Override
    public boolean isPlayerValid(Player player) {
        return player!=null && isNameValid(player.getName())
                && isTitleValid(player.getTitle())
                && isExpValid(player.getExperience())
                && isBirthdayValid(player.getBirthday());
    }

    @Override
    public Integer calculateLevel(Integer experience) {
        return (int)(Math.sqrt(2500 + 200*experience) - 50)/100;
    }

    @Override
    public Integer calculateUntilNextLevel(Integer level, Integer experience) {
        return 50*(level+1)*(level+2) - experience;
    }


    private boolean isNameValid(String name){
    final int maxLength =12;
    return name!=null && !name.isEmpty() && name.length() <= maxLength;
    }

    private boolean isTitleValid(String title){
        final int maxLength = 30;
        return title!=null && title.length()<=maxLength;//title может быть пустым!
    }

    private boolean isExpValid(Integer experience){
        final int minValue = 0;
        final int maxValue = 10_000_000;
        return experience!=null && experience.compareTo(minValue) >=0 && experience.compareTo(maxValue) <=0;
    }

    private boolean isBirthdayValid(Date date){
        return date != null && date.getTime() >= 0 &&
                date.getTime() >= 946674000482L && date.getTime() <= 32535205199494L;
    }

}
