package com.players;

abstract class PlayerA {
    public String name;
    public int stats;

    protected PlayerA(String name, int stats) {
        this.name = name;
        this.stats = stats;
    }

    public String getNameA() {
        return name;
    }

    abstract int getStatsA();
    abstract String getSportA();

}

class NFLPlayerAbstract extends PlayerA {

    protected NFLPlayerAbstract(String name, int stats) {
        super(name, stats);
    }

    @Override
    int getStatsA() {
        return stats;
    }

    @Override
    String getSportA() {
        return "NFL";
    }
}

class NBAPlayerAbstract extends PlayerA {
    protected NBAPlayerAbstract(String name, int stats) {
        super(name, stats);
    }

    @Override
    int getStatsA() {
        return stats;
    }

    @Override
    String getSportA() {
        return "NBA";
    }
}
