package util;


import blade.kit.logging.Logger;

public final class ExceptionUtil {
	private ExceptionUtil() {}
	
	public static void logStackTrace(Logger logger, Exception e) {
		logger.error(getStackTrace(e));
	}
	
	public static String getStackTrace(Exception e) {
		String message = e.getMessage();
		StringBuilder sb = new StringBuilder();
		
		sb.append(e.getClass().getCanonicalName());
		sb.append(": ");
		
		if (!StringUtil.isEmpty(message)) {
			sb.append(message);
		}
		
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append("\n");
			sb.append("at ");
			sb.append(element.toString());
		}
		
		return sb.toString();
	}
}