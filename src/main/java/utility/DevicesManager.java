package utility;

import datadriven.ConfigLoader;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * DevicesManager manages a pool of mobile devices and associated Appium services for parallel test execution.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Maintains a thread-safe queue of available devices.</li>
 *   <li>Provides synchronized acquisition and release of devices.</li>
 *   <li>Starts and stops Appium services for each device using a provided configuration.</li>
 *   <li>Logs device and service activity using Log4j.</li>
 * </ul>
 *
 * <p>Usage Example:
 * <pre>{@code
 * DevicesManager deviceManager = new DevicesManager();
 * deviceManager.initialize(new Map[]{device1, device2});
 * Map<String, Object> device = deviceManager.acquireDevice();
 * deviceManager.releaseDevice(device);
 * deviceManager.startAllServices(configLoader);
 * deviceManager.stopAllServices();
 * }</pre>
 *
 * @author Hossam Atef
 * @version 1.0
 */
public class DevicesManager {

    private final Queue<Map<String, Object>> availableDevices = new ConcurrentLinkedQueue<>();
    private boolean initialized = false;
    private static final Logger log = LogManager.getLogger(lookup().lookupClass());
    private final Map<String, AppiumDriverLocalService> runningServices = new ConcurrentHashMap<>();

    /**
     * Initializes the pool of available devices with the given list.
     * <p>
     * Each device is a {@code Map<String, Object>} with properties like
     * {@code deviceName}, {@code uuid}, and {@code serverPort}.
     * <br>Initialization happens only once; calling again has no effect.
     *
     * @param devices the list of devices to add to the pool
     * @throws NullPointerException if {@code devices} is null
     */
    public synchronized void initialize(List<Map<String, Object>> devices) {
        if (devices == null) {
            throw new NullPointerException("The devices list cannot be null.");
        }
        if (!initialized) {
            availableDevices.addAll(devices);
            log.info("Devices pool initialized with '{}' devices '{}'.", devices.size(), devices);
            initialized = true;
        }
    }


    /**
     * Acquires a device from the pool in a thread-safe manner.
     * Waits if no device is currently available.
     *
     * @return a map representing the acquired device properties
     * @throws InterruptedException if the thread is interrupted while waiting for a device
     */
    public synchronized Map<String, Object> acquireDevice() throws InterruptedException {
        while (availableDevices.isEmpty()) {
            log.info("No free devices, waiting...");
            wait();
        }
        Map<String, Object> device = availableDevices.poll();
        log.info("Acquired device: {}", device);
        return device;
    }

    /**
     * Releases a previously acquired device back into the pool.
     *
     * @param device a map representing the device to be released
     */
    public synchronized void releaseDevice(Map<String, Object> device) {
        availableDevices.offer(device);
        log.info("Released device: {}", device);
        notifyAll();
    }

    /**
     * Logs all currently available devices in the pool.
     */
    public synchronized void printAvailableDevices() {
        log.info("Available devices: {}", availableDevices);
    }

    /**
     * Starts an Appium server for each device in the available devices pool.
     *
     * @param config the configuration loader providing Appium server path and IP address
     * @throws Exception if the Appium service fails to start
     */
    public void startAllServices(ConfigLoader config) throws Exception {
        printAvailableDevices();
        for (Map<String, Object> device : availableDevices) {
            AppiumDriverLocalService service = new AppiumServiceBuilder()
                    .withAppiumJS(new File(config.getValue("appiumServerPath")))
                    .withIPAddress(config.getValue("appiumServerIPAddress"))
                    .usingPort(((Double) device.get("serverPort")).intValue())
                    .build();
            service.start();
            runningServices.put(device.get("deviceName").toString(), service);
            log.info("Starting an Appium server at '{}' for device '{}'.", service.getUrl(), device.get("deviceName"));
        }
    }

    /**
     * Stops all running Appium services managed by this DevicesManager.
     */
    public void stopAllServices() {
        for (AppiumDriverLocalService service : runningServices.values()) {
            if (service.isRunning()) {
                service.stop();
                log.info("Service '{}' is stopped successfully.", service.getUrl());
            }
        }
    }
}
