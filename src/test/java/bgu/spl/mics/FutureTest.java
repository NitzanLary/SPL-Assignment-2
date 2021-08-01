package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;


import static org.junit.jupiter.api.Assertions.*;


public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp(){
        future = new Future<>();
    }


    @Test
    public void testGet() throws InterruptedException {
        String result = "done";
        future.resolve(result);
        assertEquals(result,future.get());
    }

    @Test
    public void testResolve() throws InterruptedException {
        String str = "someResult";
        future.resolve(str);
        assertTrue(str.equals(future.get()));
    }

    @Test
    public void testIsDone(){
        assertFalse(future.isDone());
        future.resolve("");
        assertTrue(future.isDone());
    }

    @Test
    public void testTimeGet(){
        assertNull(future.get(234000, TimeUnit.MICROSECONDS));
        assertFalse(future.isDone());
        String result = "done";
        future.resolve(result);
        assertEquals(future.get(234000,TimeUnit.MICROSECONDS), result);
    }



}
