package com.weite_welt.jakob.privacytestapp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.os.Vibrator;
import android.privacy.IPrivacyManager;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class MainActivity extends Activity {

	private static final String TAG = "Privacy";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout all = new LinearLayout(this);
		all.setOrientation(LinearLayout.VERTICAL);
		setContentView(all);

		final TextView status;
		{
			status = new TextView(this);
			all.addView(status);
		}

		LinearLayout main = new LinearLayout(this);
		{
			ScrollView sv = new ScrollView(this);
			main.setOrientation(LinearLayout.VERTICAL);
			sv.addView(main);
			all.addView(sv);
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Accounts");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						AccountManager am = (AccountManager) MainActivity.this.getSystemService(Context.ACCOUNT_SERVICE);
						Account[] accounts = am.getAccounts();
						if (accounts != null && accounts.length > 0) {
							String s = accounts[0].toString();
							status.setText(s);
							// try getPassword
							String pwd = am.getPassword(accounts[0]);
							status.setText(s + "pwd=" + pwd);
						}
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Contacts");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						String[] projection = new String[] { Contacts.DISPLAY_NAME };
						Cursor cursor = managedQuery(Contacts.CONTENT_URI, projection, null, null, null);
						int col = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
						cursor.moveToFirst();
						String string = cursor.getString(col);

						status.setText(string);
							
						cursor.close();
							
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Location");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						LocationManager lm = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
						Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						status.setText(loc.toString());
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Phone ID");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						TelephonyManager tm = (TelephonyManager) MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
						status.setText("DeviceID=" + tm.getDeviceId());
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Vibrate");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						android.os.Vibrator vb = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
						vb.vibrate(500);
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Test WiFi");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {

						WifiManager wf = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
						WifiInfo ci = wf.getConnectionInfo();
						status.setText("MAC=" + ci.getMacAddress());
					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Test Inet");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {

						Builder b = new StrictMode.ThreadPolicy.Builder();
						b.permitAll();
						StrictMode.setThreadPolicy(b.build());

						Socket s = new Socket("127.0.0.1", 1);
						status.setText("socket opened");
						s.close();

					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("ID");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {

						Process p = Runtime.getRuntime().exec("id");
						p.getOutputStream().close();
						InputStream fin = p.getInputStream();
						ByteArrayOutputStream buf = new ByteArrayOutputStream();

						byte[] tmp = new byte[1024];
						for (int s; (s = fin.read(tmp, 0, tmp.length)) >= 0;) {
							buf.write(tmp, 0, s);
						}

						status.setText(new String(buf.toByteArray()));

					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Get GIDs");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {

						PackageManager pm = MainActivity.this.getPackageManager();
						int[] gids = pm.getPackageGids(MainActivity.this.getPackageName());
						if (gids == null) {
							status.setText("gids==null");
						} else {
							StringBuilder res = new StringBuilder();
							for (int i : gids) {
								if (res.length() > 0) {
									res.append(',');
								}
								res.append(Integer.toString(i));
							}
							status.setText(res);
						}

					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Get Permissions");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						String pname = MainActivity.this.getPackageName();
						PackageManager pm = MainActivity.this.getPackageManager();
						PackageInfo pi = pm.getPackageInfo(pname, PackageManager.GET_PERMISSIONS);
						StringBuilder res = new StringBuilder();
						for (String p : pi.requestedPermissions) {
							if (res.length() > 0) {
								res.append(',');
							}
							res.append(p);
							int perm = pm.checkPermission(p, pname);
							if (PackageManager.PERMISSION_GRANTED == perm) {
								res.append('+');
								if ( MainActivity.this.checkCallingOrSelfPermission(p) == PackageManager.PERMISSION_DENIED ) {
									res.append('-');
								}
							} else if (PackageManager.PERMISSION_DENIED == perm) {
								res.append('-');
							} else {
								res.append('?');
							}
						}
						status.setText(res);

					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}
				}
			});
		}

		{
			Button b = new Button(this);
			main.addView(b);
			b.setText("Test PM");
			b.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						if ( false ) {
							Class<?> sm = Class.forName("android.os.ServiceManager");
							Method gs = sm.getDeclaredMethod("getService", String.class);
							IBinder ipm_binder = (IBinder) gs.invoke(null, "PrivacyManager");
							IPrivacyManager ipm = IPrivacyManager.Stub.asInterface(ipm_binder);

							IBinder stub = ipm.getPrivacyStub("hello world");

							Log.e(TAG, "stub=" + stub);

							Toast.makeText(MainActivity.this, "stub=" + stub, Toast.LENGTH_LONG).show();
						} else {
							testDex(MainActivity.this, "/sdcard/PrivacyManager.apk");
						}

					} catch (Exception e) {
						Toast.makeText(MainActivity.this, e.getClass().getName() + ":" + e.getMessage(), Toast.LENGTH_LONG).show();
						Log.e(TAG, "error", e);
					}

				}
			});
		}

	}

	private static class MyDexClassLoader extends DexClassLoader {
		public MyDexClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
			super(dexPath, optimizedDirectory, libraryPath, parent);
		}

		@Override
		public URL findResource(String name) {
			return super.findResource(name);
		}
		
	}
	
	private static String testDex(Context ctx, String dexPath) throws Exception {
		File dex_file = new File(dexPath);
		if ( !dex_file.canRead() ) {
			throw new IllegalStateException("file missing");
		}
		String cache = ctx.getCacheDir().getAbsolutePath();
		MyDexClassLoader cl = new MyDexClassLoader(dexPath, cache, null, ClassLoader.getSystemClassLoader());
		URL res = cl.findResource("META-INF/MANIFEST.MF");
		Log.e(TAG, "res=" + res);
		InputStream manifest_stream = res.openStream();
		Manifest mani = new Manifest(manifest_stream);
		manifest_stream.close();
		
		Attributes main = mani.getMainAttributes();
		Log.e(TAG, "main=" + main + main.keySet());
		
		String spi_cls = mani.getMainAttributes().getValue("PrivacyManager-SPI");
		if ( spi_cls == null ) {
			throw new IllegalStateException("spi name missing in manifest");
		}
		Class<?> pmcls = cl.loadClass(spi_cls);
		Object obj = pmcls.newInstance();
		return obj.toString();
	}

}
