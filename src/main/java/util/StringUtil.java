package util;


import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: wistbean
 * email：wistbean.bb@gmail.com
 * date: 2017/12/23
 * desc: String工具类
 */

public final class StringUtil {
	private static final Logger logger = LoggerFactory.getLogger(StringUtil.class);
	
	/* 随机字符串生成的方式（由字母和数字组成） */
	public static final int RANDOM_TYPE_NORMAL = 1;
	/* 随机字符串生成的方式（全部由数字组成） */
	public static final int RANDOM_TYPE_ALNUM = 2;
	/* 随机字符串生成的方式（全部由字母组成） */
	public static final int RANDOM_TYPE_ALPHA = 3;
	
	private StringUtil() {}
	
	/**
	 * 检查字符串是否为 null 或者空字符串
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str));
	}
	
	/**
	 * 检查字符串是否全部由数字组成
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (isEmpty(str)) {
			return false;
		}
		return str.matches("^\\d+$");
	}
	
	/**
	 * 检测字符串是否正确的浮点数格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDecimal(String str) {
		if (isEmpty(str)) {
			return false;
		}
		
		if (isNumeric(str)) {
			return true;
		}
		
		return str.matches("^\\d+\\.\\d+$");
	}
	
	/**
	 * 检测字符串是否正确的电子邮箱格式
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmail(String str) {
		if (isEmpty(str)) {
			return false;
		}
		
		return str.matches("^(?:[\\w\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`\\{\\|\\}\\~]+\\.)*[\\w\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\`\\{\\|\\}\\~]+@(?:(?:(?:[a-zA-Z0-9_](?:[a-zA-Z0-9_\\-](?!\\.)){0,61}[a-zA-Z0-9_-]?\\.)+[a-zA-Z0-9_](?:[a-zA-Z0-9_\\-](?!$)){0,61}[a-zA-Z0-9_]?)|(?:\\[(?:(?:[01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\.){3}(?:[01]?\\d{1,2}|2[0-4]\\d|25[0-5])\\]))$");
	}
	
	/**
	 * 检测字符串是否正确的日期格式（yyyy-MM-dd）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str) {
		if (isEmpty(str)) {
			return false;
		}
		
		if (!str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
			return false;
		}
		
		String[] temp1 = str.split("-");
		
		int[] temp2 = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		
		int year = Integer.valueOf(temp1[0]);
		
		int month = Integer.valueOf(temp1[1]);
		
		int day = Integer.valueOf(temp1[2]);
		
		if (year < 1900 || month < 1 || month > 12 || day < 1 || day > 31) {
			return false;
		}
		
		if (day > temp2[month]) {
			return false;
		}
		
		boolean isLeapYear = ((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0);
		
		if (month == 2 && !isLeapYear && day > 28) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 检测字符串是否正确的日期时间格式（yyyy-MM-dd HH:mm:ss）
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isDateTime(String str) {
		if (isEmpty(str)) {
			return false;
		}
		
		if (!str.matches("^\\d{4}-\\d{1,2}-\\d{1,2}[\\x20\\t]+\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
			return false;
		}
		
		String[] temp1 = str.split("[\\x20\\t]+");
		
		if (!isDate(temp1[0])) {
			return false;
		}
		
		String[] temp2 = temp1[1].split(":");
		
		int hh = Integer.valueOf(temp2[0]);
		
		int mm = Integer.valueOf(temp2[1]);
		
		int ss = Integer.valueOf(temp2[2]);
		
		if (hh > 23) {
			return false;
		}
		
		if (mm > 59) {
			return false;
		}
		
		if (ss > 59) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 转换成 Byte 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Byte parseByte(String str) {
		return Byte.valueOf(str);
	}
	
	/**
	 * 转换成 Short 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Short parseShort(String str) {
		return Short.valueOf(str);
	}
	
	/**
	 * 转换成 Integer 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Integer parseInteger(String str) {
		return Integer.valueOf(str);
	}
	
	/**
	 * 转换成 Long 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Long parseLong(String str) {
		return Long.valueOf(str);
	}
	
	/**
	 * 转换成 Float 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Float parseFloat(String str) {
		return Float.valueOf(str);
	}
	
	/**
	 * 转换成 Double 类型
	 * 
	 * @param str
	 * @return
	 */
	public static Double parseDouble(String str) {
		return Double.valueOf(str);
	}
	
	/**
	 * 去除开头的空格或 tab
	 * 
	 * @param str
	 * @return
	 */
	public static String ltrim(String str) {
		return ltrim(str, "[\\x20\\t]*");
	}
	
