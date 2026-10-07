package com.Fluxsens.CodesGeneration.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;



public class StringUtils
{

    /**
     * @param str1
     * @return
     */
    public static boolean isNumeric(String str1)
    {

        boolean flag = false;
        try
        {
            Double.parseDouble(str1); /* incorrect integer results Exception */
            flag = true;
        }
        catch (NumberFormatException e)
        {
            flag = false;
        }
        return flag;
    }

    /**
     * Filters out newline characters.
     * 
     * @param s the String to filter
     * @return the filtered String
     */
    public static String filterNewlines(String s)
    {

        if (s == null)
        {
            return null;
        }

        StringBuffer buf = new StringBuffer(s.length());

        // loop through every character and replace if necessary
        int length = s.length();
        for (int i = 0; i < length; i++)
        {
            switch (s.charAt(i))
            {
                case '\r':
                    break;

                default:
                    buf.append(s.charAt(i));
            }
        }

        return buf.toString();
    }

    /**
     * @param fullPath
     * @param extensionSeparator
     * @return
     */
    public static String getExtension(String fullPath, String extensionSeparator)
    {
        int dot = fullPath.lastIndexOf(extensionSeparator);
        return fullPath.substring(dot + 1);
    }

    /**
     * @param fullPath
     * @param pathSeparator
     * @param extensionSeparator
     * @return
     */
    public static String getFileName(String fullPath, String pathSeparator, String extensionSeparator)
    { // gets filename without extension
        int dot = fullPath.lastIndexOf(extensionSeparator);
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(sep + 1, dot);
    }

    /**
     * @param fullPath
     * @param pathSeparator
     * @return
     */
    public String getPath(String fullPath, String pathSeparator)
    {
        int sep = fullPath.lastIndexOf(pathSeparator);
        return fullPath.substring(0, sep);
    }

    /**
     * @param inputString
     * @return
     */
    public int getWordsCount(String inputString)
    {
        int count = 0;
        StringTokenizer stringTokenizer = new StringTokenizer(inputString, " ");
        count = stringTokenizer.countTokens();
        return count;
    }

    /**
     * @param csvString
     * @return
     */
    public static List<String> csvToList(String csvString)
    {
        return deliStringsToList(csvString, ",");
    }

    /**
     * @param commaSeperatedStrings
     * @return
     */
    public static List<String> deliStringsToList(String delimiterSeperatedStrings, String delimiter)
    {
        ArrayList<String> arraylist = new ArrayList<String>();
        StringTokenizer stringTokenizer = new StringTokenizer(delimiterSeperatedStrings, delimiter);
        while (stringTokenizer.hasMoreTokens())
        {
            String token = stringTokenizer.nextToken();
            arraylist.add(token);
        }
        return arraylist;
    }

    /**
     * @param list
     * @return
     */
    public static String listToCsv(ArrayList<String> list)
    {
        String commaSeperatedStrings = "";
        StringBuffer buffer = new StringBuffer();
        if (!list.isEmpty())
        {
            for (int i = 0; i < list.size(); i++)
            {
                buffer.append(list.get(i));
                buffer.append(",");
            }
            commaSeperatedStrings = buffer.toString().substring(0, buffer.toString().length() - 1);
        }
        return commaSeperatedStrings;
    }

    /**
     * @param list
     * @return
     */
    public static Object[] listToArray(ArrayList<Object> list)
    {
        if (list != null)
        {
            Object[] objects = new Object[list.size()];
            for (int i = 0; i < list.size(); i++)
            {
                Object obj = list.get(i);
                objects[i] = obj;
            }
            return objects;
        }
        return null;
    }

