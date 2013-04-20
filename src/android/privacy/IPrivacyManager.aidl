package android.privacy;

/**
 * API for privacy 
 *
 * {@hide}
 */
interface IPrivacyManager {
  IBinder getPrivacyStub(String service);
}
