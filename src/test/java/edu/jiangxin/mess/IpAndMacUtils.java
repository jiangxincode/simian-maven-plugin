package edu.jiangxin.mess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class IpAndMacUtils {

	/**
	 * 获取本机所有IP
	 */
	public static String[] getAllLocalHostIP() {
		List<String> res = new ArrayList<String>();
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = netInterfaces.nextElement();
				System.out.println("---Name---:" + networkInterface.getName());
				Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
				while (inetAddress.hasMoreElements()) {
					InetAddress ip = (InetAddress) inetAddress.nextElement();
					if (ip.getHostAddress().indexOf(":") == -1) {
						res.add(ip.getHostAddress());
						System.out.println("本机的ip=" + ip.getHostAddress());
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return (String[]) res.toArray(new String[0]);
	}

	public static String getLocalIP() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> networkInterface = NetworkInterface.getNetworkInterfaces();
			while (networkInterface.hasMoreElements()) {
				NetworkInterface ni = networkInterface.nextElement();
				System.out.println("getLocalIP--nic.getDisplayName ():" + ni.getDisplayName());
				System.out.println("getLocalIP--nic.getName ():" + ni.getName());
				if (!ni.getName().equals("eth0")) {
					continue;
				} else {
					Enumeration<InetAddress> inetAddress = ni.getInetAddresses();
					while (inetAddress.hasMoreElements()) {
						InetAddress ia = inetAddress.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}

	public static String getWinLocalIP() {
		String ip = "";
		try {
			Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				System.out.println("getWinLocalIP--nic.getDisplayName ():" + ni.getDisplayName());
				System.out.println("getWinLocalIP--nic.getName ():" + ni.getName());
				Enumeration<?> e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					ip = ia.getHostAddress();
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}

	/**
	 * 获取本机所有物理地址
	 *
	 * @return
	 */
	public static String getMacAddress() {
		String mac = "";
		String line = "";

		String os = System.getProperty("os.name");

		if (os != null && os.startsWith("Windows")) {
			try {
				String command = "cmd.exe /c ipconfig /all";
				/*
				 * System.out.println(System.getProperties()); String[] envp = {
				 * "user.language=en, user.country=US"}; Process p =
				 * Runtime.getRuntime().exec(command,envp);
				 */
				Process p = Runtime.getRuntime().exec(command);

				BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = br.readLine()) != null) {
					if (line.indexOf("Physical Address") > 0) {
						int index = line.indexOf(":") + 2;

						mac = line.substring(index);

						break;
					}
				}

				br.close();

			} catch (IOException e) {
			}
		}

		return mac;
	}

	public static String getMacAddress(String host) {
		String mac = "";
		StringBuffer sb = new StringBuffer();

		try {
			NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName(host));

			byte[] macs = ni.getHardwareAddress();

			for (int i = 0; i < macs.length; i++) {
				mac = Integer.toHexString(macs[i] & 0xFF);

				if (mac.length() == 1) {
					mac = '0' + mac;
				}

				sb.append(mac + "-");
			}

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		mac = sb.toString();
		mac = mac.substring(0, mac.length() - 1);

		return mac;
	}

	/**
	 * 通过HttpServletRequest返回IP地址.
	 *
	 * @param request
	 *            HttpServletRequest
	 * @return ip String
	 * @throws Exception
	 */
	public static String getIpAddr(HttpServletRequest request) throws Exception {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * @param args
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws SocketException, UnknownHostException {
		getAllLocalHostIP();
		System.out.println(getLocalIP());
		System.out.println(getWinLocalIP());
		System.out.println(getMacAddress());
		System.out.println(getMacAddress("192.168.56.1"));
	}
}