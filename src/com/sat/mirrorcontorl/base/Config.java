package com.sat.mirrorcontorl.base;

/**
 * Created by Tianluhua on 2018/04/10
 */

public class Config {

	public static class MotionEventKey {

		public static final String JACTION = "action";
		public static final String JX = "x";
		public static final String JY = "y";

		public static final int ACTION_DOWN = 0;
		public static final int ACTION_MOVE = 2;
		public static final int ACTION_UP = 1;
	}

	public static class ActionKey {
		public static final String CLIENT_IP_KEY = "phoneip:";
		public static final String SERVICE_START_KEY = "start:";

	}

	public static class PortGlob {
		public static final int MULTIPORT = 9696;
		public static final int DATAPORT = 8686;
		public static final int TOUCHPORT = 8181;
		public static final int BACKPORT = 9191;

	}

	public static class HandlerGlod {
		public static final int CONNECT_SUCCESS = 0x0001;
		public static final int TOUCH_CONNECT_SUCCESS = 0x0002;
		public static final int DATA_CONNECT_SUCCESS = 0x0003;
		public static final int CONNECT_FAIL = 0x0004;
		public static final int CLEAR_FAILCOUNT = 0x0005;
		public static final int RESTART_SERVER = 0x0006;
		public static final int DATA_READ = 0x0007;
	}

}
