
public class SpeedMonitoringService extends Service {

    private Renter renter;

    @Override
    public void onCreate() {
        super.onCreate();
		
        renter = new Renter("R12345", 100.0); // Default value will be updated from Firebase
        fetchSpeedLimitFromFirebase(renter.renterId);

        // Start monitoring speed
        monitorSpeed();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This will be called when the service is started
        return START_STICKY; // Keep the service running in the background
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop monitoring when service is destroyed
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We are not binding the service to any component
    }

    // Fetch speed limit from Firebase
    private void fetchSpeedLimitFromFirebase(String renterId) {
		renter.maxSpeedLimit = //Call Firebase API;
        System.out.println("Speed limit fetched from Firebase: " + renter.maxSpeedLimit);
    }

    // Simulate monitoring speed
    private void monitorSpeed() {
        new Thread(() -> {
            while (true) {
                double currentSpeed = getCurrentCarSpeed();
                if (currentSpeed > renter.maxSpeedLimit) {
                    sendSpeedAlert(currentSpeed);
                }

                // Sleep for 5 seconds before checking speed again
                SystemClock.sleep(5000);
            }
        }).start();
    }

    // Simulate getting current speed (replace with actual speed data from AAOS)
    private double getCurrentCarSpeed() {
        return Math.random() * 150; // Simulate speed between 0 and 150 km/h
    }

    // Send a notification when the speed exceeds the limit
    private void sendSpeedAlert(double currentSpeed) {
        String messageToRenter = "Warning: Your speed is " + currentSpeed + " km/h, exceeding your limit of " + renter.maxSpeedLimit + " km/h.";
        sendUserNotificationToRenter(messageToRenter);

        String messageToRentalCompany = "Alert: Renter " + renter.renterId + " exceeded the speed limit of " + renter.maxSpeedLimit + " km/h. Current speed: " + currentSpeed + " km/h.";
        sendUserNotificationToRentalCompany(messageToRentalCompany);
    }

    // Send notification to the User
    private void sendUserNotificationToRenter(String message) {
        System.out.println("Sending notification to renter: " + message);
		//Using Notification Manager API send to notification to user.
    }

    // Send notification to the rental company using Firebase Cloud Messaging (FCM)
    private void sendUserNotificationToRentalCompany(String message) {
        System.out.println("Sending notification to rental company: " + message);
        // Call the Firebase API to send the notification to rental company
    }
}
