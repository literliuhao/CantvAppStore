package cn.can.tvlib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Locale;

public class NetworkUtils {

	/**
	 * 
	 * 检查是否有网络
	 */
	public static boolean isNetworkConnected(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	// 判断wifi状态
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 是否在使用wap或.net上网
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public static String getWifiIp(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
				(ipAddress >> 24 & 0xff));
		return ip;
	}

	/**
	 * 获取链接类型
	 * 
	 * @param context
	 * @return
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	public interface OnNetCheckListener {

		void onChecked(Boolean result);

	}

	/**
	 * 检测网络是否真正连接
	 * 
	 * @return
	 */
	public static void ping(final Context ct, final OnNetCheckListener listener) {

		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {

				boolean result = isNetworkConnected(ct);
				if (result == false) {
					return false;
				} else {
					try {
						String ip = "m.biyao.com";
						int timeout = 60000;
						String host = InetAddress.getByName(ip).getHostName();
						return InetAddress.getByName(host).isReachable(timeout);
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
			}

			protected void onPostExecute(Boolean result) {
				listener.onChecked(result);
			}

		}.execute();
	}

	/**
	 * 获取当前系统连接网络的网卡的mac地址
	 * 
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getMac() {
		byte[] mac = null;
		StringBuffer sb = new StringBuffer();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();

				while (address.hasMoreElements()) {
					InetAddress ip = address.nextElement();
					if (ip.isAnyLocalAddress() || !(ip instanceof Inet4Address) || ip.isLoopbackAddress())
						continue;
					if (ip.isSiteLocalAddress())
						mac = ni.getHardwareAddress();
					else if (!ip.isLinkLocalAddress()) {
						mac = ni.getHardwareAddress();
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}

		if (mac != null) {
			for (int i = 0; i < mac.length; i++) {
				sb.append(parseByte(mac[i]));
			}
			return sb.substring(0, sb.length() - 1);
		}
		return null;
	}
	
	private static String parseByte(byte b) {
		String s = "00" + Integer.toHexString(b) + ":";
		return s.substring(s.length() - 3);
	}
	
	public static String getEthernetMac() {
		// return "C8:0E:77:30:77:62";
		String macAddr = null;
		macAddr = _getLocalEthernetMacAddress();
		if (macAddr == null) {
			macAddr = getMac();
		}
		if (TextUtils.isEmpty(macAddr)) {
			macAddr = _getEthMacAddress2();
			if (macAddr != null && macAddr.startsWith("0:")) {
				macAddr = "0" + macAddr;
			}
		}
		return macAddr;
	}
	
	private static String _getLocalEthernetMacAddress() {
		String mac = null;
		try {
			Enumeration<NetworkInterface> localEnumeration = NetworkInterface.getNetworkInterfaces();

			while (localEnumeration.hasMoreElements()) {
				NetworkInterface localNetworkInterface = localEnumeration.nextElement();
				String interfaceName = localNetworkInterface.getDisplayName();

				if (interfaceName == null) {
					continue;
				}

				if (interfaceName.equals("eth0")) {
					mac = _convertToMac(localNetworkInterface.getHardwareAddress());
					if (mac != null && mac.startsWith("0:")) {
						mac = "0" + mac;
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return mac;
	}

	private static String _convertToMac(byte[] mac) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
			byte b = mac[i];
			int value = 0;
			if (b >= 0 && b < 16) {// Jerry(2013-11-6): if (b>=0&&b<=16) => if
									// (b>=0&&b<16)
				value = b;
				sb.append("0" + Integer.toHexString(value));
			} else if (b >= 16) {// Jerry(2013-11-6): else if (b>16) => else if
									// (b>=16)
				value = b;
				sb.append(Integer.toHexString(value));
			} else {
				value = 256 + b;
				sb.append(Integer.toHexString(value));
			}
			if (i != mac.length - 1) {
				sb.append(":");
			}
		}
		return sb.toString();
	}
	
	private static String _getEthMacAddress2() {
		String mac = _loadFileAsString("/sys/class/net/eth0/address");
		if (mac == null) {
			mac = "";
		} else {
			mac = mac.toUpperCase(Locale.CHINA);
			if (mac.length() > 17) {
				mac = mac.substring(0, 17);
			}
		}

		return mac;
	}
	
	private static String _loadFileAsString(String filePath) {
		try {
			if (new File(filePath).exists()) {
				StringBuffer fileData = new StringBuffer(1000);
				BufferedReader reader = new BufferedReader(new FileReader(filePath));
				char[] buf = new char[1024];
				int numRead = 0;
				while ((numRead = reader.read(buf)) != -1) {
					String readData = String.valueOf(buf, 0, numRead);
					fileData.append(readData);
				}
				reader.close();
				return fileData.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
