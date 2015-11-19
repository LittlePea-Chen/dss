package nuist.qlib.dss.net;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import net.sf.json.JSONObject;
import nuist.qlib.dss.activity.LoginActivity;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.IPMessageVO;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * 广播自己的IP地址
 * 
 */
public class BroadcastIP implements Runnable // 发送
{
	private static final String TAG = "BroadcastIPService";
	private MulticastSocket dsock;
	private Handler ulHandler;
	private static int i = 0;
	private int port;
	private String host;
	public static Boolean Flag = true;

	// private InetAddress addr;

	public BroadcastIP(MulticastSocket dsock, Handler ulHandler) {
		this.dsock = dsock;
		this.ulHandler = ulHandler;
		this.port = 9999; // 广播时局域网中成员接收的端口号
		this.host = "239.0.0.1"; // 局域网广播地址
	}

	public void run() {

		try {
			while (Flag) {
				String ip = getLocalIpAddress(); // 获取当前wifi的I平地址
				if (ip != null && !ip.equals("null")) {
					// 设置IP消息
					IPMessageVO ipMessageVO = new IPMessageVO();
					ipMessageVO.setRoleType(RoleType
							.praseName(LoginActivity.role));
					ipMessageVO.setOriginalIp(ip);
					// 对象转json
					JSONObject jsonObject = JSONObject.fromObject(ipMessageVO);
					// json转字符串
					String message = jsonObject.toString();

					// 数据打包
					DatagramPacket dataPack = new DatagramPacket(
							message.getBytes(), message.length(),
							InetAddress.getByName(host), // 广播
							port // 目标端口
					);
					dsock.send(dataPack);
					Log.i(TAG, message);
					if (i != 0) {
						ulHandler.sendEmptyMessage(1);
					}
					i = 0;
				} else {
					Log.i(TAG, "网络未连接");
					if (i == 0) {
						ulHandler.sendEmptyMessage(0);
					}
					i++;
				}
				Thread.sleep(10 * 1000); // 睡眠30秒
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			throw new RuntimeException("发送失败!");
		} finally {
			dsock.close();
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& !inetAddress.isLinkLocalAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}

	public static int getI() {
		return i;
	}

	public static void setI(int i) {
		BroadcastIP.i = i;
	}
}