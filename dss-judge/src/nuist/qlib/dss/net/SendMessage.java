package nuist.qlib.dss.net;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;

import net.sf.json.JSONObject;
import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.vo.ScoreMessageVO;
import android.util.Log;

/**
 * 类名：SendMessage 功能：建立主客户端的输出线程类， 向服务器端传送信息
 */
public class SendMessage implements Callable<Integer> {

	// tcp/ip协议
	private static final String TAG = "SendMessage";
	private Socket socket;
	private File path;
	private String ip;
	private String s;

	public SendMessage(ScoreMessageVO scoreMessageVO, File path) {
		this.path = path;
		this.ip = scoreMessageVO.getTargetIp();
		JSONObject jsonObject = JSONObject.fromObject(scoreMessageVO);
		this.s = jsonObject == null ? null : jsonObject.toString();
	}

	@Override
	public Integer call() throws Exception {
		try {
			socket = new Socket(ip, 6666);
			OutputStream os = socket.getOutputStream();
			os.write(s.getBytes("utf-8"));
			os.flush();
			os.close();
			socket.close();
		} catch (UnknownHostException e) {
			try {
				NetPropertiesUtil.removeIPAddress(ip, path);
			} catch (IOException e1) {
				Log.e(TAG, e.getMessage());
				return -1;
			}
		} catch (ConnectException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
