package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends SoldierMicroService {
    private long BombingTime;

    public LandoMicroservice(long duration) {
        super("Lando");
        BombingTime = duration;
    }

    @Override
    protected void initialize() {
       super.initialize();
       subscribeEvent(BombDestroyerEvent.class,(m)->{
           try{
               Thread.sleep(BombingTime);
           } catch(InterruptedException e){}
           complete(m,true);
       });
    }

    protected void updateDiary(){
        diary.setLandoTerminate(System.currentTimeMillis());
    }
}
