package nuist.qlib.dss.net.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP处理类
 * 
 * @author feige
 *
 */
public class IPUtil {

	/**
	 * 获取本地ip地址
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @since DSS 1.0
	 */
	public static String getLocalAddress() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		if (localHost == null) {
			return null;
		} else {
			return localHost.getHostAddress();
		}
	}

}
