package util;


public class MConfig {
	
	public static final String SERVER_ADDRESS = "http://192.168.1.121:8080/Demo/androidlogin.action";
	public static final String UPLOAD_ADDRESS = "http://192.168.1.121:8080/Demo/androiddata.action";
	
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";

	public static final int PACKAGE = 0;
	public static final int XueYang = 1;
	public static final int XinDian = 2;
	public static final int XueTang = 3;
	public static final int TiWen = 4;
	public static final int FenChen = 5;
	public static final int NaoDian = 6;
	public static final int XueYa = 7;
	public static final int MaiBo = 8;
	public static final int GuanZhuanZhiShu = 9;

	private static final String[] dataType = {"血氧", "心电", "血糖", "体温", "空气质量",
			"脑电(待定)", "血压（待定)", "测试"};
}
