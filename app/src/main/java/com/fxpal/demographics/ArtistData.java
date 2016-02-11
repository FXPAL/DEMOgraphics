package com.fxpal.demographics;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wiese on 1/7/16.
 */
public class ArtistData {
    public String name;
    public String imgUrl;

    public ArtistData(String name, String imgUrl){
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public static final ArrayList<ArtistData> kidArtists = new ArrayList<>();

    static{
        kidArtists.add(new ArtistData("Caspar Babypants","http://img2-ak.lst.fm/i/u/300x300/f78c29aaddb84c0997a7cd77585dae77.png"));
        kidArtists.add(new ArtistData("Raffi","http://img2-ak.lst.fm/i/u/300x300/ef036167fc974f69ae8bf7c934583c0d.png"));
        kidArtists.add(new ArtistData("Dora The Explorer","http://img2-ak.lst.fm/i/u/300x300/06c681acc0ed4c29cca0f8dac011d995.png"));
        kidArtists.add(new ArtistData("Sesame Street","http://img2-ak.lst.fm/i/u/300x300/9f3ce9c379c74b45a9490bf4649fc20d.png"));
        kidArtists.add(new ArtistData("The Wiggles","http://img2-ak.lst.fm/i/u/300x300/40e6c4a9e3c64c979cc906e010853886.png"));
    }

    public static final ArrayList<ArtistData> elderlyArtists = new ArrayList<>();

    static{
        elderlyArtists.add(new ArtistData("Dean Martin", "http://img2-ak.lst.fm/i/u/300x300/44d557bbaaa84c3c98ebe83893c088c6.png"));
        elderlyArtists.add(new ArtistData("Tony Bennett", "http://img2-ak.lst.fm/i/u/300x300/415375f41e454a56caa4452ce7f0df79.png"));
        elderlyArtists.add(new ArtistData("Bobby Darin", "http://img2-ak.lst.fm/i/u/300x300/72d5156714014b0e8210771a213f0333.png"));
        elderlyArtists.add(new ArtistData("Roy Orbison", "http://img2-ak.lst.fm/i/u/300x300/16602211753142f98697ff4893ebaa54.png"));
        elderlyArtists.add(new ArtistData("Diana Krall", "http://img2-ak.lst.fm/i/u/300x300/23e668251bb642d88233e8177420f8c5.png"));
    }

    public static final ArrayList<ArtistData> teenArtists = new ArrayList<>();

    static {
        teenArtists.add(new ArtistData("Emblem3", "http://img2-ak.lst.fm/i/u/300x300/20f0224fa7b1460ec6fd1d151000429c.png"));
        teenArtists.add(new ArtistData("Hunter Hayes", "http://img2-ak.lst.fm/i/u/300x300/a3e9ee748d0675c7298d9845b40747ce.png"));
        teenArtists.add(new ArtistData("Jonas Brothers", "http://img2-ak.lst.fm/i/u/300x300/42cc901bad5943d99b91840224b73974.png"));
        teenArtists.add(new ArtistData("Mac Miller", "http://img2-ak.lst.fm/i/u/300x300/078a1fdb3f774974b3709fecf953f9c1.png"));
        teenArtists.add(new ArtistData("Little Mix", "http://img2-ak.lst.fm/i/u/300x300/1335d6dadb63b47dfbd151932d91dd79.png"));
    }


    public static final ArrayList<ArtistData> adultArtists = new ArrayList<>();

    static {
        adultArtists.add(new ArtistData("The Lumineers", "http://img2-ak.lst.fm/i/u/300x300/09cdd71b38864beabfc8dbae72053fca.png"));
        adultArtists.add(new ArtistData("Mumford and Sons", "http://img2-ak.lst.fm/i/u/300x300/e125f7a6fe204e18ce018d5f714877ad.png"));
        adultArtists.add(new ArtistData("Of Monsters and Men", "http://img2-ak.lst.fm/i/u/300x300/ebad3734c46b4c0683adb303a18722fd.png"));
        adultArtists.add(new ArtistData("Arctic Monkeys", "http://img2-ak.lst.fm/i/u/300x300/645b5b818e834052b7f9dff0cbee8c8f.png"));
        adultArtists.add(new ArtistData("Vampire Weekend", "http://img2-ak.lst.fm/i/u/300x300/da34e4573b8c444889b148766f041f09.png"));
    }


    public static final ArrayList<ArtistData> middleAgeArtists = new ArrayList<>();

    static {
        middleAgeArtists.add(new ArtistData("Billy Joel", "http://img2-ak.lst.fm/i/u/300x300/45e004f5699949988d3cfbc0f36397de.png"));
        middleAgeArtists.add(new ArtistData("Eagles", "http://img2-ak.lst.fm/i/u/300x300/771464d8bbef45499c01fd817231012b.png"));
        middleAgeArtists.add(new ArtistData("Chicago", "http://img2-ak.lst.fm/i/u/300x300/3fa14058d8164d5eb944dd5430270c9b.png"));
        middleAgeArtists.add(new ArtistData("ABBA", "http://img2-ak.lst.fm/i/u/300x300/b507616a4ed5470e946d7f4406729f2d.png"));
        middleAgeArtists.add(new ArtistData("The Beatles", "http://img2-ak.lst.fm/i/u/300x300/c33a564f81fc478db125bf08914a4585.png"));
    }

    public static final ArrayList<ArtistData>[] kidList = new ArrayList[]{kidArtists, elderlyArtists, middleAgeArtists, teenArtists, adultArtists};
    public static final ArrayList<ArtistData>[] teenList = new ArrayList[]{teenArtists, adultArtists, middleAgeArtists, elderlyArtists, kidArtists};
    public static final ArrayList<ArtistData>[] adultList = new ArrayList[]{adultArtists, middleAgeArtists, teenArtists, kidArtists, elderlyArtists};
    public static final ArrayList<ArtistData>[] middleAgeList = new ArrayList[]{middleAgeArtists, elderlyArtists, adultArtists, kidArtists, teenArtists };
    public static final ArrayList<ArtistData>[] elderlyList = new ArrayList[]{elderlyArtists, middleAgeArtists, adultArtists, kidArtists, teenArtists};


    public static final HashMap<String, ArrayList<ArtistData>[]> allOrders = new HashMap<>();

    static{
        allOrders.put("child", kidList);
        allOrders.put("teen", teenList);
        allOrders.put("adult", adultList);
        allOrders.put("middleaged", middleAgeList);
        allOrders.put("elderly", elderlyList);
    }
}
