package nuist.qlib.dss.net.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.IPMessageVO;

import org.apache.commons.lang3.StringUtils;

public class NetPropertiesUtil {

	private static final String NET_PROPERTIES_FILE = "/net.properties";
	private static final String IP_ADDRESS_FILE = "/address.properties";

	/**
	 * 获取广播地址（IP和端口号）
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static InetSocketAddress getInetSocketAddress(File rootPath)
			throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(rootPath + NET_PROPERTIES_FILE);
		pros.load(in);
		String ip = pros.getProperty("IPBroadcaster.ip");
		String port = pros.getProperty("IPBroadcaster.port");
		return new InetSocketAddress(ip, Integer.parseInt(port));
	}

	/**
	 * 获取广播地址（端口号）
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static InetSocketAddress getInetSocketPort(File rootPath)
			throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(rootPath + NET_PROPERTIES_FILE);
		pros.load(in);
		String port = pros.getProperty("IPBroadcaster.port");
		return new InetSocketAddress(Integer.parseInt(port));
	}

	/**
	 * 保存角色和对应的IP
	 * 
	 * @param msg
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static void saveIPAddress(IPMessageVO msg, File rootPath)
			throws IOException {
		RoleType role = msg.getRoleType();
		String ip = msg.getOriginalIp();
		if (role == null || StringUtils.isBlank(ip)) {
			return;
		} else {
			Properties pros = new Properties();
			InputStream in = NetPropertiesUtil.class
					.getResourceAsStream(rootPath + IP_ADDRESS_FILE);
			pros.load(in);
			in.close();

			pros.setProperty(role.getKeyWord(), ip);
			OutputStream out = new FileOutputStream(rootPath + IP_ADDRESS_FILE);
			pros.store(out, "address");
			out.close();
		}
	}

	/**
	 * 获取记录员高级裁判组的ip
	 * 
	 * @return
	 * @since DSS 1.0
	 */
	public static List<String> getEditorAndCheifReceiver(File rootPath) {
		return null;
	}

	/**
	 * 删除连接不上或未知的IP信息
	 * 
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static void removeIPAddress(String ip, File rootPath)
			throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(rootPath + IP_ADDRESS_FILE);
		pros.load(in);
		in.close();

		for (Entry entry : pros.entrySet()) {
			if (ip.equals(entry.getValue())) {
				pros.remove(entry);
			}
		}

		OutputStream out = new FileOutputStream(rootPath + IP_ADDRESS_FILE);
		pros.store(out, "address");
		out.close();
	}

	/**
	 * 清空所有ip
	 * 
	 * @throws IOException
	 * 
	 * @since DSS 1.0
	 */
	public static void clearAll(File rootPath) throws IOException {
		Properties pros = new Properties();
		InputStream in = NetPropertiesUtil.class
				.getResourceAsStream(rootPath + IP_ADDRESS_FILE);
		pros.load(in);
		in.close();

		pros.clear();
	}

}
