package bgu.spl.mics.application.services;

import java.util.*;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Diary diary;
	private Queue<Future> futures;
	
    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		diary = Diary.getInstance();
        futures = new LinkedList<>();
    }

    private void attack(){
        for (Attack a : attacks){
            futures.add(sendEvent(new AttackEvent(a)));
        }
        while (!futures.isEmpty()){
            try{
                Future<Boolean> f = futures.poll();
                f.get(); // wait for future's resolve
            }catch (InterruptedException e){}
        }
    }

    private void deactivate(){
        Future<Boolean> f = sendEvent(new DeactivationEvent());
        try {
            f.get();
        }catch (InterruptedException e){}
    }


    private void bomb(){
        Future<Boolean> f = sendEvent(new BombDestroyerEvent());
        try {
            f.get();
        }catch (InterruptedException e){}
    }


    private void startAttack(){
        attack();
        deactivate();
        bomb();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TerminationBroadcast.class,(m)->terminate());
        startAttack();
        sendBroadcast(new TerminationBroadcast());
    }

    @Override
    protected void updateDiary() {
        diary.setLeaTerminate(System.currentTimeMillis());
    }
}
