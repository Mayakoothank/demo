import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.util.Log;

public class SpeedMonitoringService extends Service {
    private Car car;
    private CarPropertyManager carPropertyManager;
    private Context context;
    private Renter renter;
    private static final String TAG = "SpeedMonitoringService";	

    @Override
    public void onCreate() {
        super.onCreate();
		
        renter = new Renter("R12345", 100.0); // Default value will be updated from Firebase
        fetchSpeedLimitFromFirebase(renter.renterId);
	context = this;
	car = Car.createCar(context);
        carPropertyManager = (CarPropertyManager) car.getCarManager(Car.PROPERTY_SERVICE);
        // Start monitoring speed
        registerSpeedListener();
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
	stopMonitoring();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // We are not binding the service to any component
    }

    // Fetch speed limit from Firebase
    private void fetchSpeedLimitFromFirebase(String renterId) {
		renter.maxSpeedLimit = //Call Firebase API;
        Log.d(TAG,"Speed limit fetched from Firebase: " + renter.maxSpeedLimit);
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
        Log.d(TAG,"Sending notification to renter: " + message);
		//Using Notification Manager API send to notification to user.
    }

    // Send notification to the rental company using Firebase Cloud Messaging (FCM)
    private void sendUserNotificationToRentalCompany(String message) {
        Log.d(TAG,"Sending notification to rental company: " + message);
        // Call the Firebase API to send the notification to rental company
    }
    // Generic function to fetch properties
    private int getPropertyValue(int propertyId) {
        if (carPropertyManager != null && carPropertyManager.isPropertyAvailable(propertyId)) {
            CarPropertyValue<Integer> propValue = carPropertyManager.getProperty(Integer.class, propertyId);
            return propValue.getValue();
        }
        return -1; // Return -1 if unavailable
    }

    // Listen for speed changes
    public void registerSpeedListener() {
        carPropertyManager.registerCallback(speedCallback, CarPropertyManager.VEHICLE_PROPERTY_IDS_SPEED, CarPropertyManager.SENSOR_RATE_ONCHANGE);
    }

    private final CarPropertyManager.CarPropertyEventCallback speedChangeListener = new CarPropertyManager.CarPropertyEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            int speed = (int) carPropertyValue.getValue();
            Log.d(TAG, "Speed Updated: " + speed + " km/h");

            // Alert if speed crosses 80 km/h
            if (speed > renter.maxSpeedLimit) {
                Log.w(TAG, "⚠ Speed limit exceeded! Sending notification...");
                // TODO: Send notification via Firebase or AWS
		sendSpeedAlert(currentSpeed);    
            }
        }

        @Override
        public void onErrorEvent(int propertyId, int areaId) {
            Log.e(TAG, "Error in Property: " + propertyId);
        }
    };

    public void stopMonitoring() {
        if (carPropertyManager != null) {
            carPropertyManager.unregisterCallback(speedChangeListener);
        }
        if (car != null) {
            car.disconnect();
        }
    }

}