    /**
     * @param list
     * @param searchString
     * @return
     */
    public static boolean containsStringIngoreCase(ArrayList<String> list, String searchString)
    {
        for (String listValue : list)
        {
            if (listValue.trim().toLowerCase().equalsIgnoreCase(searchString.trim().toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @param objects
     * @return
     */

    public static List<Object> arrayToList(Object[] objects)
    {
        if (objects != null)
        {
            ArrayList<Object> list = new ArrayList<Object>();
            for (int i = 0; i < objects.length; i++)
            {
                Object obj = objects[i];
                list.add(obj);
            }

            return list;
        }
        return null;
    }

    public static List<String> arrayToList(String[] objects)
    {
        if (objects != null)
        {
            ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < objects.length; i++)
            {
                String obj = objects[i];
                list.add(obj);
            }

            return list;
        }
        return null;
    }

    /**
     * @param tokenizedString
     * @return
     */
    public static String filterDuplicates(String tokenizedString, String delimiter)
    {
        StringBuffer filteredString = new StringBuffer();
        String filteredStringValues = "";
        StringTokenizer stkr = new StringTokenizer(tokenizedString, delimiter);
        TreeMap<String, String> stringMap = new TreeMap<String, String>();
        while (stkr.hasMoreTokens())
        {
            String token = stkr.nextToken();
            if (token != null)
            {
                stringMap.put(token, token);
            }
        }
        for (String key : stringMap.keySet())
        {
            filteredString.append(key);
            filteredString.append(delimiter);
        }
        if (filteredString.toString().length() > 0)
        {
            filteredStringValues = filteredString.toString().substring(0, filteredString.toString().length());
        }
        return filteredStringValues;
    }

    /**
     * @param s
     * @return
     */
    public static String capitalizeFirstLetters(String s)
    {

        for (int i = 0; i < s.length(); i++)
        {

            if (i == 0)
            {
                // Capitalize the first letter of the string.
                s = String.format("%s%s", Character.toUpperCase(s.charAt(0)), s.substring(1));
            }

            // Is this character a non-letter or non-digit? If so
            // then this is probably a word boundary so let's capitalize
            // the next character in the sequence.
            if (!Character.isLetterOrDigit(s.charAt(i)))
            {
                if (i + 1 < s.length())
                {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1), Character.toUpperCase(s.charAt(i + 1)), s.substring(i + 2));
                }
            }

        }

        return s;

    }

    /**
     * @param s
     * @return
     */
    public static String capitalizeFirstLettersTokenizer(String s)
    {

        final StringTokenizer st = new StringTokenizer(s, " ", true);
        final StringBuilder sb = new StringBuilder();

        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            token = String.format("%s%s", Character.toUpperCase(token.charAt(0)), token.substring(1));
            sb.append(token);
        }

        return sb.toString();

    }

    /**
     * Checks the input str whether null or not
     * 
     * @param str
     * @return
     */
    public static String checkNull(String str)
    {
        return (str == null) ? "" : str;
    }

    /**
     * @param commaSeperatedUserTypes
     * @param token
     * @return
     */
    public static boolean containsToken(String commaSeperatedTokens, String token, String delimiter)
    {

        boolean tokenExists = false;
        if (commaSeperatedTokens != null && token != null)
        {
            StringTokenizer stkr = new StringTokenizer(commaSeperatedTokens, delimiter);
            while (stkr.hasMoreTokens())
            {
                if (token.equalsIgnoreCase(stkr.nextToken()))
                {
                    tokenExists = true;
                    break;
                }
            }
        }

        return tokenExists;
    }

    /**
     * @param list
     * @return
     */
    public static String listToString(List<String> list)
    {
        String listString = "";
        for (String string : list)
        {
            listString += string + " ";
        }
        return listString.trim();
    }

    /**
     * Validates the value against the given pattern (regular expression)
     * 
     * @param value The value to be validated against regular expression
     * @param pattern The pattern (regular expression)
     * @return boolean The boolean value
     */
    public static boolean isValidPattern(String value, String pattern)
    {
        Pattern ptrn = null;
        ptrn = Pattern.compile(pattern);
        Matcher matcher = ptrn.matcher(value);
        return matcher.matches();
    }

    /**
     * @param value
     * @return
     */
    public static String splitByPattern(String value)
    {
        Pattern ptrn = null;
        ptrn = Pattern.compile("[#][<][f][o][n][t][\\s][\\d]*[\\s][\"][\"][>]");
        String[] tokens = ptrn.split(value);
        StringBuffer strBuffer = new StringBuffer();
        for (int i = 0; i < tokens.length; i++)
        {
            strBuffer.append(tokens[i]);
        }
        return strBuffer.toString();
    }

    /**
     * @param inputString
     * @param delimiter
     * @return
     */
    public static String getFirstToken(String inputString, String delimiter)
    {
        String[] tokens = inputString.split(delimiter);
        return (tokens.length > 0) ? tokens[0] : "";
    }

    public static Long parseLong(String val)
    {
        if (val != null && !"".equals(val))
        {
            return Long.parseLong(val);
        }
        return new Long(0);
    }

    public static double parseDouble(String val)
    {
        if (val != null && !"".equals(val))
        {
            return Double.parseDouble(val);
        }
        return 0.0;
    }

    public static int parseInt(String val)
    {
        if (val != null && !"".equals(val))
        {
            return Integer.parseInt(val);
        }
        return 0;
    }

    /**
     * @param val
     * @return
     */
    public static String removeSpaces(String val)
    {
        return (val == null) ? "" : removeChar(removeChar(val, " "), ".");
    }

    /**
     * @param val
     * @param character
     * @return
     */
    public static String removeChar(String val, String character)
    {
        StringTokenizer tokenizer = new StringTokenizer(val, character);
        String out = "";
        while (tokenizer.hasMoreTokens())
        {
            String str1 = tokenizer.nextToken();
            out += str1;
        }
        return out;
    }

