package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusTest {
    private Event<Integer> e;
    Broadcast b;
    MessageBusImpl mb;
    MicroService m1;
    MicroService m2;

    @BeforeEach
    void setUp(){
        e = new Event<Integer>(){};
        b = new Broadcast(){};
        mb = MessageBusImpl.getInstance();//mb = new MessageBusImpl();
        m1 = new MicroService("m1") { // in original tests didnt implement all method
            @Override
            protected void initialize() {
                mb.register(this);
            }

            @Override
            protected void updateDiary() {}
        };
        m2 = new MicroService("m2"){
            @Override
            protected void initialize() {
                mb.register(this);
                subscribeEvent(e.getClass(),(x)->{} );
                subscribeBroadcast(b.getClass(), (x) -> {});
            }

            @Override
            protected void updateDiary() {}
        };

        m1.initialize();
        m2.initialize();
    }

/** checked under awaitMessage() */
//    @Test
//    void subscribeEvent() {
//    }
    /** checked under sendBroadcast() */
//    @Test
//    void subscribeBroadcast() {
//    }

    @Test
    void testComplete(){
        Future<Integer> f = m1.sendEvent(e);
        m2.complete(e, 5);
        try {
            assertEquals(5, f.get());
            assertTrue(f.isDone());
        } catch (Exception e){}

    }

    /**
     * here we check: sendBroadcast(), subscribeBroadcast().
     * @throws InterruptedException
     */
    @Test
    void testSendBroadcast() throws InterruptedException { // maybe should envelope with try-catch
        m2.subscribeBroadcast(b.getClass(), (x) -> {});
        m1.sendBroadcast(b);
        Message b2 = mb.awaitMessage(m2);
        assertEquals(b,b2);
    }

/** checked under awaitMessage() */
//    @Test
//    void sendEvent() {
//    }
/** checked under awaitMessage() */
//    @Test
//    void register() {
//    }


    /**
     * Tests we check: register(), subscribeEvent(), awaitMessage(), sendEvent().
     * @throws InterruptedException
     */
    @Test
    void testAwaitMessage() throws InterruptedException {
        Event<Integer> et = new Event() {};
        m2.subscribeEvent(et.getClass(),(x)->{});
        Future <Integer> f = m1.sendEvent(et);
        Message e2 = mb.awaitMessage(m2);
        assertEquals(et, e2); // checks if the two events are equals
    }
}