package com.example.musiccatalog.entity;

public class Album {

    private Long id;
    private String title;
    private String artist;
    private Integer year;

    public Album() {
    }

    public Album(Long id, String title, String artist, Integer year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}