package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;

/**
 * C3POMicroservices is in charge of the handling {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends AttackerMicroService {
	
    public C3POMicroservice() {
        super("C3PO");
    }

    protected void updateDiary(){
        diary.setC3POTerminate(System.currentTimeMillis());
    }

    public void increaseAttack(){
        super.increaseAttack();
        diary.setC3POFinish(System.currentTimeMillis());
    }
}
