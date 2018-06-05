package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatcherUtil {

	public static String match(String regex, String input){
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input);
		if(m.find()){
		    return m.group(1);
		}
		return null;
	}
	
}
