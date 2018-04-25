package com.sat.mirrorcontorl.core;

public abstract class BaseTransfer implements Transferable{

	/**
	 * ͷ���ָ��ַ�
	 */
	public static final String SPERATOR = "::";

	/**
	 * �ֽ����鳤��
	 */
	public static final int BYTE_SIZE_HEADER = 1024 * 10;
	public static final int BYTE_SIZE_SCREENSHOT = 1024 * 40;
	public static final int BYTE_SIZE_DATA = 1024 * 4;

	/**
	 * �����ļ�����
	 */
	public static final int TYPE_FILE = 1; // �ļ�����
	public static final int TYPE_MSG = 2; // ��Ϣ����

	/**
	 * �����ֽ�����
	 */
	public static final String UTF_8 = "UTF-8";

}