    /**
     * @param val
     * @param noOfDigits
     * @return
     */
    public static String appendZerosToString(String val, int noOfDigits)
    {
        String str = val;
        for (int index = val.length(); index < noOfDigits; index++)
        {
            str = "0" + str;
        }
        return str;
    }

    /**
     * @param val
     * @return
     */
    public static String getFistLettersFromAllWords(String val)
    {
        StringTokenizer tokenizer = new StringTokenizer(val, " ");
        String str = "";
        while (tokenizer.hasMoreTokens())
        {
            str += tokenizer.nextToken().substring(0, 1);
        }
        return str.toUpperCase();
    }

    /**
     * @param request
     * @return
     */
    public static String getRequestedURIPath(HttpServletRequest request)
    {
        String path = request.getRequestURI();
        String requestedFilePath = path.substring(path.indexOf("/", 1) + 1);
        return requestedFilePath;
    }

    public static String getApplicationURL(HttpServletRequest request)
    {
        String path = request.getRequestURL().toString();
        String servletPath = request.getServletPath();
        String requestedFilePath = path.substring(0, path.indexOf(servletPath));
        return requestedFilePath;
    }

    public static String lowerCaseFirstLetter(final String value)
    {
        return Character.toLowerCase(value.charAt(0)) + value.substring(1);
    }

    public static String stringify(String value)
    {
        return value == null ? "" : value;
    }
    
    public static long getTendigitsRandomUniqueId() 
   	{
   		long LIMIT = 10000000000L;
   		long last = 0;
   		
   	  // 10 digits.
   	  long id = System.currentTimeMillis() % LIMIT;
   	  if ( id <= last ) {
   	    id = (last + 1) % LIMIT;
   	  }
   	  last = id;
   	  return last;
   	}
       
       public static long getTendigitsSequenceNumber(long counter) 
   	{
   		if(counter==0)
   			counter = 1000000001;
   		else
   			counter=counter+1;

   	  return counter;
   	}
 //To get ten digits sequence number for primary    
       public static long getTendigitsSequenceNumberForPrimary(long counter) 
   	{
   		if(counter==0)
   			counter = 1000000001;
   		else
   			counter=counter+1;
   	  return counter;
   	}
       
       //To get ten digits sequence number for Secondary      
       public static long getTendigitsSequenceNumberForSecondary(long counter) 
   	{
   		if(counter==0)
   			counter = 2000000001;
   		else
   			counter=counter+1;
   	  return counter;
   	}
       
//       public static String getTendigitsSequenceNumberForPrimary(Long counter,Integer year,String plantCode,String lineCode){
//    	    // Get last 2 digits of year (2026 -> 26)
//    	    String yr = String.valueOf(year).substring(2);
//    	    if (counter == null || counter == 0)
//    	        counter = Long.parseLong(yr + "00000001");
//    	    else
//    	        counter++;
//    	    return plantCode + lineCode + "-" + counter;
//    	}     
//
//       
//       public static String getTendigitsSequenceNumberForSecondary(Long counter,Integer year,String plantCode,String lineCode){
//   	    // Get last 2 digits of year (2026 -> 26)
//   	    String yr = String.valueOf(year).substring(2);
//   	    if (counter == null || counter == 0)
//   	        counter = Long.parseLong(yr + "00000001");
//   	    else
//   	        counter++;
//   	    return plantCode + lineCode + "-" + counter;
//   	} 
       
       
       
