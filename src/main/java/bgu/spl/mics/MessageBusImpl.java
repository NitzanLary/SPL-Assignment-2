package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static class MessageHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	// MicrserviceQueues hold for every registered Microservice:
	// first   --which class it had been registered for (register method)--
	// second  --messages he got (subscribe method)--
	// third   --all Events he got--.
	private ConcurrentHashMap<MicroService, ObjectsTres<Queue<Class>,Queue<Message>, Queue<Event>>> MicroServicesQueues;
	private ConcurrentHashMap<Class, LinkedList<MicroService>> EventsHandles;
	private ConcurrentHashMap<Class, Set<MicroService>> BroadcastsHandles;
	private ConcurrentHashMap<Event,Future> Events_Futures;


	private MessageBusImpl(){
		//initialization code
		MicroServicesQueues = new ConcurrentHashMap<>();
		EventsHandles = new ConcurrentHashMap<>();
		BroadcastsHandles = new ConcurrentHashMap<>();
		Events_Futures = new ConcurrentHashMap<>();
	}

	public static MessageBusImpl getInstance(){
		return MessageHolder.instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		MicroServicesQueues.get(m).getFirst().add(type); // adding the Event's type to the first Q of the pair
		synchronized (type) {
			if (!EventsHandles.containsKey(type))
				EventsHandles.put(type, new LinkedList<>());
			EventsHandles.get(type).add(m);
			type.notifyAll();
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		MicroServicesQueues.get(m).getFirst().add(type); // adding the Broadcast's type to the first Q of the pair
		synchronized (type) {
			if (!BroadcastsHandles.containsKey(type)) {
				Set<MicroService> hashSet = ConcurrentHashMap.newKeySet();
				BroadcastsHandles.put(type, hashSet);
			}
			BroadcastsHandles.get(type).add(m);
			type.notifyAll();
		}
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Events_Futures.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b){
		Set<MicroService> micros = BroadcastsHandles.get(b.getClass());
		synchronized (b.getClass()){
			while (micros == null) {
				try {
					b.getClass().wait();
				} catch (InterruptedException e) {}
				micros = BroadcastsHandles.get(b.getClass());
			}
		}
		for (MicroService m : micros){
			synchronized (m){
				MicroServicesQueues.get(m).getSecond().add(b);
				m.notifyAll();
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e){
		MicroService m = null;
		synchronized (e.getClass()) {
			Queue<MicroService> LL = EventsHandles.get(e.getClass());
			while (LL == null){
				try {
					e.getClass().wait();
					LL = EventsHandles.get(e.getClass());
				} catch (InterruptedException ex) {}
			}
			m = LL.poll();
			LL.add(m);
		}
		synchronized (m) {
			MicroServicesQueues.get(m).getSecond().add(e);
			MicroServicesQueues.get(m).getThird().add(e);
			if (Events_Futures.containsKey(e)) //only happened through unregister
				return null;
			Future<T> future = new Future();
			Events_Futures.put(e, future);
			m.notifyAll();
			return future;
		}
	}

	@Override
	public void register(MicroService m) {
		ObjectsTres ot = new ObjectsTres(new LinkedList<Class>(), new LinkedList<Message>(), new LinkedList<Event>());
		MicroServicesQueues.put(m, ot);
	}

	@Override
	public void unregister(MicroService m) {
		if (!MicroServicesQueues.containsKey(m))
			return;
		ObjectsTres<Queue<Class>, Queue<Message>, Queue<Event>> cur = MicroServicesQueues.get(m);
		MicroServicesQueues.remove(m);
		for (Class c : cur.getFirst()){
			synchronized (c) {
				if (EventsHandles.containsKey(c)) {
					EventsHandles.get(c).remove(m); // O(n)
					if (EventsHandles.get(c).isEmpty())
						EventsHandles.remove(c);
				} else { // in BroadcastHandles
					BroadcastsHandles.get(c).remove(m);   // O(1)
					if (BroadcastsHandles.get(c).isEmpty())
						BroadcastsHandles.remove(c);
				}
			}
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Queue<Message> messages = MicroServicesQueues.get(m).getSecond();
		Message message = null;
		synchronized (m) {
			while (messages.isEmpty()) {
				m.wait();
			}
			message = MicroServicesQueues.get(m).getSecond().poll();
		}
		return message;
	}
}
