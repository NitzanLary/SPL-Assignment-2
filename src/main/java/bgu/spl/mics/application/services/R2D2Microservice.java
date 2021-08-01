package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

/**
 * R2D2Microservices is in charge of the handling {@link DeactivationEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link DeactivationEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends SoldierMicroService {
    private long DeactivationTime;

    public R2D2Microservice(long duration) {
        super("R2D2");
        DeactivationTime = duration;
    }

    @Override
    protected void initialize() {
        super.initialize();
        subscribeEvent(DeactivationEvent.class, (m)->{
            try{
                Thread.sleep(DeactivationTime);
            }catch(InterruptedException e){}
            diary.setR2D2Deactivate(System.currentTimeMillis());
            complete(m,true);
        });
    }

    protected void updateDiary() {
        diary.setR2D2Terminate(System.currentTimeMillis());
    }

}
