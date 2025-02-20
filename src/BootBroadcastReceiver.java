
public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the broadcast is for boot completion
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Start the SpeedMonitoringService when boot is complete
            Intent serviceIntent = new Intent(context, SpeedMonitoringService.class);
            context.startService(serviceIntent); // Start service after boot completion

            // Showing a Toast for debugging
            Toast.makeText(context, "SpeedMonitoringService Started after Boot", Toast.LENGTH_SHORT).show();
        }
    }
}
