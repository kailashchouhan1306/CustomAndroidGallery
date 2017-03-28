package com.kailash.gallery.models;

/**
 * @author Kailash Chouhan
 * @creationDate 17-Feb-2017
 */
public class Album {
    public String name;
    public String cover;
    public int count;

    public Album(String name, String cover) {
        this.name = name;
        this.cover = cover;
    }

    public Album(String name, String cover, int count) {
        this.name = name;
        this.cover = cover;
        this.count = count;
    }
}
