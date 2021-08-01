package bgu.spl.mics;

import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.passiveObjects.*;

public abstract class SoldierMicroService extends MicroService {
    protected Diary diary;


    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public SoldierMicroService(String name) {
        super(name);
        diary = Diary.getInstance();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class, (m) -> terminate());
    }
}