       public static String generateRandomWithSpecificDigits(int digits) 
       {
           Random rand = new Random();
           long num = (long)(rand.nextDouble()*100000000000000L);
           String s = String.format("%0"+digits+"d", num);
           //return Long.valueOf(s);
           return s;
       }
       
       
       public static String generateRandomStringWithLength(int len) {
   		String aToZ="1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; 
   		Random rand=new Random();
   	    StringBuilder res=new StringBuilder();
   	    for (int i = 0; i<len; i++) {
   	       int randIndex=rand.nextInt(aToZ.length()); 
   	       res.append(aToZ.charAt(randIndex));            
   	    }
   	    return res.toString();
   	}
       
    
      public static String getCharecterByNumber(Integer num)
      {
    	  final  HashMap<Integer, String> alphabets = new HashMap<>();
 			alphabets.put(0,"0");
 			alphabets.put(1,"a");
 			alphabets.put(2,"b");
 			alphabets.put(3,"c");
 			alphabets.put(4,"d");
 			alphabets.put(5,"e");
 			alphabets.put(6,"f");
 			alphabets.put(7,"g");
 			alphabets.put(8,"h");
 			alphabets.put(9,"i");
 			alphabets.put(10,"j");
 			alphabets.put(11,"k");
 			alphabets.put(12,"l");
 			alphabets.put(13,"m");
 			alphabets.put(14,"n");
 			alphabets.put(15,"o");
 			alphabets.put(16,"p");
 			alphabets.put(17,"q");
 			alphabets.put(18,"r");
 			alphabets.put(19,"s");
 			alphabets.put(20,"t");
 			alphabets.put(21,"u");
 			alphabets.put(22,"v");
 			alphabets.put(23,"w");
 			alphabets.put(24,"x");
 			alphabets.put(25,"y");
 			alphabets.put(26,"z");
 			
 			alphabets.put(27,"A");
 			alphabets.put(28,"B");
 			alphabets.put(29,"C");
 			alphabets.put(30,"D");
 			alphabets.put(31,"E");
 			alphabets.put(32,"F");
 			alphabets.put(33,"G");
 			alphabets.put(34,"H");
 			alphabets.put(35,"I");
 			alphabets.put(36,"J");
 			alphabets.put(37,"K");
 			alphabets.put(38,"L");
 			alphabets.put(39,"M");
 			alphabets.put(40,"N");
 			alphabets.put(41,"O");
 			alphabets.put(42,"P");
 			alphabets.put(43,"Q");
 			alphabets.put(44,"R");
 			alphabets.put(45,"S");
 			alphabets.put(46,"T");
 			alphabets.put(47,"U");
 			alphabets.put(48,"V");
 			alphabets.put(49,"W");
 			alphabets.put(50,"X");
 			alphabets.put(51,"Y");
 			alphabets.put(52,"Z");
 			
    	  return alphabets.get(num);
      }
       
       
       public static String generateUniqueString(String generatedBy,String version,int mfgYr,int cmp,int plnt,int lin)
       {
   			StringBuffer sb = new StringBuffer();
   			
   			Integer r = (int)cmp/52;
   			String first = getCharecterByNumber(r);
   			Integer add = (int)(cmp-(r*52));
   			String sec = getCharecterByNumber(add);
   			
   			Integer r1 = (int)mfgYr/52;
   			int mf=mfgYr-r1;
   			
   			sb.append(generatedBy).append(version).append(first).append(sec).append(getCharecterByNumber(plnt)).append(getCharecterByNumber(lin)).append(getCharecterByNumber(r1)).append(getCharecterByNumber(mf));
   			return sb.toString();
       }
       
       public static String generateStringByParams(int mfgYr,int cmp,int plnt,int lin)
       {
   			StringBuffer sb = new StringBuffer();
   			
   			Integer r = (int)cmp/52;
   			String first = getCharecterByNumber(r);
   			Integer add = (int)(cmp-(r*52));
   			String sec = getCharecterByNumber(add);
   			
   			Integer r1 = (int)mfgYr/52;
   			int mf=mfgYr-r1;
   			
   			sb.append(first).append(sec).append(getCharecterByNumber(plnt)).append(getCharecterByNumber(lin)).append(getCharecterByNumber(r1)).append(getCharecterByNumber(mf));
   			return sb.toString();
       }
       
       
       public static String unicodeGeneration()
	   	{
	   		Random rand = new Random(); String
	   		yourValue = String.format((Locale)null,
	   				"UID-%02d%02d%04d%04d%04d",
	   				rand.nextInt(100),
	   				rand.nextInt(100), 
	   				rand.nextInt(10000),
	   				rand.nextInt(10000), 
	   				rand.nextInt(10000));
	   				return yourValue;
	   	}
       
       
       
       
       public static String getUIDWithDynamicKeyIndex(String genby,String vn,String vc,String s1,String s2)
		{
			String s[] = new String[24];
			s[0]=genby;
			s[1]=vn;
			s[2]=vc;
			StringBuffer sb = new StringBuffer();
			
			//String s2=RandomStringUtils.randomAlphanumeric(14);
			//String s2= StringUtils.generateRandomStringWithLength(14);
			
			int min=5;
			int max=19;
			int b = (int)(Math.random()*(max-min+1)+min);
			b--;
			int rem = b%26;
			char c = (char)((int)'A' + rem);
			s[3]=""+c;
			
			int j=0;
			for(int i=b;i<=b+5;i++) 
			{
				s[i]=""+s1.charAt(j);
				j++;
			}
			System.out.println(Arrays.toString(s));
			
			int x=0;
			for (int i=0; i<=s.length-1; i++) 
			{
				if (s[i] != null) {
				}
				else {
					s[i]=""+s2.charAt(x);
					x++;
				}
				sb.append(s[i]);
			}
			return sb.toString();
		}

		// Single method replaces both getTendigitsSequenceNumberForPrimary and
		// getTendigitsSequenceNumberForSecondary Output → "P1L1-2600000001"
		public static String buildSerialString(long counter, String plantId, String lineId) {
			return plantId + lineId + "-" + counter;
		} 
}
