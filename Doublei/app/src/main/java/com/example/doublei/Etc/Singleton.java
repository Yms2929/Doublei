package com.example.doublei.Etc;

public class Singleton {
    private boolean switchValue;

    public boolean getSwitchValue() {
        return switchValue;
    }

    public void setSwitchValue(boolean switchValue){
        this.switchValue = switchValue;
    }

    private static Singleton instance = null;

    public static synchronized Singleton getInstance(){
        if(instance == null){
            instance = new Singleton();
        }
        return instance;
    }
}
