package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Ewok;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.ArrayList;
import java.util.List;

public class AttackEvent implements Event<Boolean> {
	private Attack attack;

	public AttackEvent(Attack attack){
	    this.attack = attack;
    }

    public Attack getAttack() {
        return attack;
    }

    public void attack(MicroService ms){
        List<Integer> ewoks = attack.getSerials();
        Ewoks ew = Ewoks.getInstance();
        ew.acquire(ewoks);
        try {
            Thread.sleep(attack.getDuration());
        }catch (InterruptedException e){}
        ew.release(ewoks);
    }
}
