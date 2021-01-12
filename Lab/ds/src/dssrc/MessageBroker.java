// Bindhu Shree Hadya Ravi - 1001699836

package dssrc;
import java.util.Queue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

//MESSAGE BROKER IS A SINGLETON CLASS
public class MessageBroker {
	private static HashMap<String, ConcurrentLinkedQueue<String>> inputQueue = new HashMap<String, ConcurrentLinkedQueue<String>>();
	private static HashMap<String, ConcurrentLinkedQueue<String>> outputQueue = new HashMap<String, ConcurrentLinkedQueue<String>>();
	private static MessageBroker m_me;

	private ConversionSet setA, setB, setC;

	private MessageBroker() {
		inputQueue.put("a", new ConcurrentLinkedQueue<String>());
		inputQueue.put("b", new ConcurrentLinkedQueue<String>());
		inputQueue.put("c", new ConcurrentLinkedQueue<String>());

		outputQueue.put("a", new ConcurrentLinkedQueue<String>());
		outputQueue.put("b", new ConcurrentLinkedQueue<String>());
		outputQueue.put("c", new ConcurrentLinkedQueue<String>());
		// RETRIEVES THE CONVERSIONS FROM TEXTFILE FOR QUEUE A
		setA = new ConversionSet("A.txt");
		// RETRIEVES THE CONVERSIONS FROM A TEXTFILE FOR QUEUE B
		setB = new ConversionSet("B.txt");
		// RETRIEVES THE CONVERSIONS FROM A TEXTFILE FOR QUEUE C
		setC = new ConversionSet("C.txt");

		
		monitorInputQueue();
	}

	public static MessageBroker getInstance() {
		if (m_me == null) {
			m_me = new MessageBroker();
		}
		return m_me;
	}
	// GIVEN THE QUEUE AND VALUE, THIS ROUTINE ADDS THE VALUE TO THE DESIGNATED QUEUE FOR CONVERSION
	public void addToInputQueue(String queue, String valToConvert) {
		if (queue.equalsIgnoreCase("a")) {
			Queue<String> q = inputQueue.get("a");
			q.add((valToConvert));
			
			
		} else if (queue.equalsIgnoreCase("b")) {
			Queue<String> q = inputQueue.get("b");
			q.add((valToConvert));
			
		} else if (queue.equalsIgnoreCase("c")) {
			Queue<String> q = inputQueue.get("c");
			q.add((valToConvert));
			
		}
		

	}
	// MONITORS THE INPUT QUEUE FOR INPUTS FROM THE CLIENT
	public void monitorInputQueue() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				while (true) {

					if (!inputQueue.get("a").isEmpty()) {
						String srcValue = inputQueue.get("a").remove();
						setA.compute(srcValue);
						System.out.println(setA.peekResults());
					}
					if (!inputQueue.get("b").isEmpty()) {
						String srcValue = inputQueue.get("b").remove();
						setB.compute(srcValue);
						System.out.println(setB.peekResults());
					}
					if (!inputQueue.get("c").isEmpty()) {
						String srcValue = inputQueue.get("c").remove();
						setA.compute(srcValue);
						System.out.println(setC.peekResults());
					}
					
					Thread.yield();

				}
			}

		}).start();
	}
	//	CHECKS IF THE RESULTS ARE READY
	public boolean areResultsAvaialble(String queueName) {
		switch (queueName.toLowerCase()) {
		case "a":
			return setA.getResultCount() != 0;
		case "b":
			return setB.getResultCount() != 0;
		case "c":
			return setC.getResultCount() != 0;
		default:
			return false;
		}

	}
	// RETURNS THE RESULTS
	public String getResults(String queueName) {

		switch (queueName.toLowerCase()) {
		case "a":
			return setA.getResults();
		case "b":
			return setB.getResults();
		case "c":
			return setC.getResults();
		default:
			return "";
		}
	}

}


