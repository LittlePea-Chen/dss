package nuist.qlib.dss.net;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import net.sf.json.JSONObject;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.vo.IPMessageVO;
import nuist.qlib.dss.util.ToolUtil;
import android.util.Log;

/**
 * 
 * 接收IP的线程
 * 
 */
public class ReceIP implements Runnable // 接收
{
	private static final String TAG = "ReceIP";
	private MulticastSocket dsock; // 广播套接字、
	private String host;
	private File path;
	public static Boolean Flag = true;

	public ReceIP(MulticastSocket dsock, File path) {
		this.dsock = dsock;
		this.host = "239.0.0.1";
		this.path = path;
	}

	public void run() {
		try {
			InetAddress ip = InetAddress.getByName(this.host);
			dsock.joinGroup(ip); // 加入到广播组
			while (Flag) {
				byte[] data = new byte[256];

				DatagramPacket packet = new DatagramPacket(data, data.length);
				dsock.receive(packet);

				String message = new String(packet.getData(), 0,
						packet.getLength());
				JSONObject jsonObject = JSONObject.fromObject(message);
				IPMessageVO ipMessageVO = (IPMessageVO) JSONObject.toBean(
						jsonObject, IPMessageVO.class);

				RoleType roleType = ipMessageVO.getRoleType();
				if (roleType == null) {
					continue;
				}
				if (RoleType.EDITOR == roleType) {// 接收记录员的IP
					ToolUtil.createDBConfig(path, ipMessageVO.getOriginalIp());
				}

				// 将接受的消息存入address.properties文件
				NetPropertiesUtil.saveIPAddress(ipMessageVO, path);
				Log.i(TAG, message);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("接受失败!");
		} finally {
			dsock.close();
		}
	}
}