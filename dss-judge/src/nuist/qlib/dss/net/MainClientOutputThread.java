package nuist.qlib.dss.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import nuist.qlib.dss.net.util.NetPropertiesUtil;
import nuist.qlib.dss.net.util.ThreadPoolUtil;
import nuist.qlib.dss.net.vo.ScoreMessageVO;

/**
 * 类名：MainClientOutputThread 功能：管理客户端信息发送
 */
public class MainClientOutputThread {
	
	/**
	 * 发送得分给记录员和高级裁判组
	 * 
	 * @param scoreMessageVO
	 * @param rootPath
	 * @return
	 * @since DSS 1.0
	 */
	public int sendScore(ScoreMessageVO scoreMessageVO, File rootPath) {
		try {
			List<String> list = new ArrayList<String>();
			list = NetPropertiesUtil.getEditorAndCheifReceiver(rootPath); // 获取IP
			List<FutureTask<Integer>> tasks = new ArrayList<FutureTask<Integer>>();
			int sum = 1;

			// 遍历获取的IP，并发送
			if (list.size() == 0) {// 未收到ip
				sum = 0;
			} else {
				ExecutorService executor = ThreadPoolUtil
						.getExecutorServiceInstance();
				for (int i = 0; i < list.size(); i++) {
					scoreMessageVO.setTargetIp(list.get(i));
					tasks.add((FutureTask<Integer>) executor
							.submit(new SendMessage(scoreMessageVO, rootPath)));
				}

				for (FutureTask<Integer> task : tasks)
					// 获取线程返回值
					sum *= task.get();
			}
			return sum;
		} catch (Exception e) {
			return -1;// 发送失败
		}
	}
}
