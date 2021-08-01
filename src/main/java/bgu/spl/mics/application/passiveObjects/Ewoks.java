package bgu.spl.mics.application.passiveObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {
    private ArrayList<Ewok> ewoks;

    private static class EwoksHolder{
        private static Ewoks instance = new Ewoks();
    }

    private Ewoks(){
        ewoks = new ArrayList<>();
    }

    public static Ewoks getInstance(){return EwoksHolder.instance;}

    private void clear() {
        ewoks.clear();
    }

    public void recruitEwok(int serials){
        clear();
        for (int i =1; i<=serials; i++){
            ewoks.add(new Ewok(i));
        }
    }

    public void acquire(List<Integer> serials) {
        Collections.sort(serials);
        for (int i : serials) {
            ewoks.get(i-1).acquire();
        }
    }

    public void release(List<Integer> serials){
        for (int i : serials){
            ewoks.get(i-1).release();
        }
    }
}
