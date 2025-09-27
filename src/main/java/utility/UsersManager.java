package utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * Manages a pool of user identifiers in a thread-safe manner.
 * <p>
 * This class is designed for concurrent environments where multiple threads
 * need to acquire and release user IDs for isolated operations (e.g., parallel test execution).
 * <br>It ensures that each thread gets a unique user and waits if none are available.
 * </p>
 * <p>
 * Internally, it uses a {@link ConcurrentLinkedQueue} to store available users,
 * and synchronization to manage blocking behavior and thread safety.
 * </p>
 *
 *  <br>@author Hossam Atef
 *  <br>
 *  <br>@version 1.0
 */
public class UsersManager {

    private final Queue<String> availableUsers = new ConcurrentLinkedQueue<>();
    private boolean initialized = false;
    private static final Logger log = LogManager.getLogger(lookup().lookupClass());

    /**
     * Initializes the user pool with the given array of user identifiers.
     * <br>This method is synchronized to prevent multiple initializations in concurrent scenarios.
     *
     * @param users an array of user IDs to populate the queue
     */
    public synchronized void initialize(String[] users) {
        if (!initialized) {
            Collections.addAll(availableUsers, users);
            log.info("A Queue is initialized with '{}' users.", String.join(", ", users));
            initialized = true;
        }
    }

    public synchronized boolean printAvailableUsers() {
        System.out.println("Available users: " + availableUsers);
        return false;
    }

    /**
     * Acquires a user from the pool.
     * <p>
     * If no users are available, the calling thread will block until one is released.
     * <br>This method is synchronized to ensure safe access to the shared queue and proper use of {@code wait()}.
     * </p>
     *
     * @return a user ID from the pool
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized String acquireUser() throws InterruptedException {

        while (availableUsers.isEmpty()) {
            log.info("waiting a user to be released");
            wait(); // Wait until a user is released
        }
        return availableUsers.poll();
    }

    /**
     * Releases a user back into the pool and notifies any waiting threads.
     * <br>This method is synchronized to ensure safe modification of the queue and proper use of {@code notifyAll()}.
     *
     * @param user the user ID to release back into the pool
     */
    public synchronized void releaseUser(String user) {
        availableUsers.offer(user);
        log.info("User '{}' is released.", user);
        notifyAll(); // Notify waiting threads for a released user
    }


}
