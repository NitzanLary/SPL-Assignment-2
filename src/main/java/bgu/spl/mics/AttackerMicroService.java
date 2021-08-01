package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;

public abstract class AttackerMicroService extends SoldierMicroService {
    /**
     * @param name the micro-service name (used mainly for debugging purposes -
     *             does not have to be unique)
     */
    public AttackerMicroService(String name) {
        super(name);
    }

    @Override
    protected void initialize() {
        super.initialize();
        subscribeEvent(AttackEvent.class, (m) -> {
            m.attack(this);
            complete(m,true);
            increaseAttack();
        });
    }

    public void increaseAttack(){
        diary.increaseTotalAttacks();
    }
}
