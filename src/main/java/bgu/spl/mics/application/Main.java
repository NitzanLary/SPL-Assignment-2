package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {

	public static Input parseJson(String arg){
		Gson gson = new Gson();
		try (Reader reader = new FileReader(arg)) {
			return gson.fromJson(reader, Input.class);
		}catch (Exception e){}
		return null;
	}

	public static void toOutput(String arg){
		Diary diary = Diary.getInstance();
		Gson gson = new Gson();
		try (Writer writer = new FileWriter(arg)) {
			gson.toJson(diary,writer);
		}catch (Exception e){}
	}

	public static void initProgram(Input input){
		// initializing Ewoks from json input
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.recruitEwok(input.getEwoks());

		// creating LinkedList of all running threads in current test for joining purposes.
		LinkedList<Thread> joinArr = new LinkedList<>();
		Thread c3po = new Thread(new C3POMicroservice());
		c3po.start();
		joinArr.add(c3po);

		Thread hansolo = new Thread(new HanSoloMicroservice());
		hansolo.start();
		joinArr.add(hansolo);

		Thread r2d2 = new Thread(new R2D2Microservice(input.getR2D2()));
		r2d2.start();
		joinArr.add(r2d2);

		Thread lando = new Thread(new LandoMicroservice(input.getLando()));
		lando.start();
		joinArr.add(lando);

		Thread leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		leia.start();
		joinArr.add(leia);


		for (Thread t : joinArr){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Input input = parseJson(args[0]);
		initProgram(input);
		toOutput(args[1]);
	}
}
