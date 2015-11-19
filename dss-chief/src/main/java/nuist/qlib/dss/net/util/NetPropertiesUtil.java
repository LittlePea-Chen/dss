package nuist.qlib.dss.net.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.IPMessageVO;

import org.apache.commons.lang.StringUtils;

public class NetPropertiesUtil {

	private static final String NET_PROPERTIES_FILE = "conf/net.properties";
	private static final String IP_ADDRESS_FILE = "conf/address.properties";

	private static Properties addrPros = new Properties();

	/**
	 * 获取广播地址（IP和端口号）
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static InetSocketAddress getInetSocketAddress() throws IOException {
		Properties pros = loadNettProperties();
		String ip = pros.getProperty("IPBroadcaster.ip");
		String port = pros.getProperty("IPBroadcaster.port");
		return new InetSocketAddress(ip, Integer.parseInt(port));
	}

	/**
	 * 保存角色和对应的IP
	 * 
	 * @param msg
	 * @throws IOException
	 * @since DSS 1.0
	 */
	public static void saveIPAddress(IPMessageVO msg) throws IOException {
		RoleType role = msg.getRoleType();
		String ip = msg.getOriginalIp();
		if (role == null || StringUtils.isBlank(ip)) {
			return;
		} else {
			addrPros.setProperty(role.getKeyWord(), ip);
			storeProperties(addrPros);
		}
	}

	/**
	 * 获取接收队伍信息的ip
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getTeamReceiver() throws IOException {
		List<String> teamReceiver = new ArrayList<String>();
		for (Entry entry : addrPros.entrySet()) {
			if (RoleType.EDITOR.getKeyWord().equals(entry.getKey())) {
				continue;
			}
			teamReceiver.add((String) entry.getValue());
		}

		return teamReceiver;
	}

	/**
	 * 获取高级裁判组的ip
	 * 
	 * @return
	 * @since DSS 1.0
	 */
	public static List<String> getCheifReceiver() {
		return null;
	}

	/**
	 * 根据角色获取对应的ip
	 * 
	 * @param roleType
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static String getCommandReceiver(RoleType roleType)
			throws IOException {
		if (roleType == null) {
			return null;
		} else {
			String ip = null;
			for (Entry entry : addrPros.entrySet()) {
				if (roleType.getKeyWord().equals(entry.getKey())) {
					ip = (String) entry.getValue();
					break;
				}
			}
			return ip;
		}
	}

	/**
	 * 删除连接不上或未知的IP信息
	 * 
	 * @throws IOException
	 * @since DSS 1.0
	 */
	@SuppressWarnings("rawtypes")
	public static void removeIPAddress(String ip) throws IOException {
		for (Entry entry : addrPros.entrySet()) {
			if (ip.equals(entry.getValue())) {
				addrPros.remove(entry);
			}
		}

		storeProperties(addrPros);
	}

	/**
	 * 清空所有ip
	 * 
	 * @throws IOException
	 * 
	 * @since DSS 1.0
	 */
	public static void clearAll() throws IOException {
		addrPros.clear();
		storeProperties(addrPros);
	}

	/**
	 * 读取网络配置properties
	 * 
	 * @return
	 * @throws IOException
	 * @since DSS 1.0
	 */
	private static Properties loadNettProperties() throws IOException {
		InputStream in = NetPropertiesUtil.class.getResourceAsStream("/"
				+ NET_PROPERTIES_FILE);
		addrPros.load(in);
		return addrPros;
	}

	/**
	 * 向properties文件写入
	 * 
	 * @param pros
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @since DSS 1.0
	 */
	private static void storeProperties(Properties pros)
			throws FileNotFoundException, IOException {
		URL resource = NetPropertiesUtil.class.getClassLoader().getResource(
				IP_ADDRESS_FILE);
		OutputStream out = new FileOutputStream(resource.getFile());
		pros.store(out, "address");
		out.close();
	}

}
