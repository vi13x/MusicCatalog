package com.example.musiccatalog;

import com.example.musiccatalog.entity.Album;
import com.example.musiccatalog.repository.AlbumRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    private final AlbumRepository repository;

    public SeedData(AlbumRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {

        repository.clear();

        repository.save(new Album(null, "Замай", "Oxxxymiron", 2015));
        repository.save(new Album(null, "Город под подошвой", "Баста", 2016));
        repository.save(new Album(null, "Капкан", "Скриптонит", 2017));
        repository.save(new Album(null, "Минимал", "Miyagi & Andy Panda", 2018));
        repository.save(new Album(null, "Cadillac", "Моргенштерн", 2020));
        repository.save(new Album(null, "Розовое вино", "Feduk", 2017));
        repository.save(new Album(null, "Juice", "Big Baby Tape", 2018));
        repository.save(new Album(null, "Тает лёд", "Грибы", 2017));
        repository.save(new Album(null, "Медуза", "Matrang", 2018));
        repository.save(new Album(null, "Дежавю", "PHARAOH", 2018));
    }
}