/*
 * Copyright (c) 2015, NUIST - 120Lib. All rights reserved.
 */

package nuist.qlib.dss.constant;

import lombok.Getter;
import lombok.Setter;

public enum MessageType {

	IP("IP信息", "IP"),

	MATCHINFO("比赛信息", "matchInfo"),

	SCORE("单项得分", "score"),

	ALLSOCRE("整体得分", "allScore"),

	COMMAND("调分指令", "command");

	MessageType(String name, String keyWord) {
		this.name = name;
		this.keyWord = keyWord;
	}

	@Setter
	@Getter
	private String name;

	@Setter
	@Getter
	private String keyWord;

	/**
	 * 根据名称返回MessageType
	 * 
	 * @param name
	 * @return
	 * @since DSS 1.0
	 */
	public static MessageType praseName(String name) {
		for (MessageType type : MessageType.values()) {
			if (name != null && name.equalsIgnoreCase(type.getName())) {
				return type;
			}
		}
		return null;
	}

	/**
	 * 根据关键字返回MessageType
	 * 
	 * @param name
	 * @return
	 * @since DSS 1.0
	 */
	public static MessageType praseKeyword(String keyword) {
		for (MessageType type : MessageType.values()) {
			if (keyword != null && keyword.equalsIgnoreCase(type.getKeyWord())) {
				return type;
			}
		}
		return null;
	}
}
