package com.example.doublei.Etc;

public class Singleton {
    private boolean switchValue;
    private int strabimusCount;

    public boolean getSwitchValue() {
        return switchValue;
    }
    public void setSwitchValue(boolean switchValue){
        this.switchValue = switchValue;
    }

    public int getStrabimusCount() {return strabimusCount;}
    public void setStrabimusCount(int strabimusCount){ this.strabimusCount = strabimusCount;}

    private static Singleton instance = null;

    public static synchronized Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}
