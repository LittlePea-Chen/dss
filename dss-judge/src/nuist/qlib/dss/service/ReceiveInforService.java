package nuist.qlib.dss.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import nuist.qlib.dss.constant.MessageType;
import nuist.qlib.dss.constant.RoleType;
import nuist.qlib.dss.net.vo.AllScoreMessageVO;
import nuist.qlib.dss.net.vo.BaseMessageVO;
import nuist.qlib.dss.net.vo.CommandMessageVO;
import nuist.qlib.dss.net.vo.MatchInfoMessageVO;
import nuist.qlib.dss.net.vo.ScoreMessageVO;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

public class ReceiveInforService extends Service {

	private static final String TAG = "ReceiveInforService";
	private static final String REGEX = "\\d+";
	public static String units_name;
	public static String category_name;
	private ServerSocket serverSocket;
	public static Boolean Flag = true;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		try {
			// Log.i(TAG, "serverSocket创建---！");
			this.serverSocket = new ServerSocket(6666);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread() {
			public void run() {
				receiveInfor();
			};
		}.start();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Flag = false;
		super.onDestroy();
	}

	private void receiveInfor() {
		try {
			while (Flag) {
				Socket socket = serverSocket.accept(); // 监听
				// 建立输入流
				InputStream is = socket.getInputStream();
				byte[] by = new byte[1024];
				// 将输入流里的字节读到字节数组里，并返回读的字节数
				int length = is.read(by);
				// 将字节数组里的length个字节转换为字符串
				if (length == -1) {
					continue;
				}

				// 解析接收的信息
				String message = new String(by, 0, length, "utf-8");
				JSONObject jsonObject = JSONObject.fromObject(message);
				BaseMessageVO baseMessageVO = (BaseMessageVO) JSONObject
						.toBean(jsonObject, BaseMessageVO.class);

				// 按不同场景生成intent
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				intent.setAction("android.intent.action.MY_RECEIVER");
				MessageType messageType = baseMessageVO.getMessageType();
				// 打分信息
				if (MessageType.SCORE == messageType) {
					forScoreIntent(jsonObject, intent);
				} else if (MessageType.COMMAND == messageType) {
					forCommandIntent(jsonObject, intent);
				} else if (MessageType.MATCHINFO == messageType) {
					forMatchInfoIntent(jsonObject, intent);
				} else if (MessageType.ALLSOCRE == messageType) {
					forAllScoreInfoIntent(jsonObject, intent);
				}

				sendBroadcast(intent);
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} // 监听端口号
	}

	private void forScoreIntent(JSONObject jsonObject, Intent intent) {
		ScoreMessageVO scoreMessageVO = (ScoreMessageVO) JSONObject.toBean(
				jsonObject, ScoreMessageVO.class);

		// 角色类型
		RoleType roleType = scoreMessageVO.getRoleType();
		if (roleType == null) {
			return;
		}
		// 角色编号
		int roleNum = 0;
		Pattern pattern = Pattern.compile(REGEX);
		Matcher matcher = pattern.matcher(roleType.getKeyWord());
		while (matcher.find()) {
			roleNum = Integer.parseInt(matcher.group());
		}

		intent.putExtra("num", roleNum);
		intent.putExtra("score", scoreMessageVO.getScore());
		if (roleType.isArtJudge()) { // 接收艺术裁判打分
			intent.putExtra("item", RoleType.ARTJUDGE01.getKeyWord());
		} else if (roleType.isExecJudge()) { // 接收完成裁判打分
			intent.putExtra("item", RoleType.EXECJUDGE01.getKeyWord());
		} else if (roleType.isExecJudge()) { // 接收舞蹈裁判打分
			intent.putExtra("item", RoleType.IMPJUDGE01.getKeyWord());
		}
	}

	private void forCommandIntent(JSONObject jsonObject, Intent intent) {
		CommandMessageVO commandMessageVO = (CommandMessageVO) JSONObject
				.toBean(jsonObject, CommandMessageVO.class);

		intent.putExtra("item", MessageType.COMMAND.getKeyWord());
		intent.putExtra("CommandContent", commandMessageVO.getCommandType());
	}

	private void forMatchInfoIntent(JSONObject jsonObject, Intent intent) {
		MatchInfoMessageVO matchInfoMessageVO = (MatchInfoMessageVO) JSONObject
				.toBean(jsonObject, MatchInfoMessageVO.class);

		intent.putExtra("item", MessageType.MATCHINFO.getKeyWord());
		intent.putExtra("categoryName", matchInfoMessageVO.getMatchCategory());
		intent.putExtra("matchName", matchInfoMessageVO.getMatchName());
		intent.putExtra("matchUnit", matchInfoMessageVO.getMatchUnit());
	}
	
	private void forAllScoreInfoIntent(JSONObject jsonObject, Intent intent) {
		AllScoreMessageVO allScoreMessageVO = (AllScoreMessageVO) JSONObject.toBean(
				jsonObject, AllScoreMessageVO.class);
		
		intent.putExtra("item", "all");
		intent.putExtra(RoleType.ARTJUDGE01.getKeyWord(), allScoreMessageVO.getArtScore01());
		intent.putExtra(RoleType.ARTJUDGE02.getKeyWord(), allScoreMessageVO.getArtScore02());
		intent.putExtra(RoleType.ARTJUDGE03.getKeyWord(), allScoreMessageVO.getArtScore03());
		intent.putExtra(RoleType.ARTJUDGE04.getKeyWord(), allScoreMessageVO.getArtScore04());
		intent.putExtra("artTotalScore", allScoreMessageVO.getArtTotalScore());
		intent.putExtra(RoleType.EXECJUDGE01.getKeyWord(), allScoreMessageVO.getExecScore01());
		intent.putExtra(RoleType.EXECJUDGE02.getKeyWord(), allScoreMessageVO.getExecScore02());
		intent.putExtra(RoleType.EXECJUDGE03.getKeyWord(), allScoreMessageVO.getExecScore03());
		intent.putExtra(RoleType.EXECJUDGE04.getKeyWord(), allScoreMessageVO.getExecScore04());
		intent.putExtra("execTotalScore", allScoreMessageVO.getExecTotalScore());
		intent.putExtra(RoleType.IMPJUDGE01.getKeyWord(), allScoreMessageVO.getImpScore01());
		intent.putExtra(RoleType.IMPJUDGE02.getKeyWord(), allScoreMessageVO.getImpScore02());
		intent.putExtra("deductionSubScore", allScoreMessageVO.getSubScore());
		intent.putExtra("totalscore", allScoreMessageVO.getTotalScore());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