	/**
	 * 去除开头的指定的字符串
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String ltrim(String str, String regex) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(regex)) {
			return str;
		}
		
		return str.replaceAll("^" + regex, "");
	}
	
	/**
	 * 去除结尾的空格或 tab
	 * 
	 * @param str
	 * @return
	 */
	public static String rtrim(String str) {
		return rtrim(str, "[\\x20\\t]*");
	}
	
	/**
	 * 去除结尾的指定的字符串
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String rtrim(String str, String regex) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(regex)) {
			return str;
		}
		
		return str.replaceAll(regex + "$", "");
	}
	
	/**
	 * 去除首尾的空格或 tab
	 * 
	 * @param str
	 * @return
	 */
	public static String trim(String str) {
		return trim(str, "[\\x20\\t]*");
	}
	
	/**
	 * 去除首尾的指定的字符串
	 * 
	 * @param str
	 * @param regex
	 * @return
	 */
	public static String trim(String str, String regex) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(regex)) {
			return str;
		}
		
		str = ltrim(str, regex);
		str = rtrim(str, regex);
		
		return str;
	}
	
	/**
	 * 转换成 int
	 * 
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		try {
			return (new BigDecimal(str)).intValue();
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 转换成 int
	 *
	 * @param str
	 * @return
	 */
	public static long toLong(String str) {
		try {
			return (new BigDecimal(str)).longValue();
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * 转换成 float
	 * 
	 * @param str
	 * @return
	 */
	public static float toFloat(String str) {
		try {
			return (new BigDecimal(str)).floatValue();
		} catch (Exception e) {
			return 0f;
		}
	}
	
	/**
	 * 将字符串转换成 Date 对象
	 * 
	 * @param str
	 * @return
	 */
	public static Date parseDate(String str) {
		return parseDate(str, "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 将字符串转换成 Date 对象
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static Date parseDate(String str, String pattern) {
		if (isEmpty(str) || isEmpty(pattern)) {
			return null;
		}
		
		SimpleDateFormat df = new SimpleDateFormat(pattern);
		
		try {
			return df.parse(str);
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return null;
		}
	}
	
	/**
	 * MD5 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String md5(String str) {
		if (isEmpty(str)) {
			return "";
		}
		
		MessageDigest messageDigest;
		byte[] bytes;
		
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
			
			bytes = messageDigest.digest();
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return "";
		}
		
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < bytes.length; i++) {
			if (Integer.toHexString(0xFF & bytes[i]).length() == 1) {
				sb.append("0").append(Integer.toHexString(0xFF & bytes[i]));
			}
			else {
				sb.append(Integer.toHexString(0xFF & bytes[i]));
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * MD5 加密（加上干扰码）
	 * 
	 * @param str
	 * @param salt
	 * @return
	 */
	public static String md5(String str, String salt) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(salt)) {
			return md5(md5(str));
		}
		else {
			return md5(md5(str) + salt);
		}
	}
	
	/**
	 * 生成随机字符串
	 * 
	 * @param len
	 * @return
	 */
	public static String getRandomString(int len) {
		return getRandomString(len, RANDOM_TYPE_NORMAL);
	}
	
	/**
	 * 按指定的方式生成随机字符串
	 * 
	 * @param len
	 * @param randomType
	 * @return
	 */
	public static String getRandomString(int len, int randomType) {
		if (len < 1) {
			return "";
		}
		
		String str;
		
		if (randomType == RANDOM_TYPE_ALNUM) {
			str = "3456789987654334567899876543";
		}
		else if (randomType == RANDOM_TYPE_ALPHA) {
			str = "abcdefghjkmnpqrstuvwxyYXWVUTSRQPNMKJHGFEDCBAyxwvutsrqpnmkjhgfedcbaABCDEFGHJKMNPQRSTUVWXY";
		}
		else {
			str = "yxwvutsrqpnmkjhgfedcba3456789ABCDEFGHJKMNPQRSTUVWXY99876543ABCDEFGHJKMNPQRSTUVWXYabcdefghjkmnpqrstuvwxy";
		}
		
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < len; i++) {
			int index = rnd.nextInt(str.length());
			sb.append(str.charAt(index));
		}
		
		return sb.toString();
	}
	
	/**
	 * 从文件中读取
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFromFile(String filePath) {
		return readFromFile(filePath, null);
	}
	
	/**
	 * 从文件中读取
	 * 
	 * @param filePath
	 * @param encoding
	 * @return
	 */
	public static String readFromFile(String filePath, String encoding) {
		if (isEmpty(filePath)) {
			return "";
		}
		
		return readFromFile(new File(filePath), encoding);
	}
	
	/**
	 * 从文件中读取
	 * 
	 * @param file
	 * @return
	 */
	public static String readFromFile(File file) {
		return readFromFile(file, null);
	}
	
	/**
	 * 从文件中读取
	 * 
	 * @param file
	 * @param encoding
	 * @return
	 */
	public static String readFromFile(File file, String encoding) {
		try {
			return readFromInputStream(new FileInputStream(file), encoding);
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return "";
		}
	}
	
	/**
	 * 从字节数组中读取
	 * 
	 * @param bytes
	 * @return
	 */
	public static String readFromByteArray(byte[] bytes) {
		return readFromByteArray(bytes, null);
	}
	
	/**
	 * 从字节数组中读取
	 * 
	 * @param bytes
	 * @param encoding
	 * @return
	 */
	public static String readFromByteArray(byte[] bytes, String encoding) {
		if (bytes == null || bytes.length == 0) {
			return "";
		}
		
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		
		return readFromInputStream(in, encoding);
	}
	
	/**
	 * 从 InputStream 读取
	 * 
	 * @param in
	 * @return
	 */
	public static String readFromInputStream(InputStream in) {
		return readFromInputStream(in, null);
	}
	
	/**
	 * 从 InputStream 读取
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 */
	public static String readFromInputStream(InputStream in, String encoding) {
		BufferedReader reader = null;
		
		try {
			StringBuffer sb = new StringBuffer();
			
			char[] buf = new char[1024];
			
			int count = 0;
			
			if (isEmpty(encoding) || !Charset.isSupported(encoding)) {
				reader = new BufferedReader(new InputStreamReader(in));
			}
			else {
				reader = new BufferedReader(new InputStreamReader(in, encoding));
			}
			
			while ((count = reader.read(buf)) > 0) {
				sb.append(buf, 0, count);
			}
			
			return sb.toString();
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return "";
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {
				//code
			}
		}
	}
	
	/**
	 * 写入到文件
	 * 
	 * @param filePath
	 * @param str
	 * @return
	 */
	public static boolean writeToFile(String filePath, String str) {
		if (isEmpty(filePath)) {
			return false;
		}
		
		return writeToFile(new File(filePath), str);
	}
	
	/**
	 * 写入到文件
	 * 
	 * @param file
	 * @param str
	 * @return
	 */
	public static boolean writeToFile(File file, String str) {
		if (file == null) {
			return false;
		}
		
		if (str == null) {
			str = "";
		}
		
		BufferedWriter writer = null;
		
		try {
			writer = new BufferedWriter(new FileWriter(file));
			
			writer.write(str);
			
			return true;
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				//code
			}
		}
	}
	
	/**
	 * 追加写入文件
	 * 
	 * @param filePath
	 * @param str
	 * @return
	 */
	public static boolean appendToFile(String filePath, String str) {
		if (isEmpty(filePath) || isEmpty(str)) {
			return false;
		}
		
		return appendToFile(new File(filePath), str);
	}
	
	/**
	 * 追加写入到文件
	 * 
	 * @param file
	 * @param str
	 * @return
	 */
	public static boolean appendToFile(File file, String str) {
		if (file == null || isEmpty(str)) {
			return false;
		}
		
		RandomAccessFile raf = null;
		
		try {
			if (!file.isFile() && !file.createNewFile()) {
				return false;
			}
			
			raf = new RandomAccessFile(file, "rw");
			raf.seek(file.length());
			raf.writeBytes(str);
			
			return true;
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return false;
		} finally {
			try {
				raf.close();
			} catch (Exception ex) {
				//code
			}
		}
	}
	
	/**
	 * 格式化显示浮点数
	 * 
	 * @param number
	 * @param format
	 * @return
	 */
	public static String numberFormat(String number, String format) {
		return numberFormat(Double.valueOf(number), format);
	}
	
	/**
	 * 格式化显示浮点数
	 * 
	 * @param number
	 * @param format
	 * @return
	 */
	public static String numberFormat(Double number, String format) {
		if (number == null || isEmpty(format)) {
			return "";
		}
		
		DecimalFormat df = new DecimalFormat(format);
		
		return df.format(number);
	}
	
	/**
	 * 从 classpath 路径读取
	 * 
	 * @param pathname
	 * @return
	 */
	public static String readFromClassPathResource(String pathname) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new InputStreamReader(StringUtil.class.getClassLoader().getResourceAsStream(pathname), "utf-8"));
			
			StringBuffer sb = new StringBuffer();
			
			char[] cbuf = new char[1024];
			
			int len = 0;
			
			while ((len = reader.read(cbuf)) > 0) {
				sb.append(cbuf, 0, len);
			}
			
			return sb.toString();
		} catch (Exception e) {
			ExceptionUtil.logStackTrace(logger, e);
			return "";
		} finally {
			try {
				reader.close();
			} catch (Exception ex) {}
		}
	}
	
	/**
	 * 检查是否匹配正则表达式
	 * 
	 * @param input
	 * @param regex
	 * @return
	 */
	public static boolean isMatch(String input, String regex) {
		return isMatch(input, regex, false);
	}
	
	/**
	 * 检查是否匹配正则表达式
	 * 
	 * @param input
	 * @param regex
	 * @param ignoreCase
	 * @return
	 */
	public static boolean isMatch(String input, String regex, boolean ignoreCase) {
		if (isEmpty(input)) {
			return false;
		}
		
		if (isEmpty(regex)) {
			return true;
		}
		
		Pattern pattern;
		
		if (ignoreCase) {
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}
		else {
			pattern = Pattern.compile(regex);
		}
		
		return pattern.matcher(input).matches();
	}
	
	/**
	 * 正则表达式分组捕获
	 * 
	 * @param input
	 * @param regex
	 * @param ignoreCase
	 * @return
	 */
	public static List<String[]> getMatches(String input, String regex, boolean ignoreCase) {
		List<String[]> matches = new ArrayList<String[]>();
		
		if (isEmpty(input) || isEmpty(regex)) {
			return matches;
		}
		
		Pattern pattern;
		
		if (ignoreCase) {
			pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		}
		else {
			pattern = Pattern.compile(regex);
		}
		
		Matcher matcher = pattern.matcher(input);
		
		while (matcher.find()) {
			int count = matcher.groupCount() + 1;
			
			String[] groups = new String[count];
			
			for (int i = 0; i < count; i++) {
				groups[i] = matcher.group(i);
			}
			
			matches.add(groups);
		}
		
		return matches;
	}
	
	/**
	 * 删除 mysql 特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String removeMySqlSpecialChars(String str) {
		if (isEmpty(str)) {
			return "";
		}
		
		str = str.replace("'", "");
		str = str.replace("\\", "/");
		
		return str;
	}


	/**
	 * 字符串转化为数组
	 * @param content
	 * @param separator
     * @return
     */
	public static String[] Str2Array(String content,String separator) {
		if (isEmpty(separator)) {
			return null;
		}

		return content.split(separator);

	}

	
	/**
	 * 检查是否国内的手机号码
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static boolean isNationalMobileNumber(String mobileNumber) {
		if (isEmpty(mobileNumber)) {
			return false;
		}
		
		if (!isMatch(mobileNumber, "^[1-9][0-9]+$")) {
			return false;
		}
		
		if (mobileNumber.length() < 11) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * 手机号码加掩码
	 * 
	 * @param mobileNumber
	 * @return
	 */
	public static String maskMobileNumber(String mobileNumber) {
		if (isEmpty(mobileNumber)) {
			return "";
		}
		
		mobileNumber = trim(mobileNumber);
		char[] numbers = mobileNumber.toCharArray();
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < numbers.length; i++) {
			if (i < 3) {
				sb.append(numbers[i]);
				continue;
			}

			if (i <= (numbers.length - 5)) {
				sb.append("*");
				continue;
			}

			sb.append(numbers[i]);
		}
		
		return sb.toString();
	}
	
	public static String maskEmailAddress(String emailAddress) {
		if (isEmpty(emailAddress)) {
			return "";
		}
		
		if (!isEmail(emailAddress)) {
			return emailAddress;
		}
		
		int n1 = emailAddress.indexOf('@');
		
		String p1 = emailAddress.substring(0, n1);
		String p2 = emailAddress.substring(n1);
		char[] a1 = p1.toCharArray();
		StringBuilder sb = new StringBuilder();
		
		if (a1.length <= 3) {
			for (int i = 1; i <= a1.length; i++) {
				sb.append("*");
			}
		}
		else {
			for (int i = 0; i < a1.length; i++) {
				if (i < 3) {
					sb.append(a1[i]);
				}
				else {
					sb.append("*");
				}
			}
		}
		
		sb.append(p2);
		
		return sb.toString();
	}
	
	public static String fractionalFormat(String str) {
		return fractionalFormat(str, 2);
	}
	
	public static String fractionalFormat(String str, int len) {
		NumberFormat nf = NumberFormat.getInstance();
		
		nf.setMinimumFractionDigits(len);
		nf.setGroupingUsed(false);
		
		return nf.format((new BigDecimal(str)).floatValue());
	}
	
	public static String urlencode(String str) {
		return urlencode(str, "utf-8");
	}
	
	public static String urlencode(String str, String enc) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(enc)) {
			enc = "utf-8";
		}
		
		try {
			return URLEncoder.encode(str, enc);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static String urldecode(String str) {
		return urldecode(str, "utf-8");
	}
	
	public static String urldecode(String str, String enc) {
		if (isEmpty(str)) {
			return "";
		}
		
		if (isEmpty(enc)) {
			enc = "utf-8";
		}
		
		try {
			return URLDecoder.decode(str, enc);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}