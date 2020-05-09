package edu.stanford.cs244b.projects.priorityqueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PriorityQueueApplication {

	public static void main(String[] args) {
		System.out.println("Instance Number: " + args[0]);
		SingletonInstances.INSTANCE_NUMBER.set(Integer.parseInt(args[0]));
		SingletonInstances.NUM_INSTANCES.set(Integer.parseInt(args[1]));
		SnapshotUtils.loadSnapshot(SingletonInstances.PRIORITY_QUEUE);
		SpringApplication.run(PriorityQueueApplication.class, args);
	}
}
