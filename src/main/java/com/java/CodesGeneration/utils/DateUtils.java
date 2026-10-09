package com.java.CodesGeneration.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class DateUtils
{
    private DateUtils()
    {

    }

    private static final Log LOG = LogFactory.getLog(DateUtils.class);

    /**
     * static final members for various date formats useful in the application
     */
    public static final SimpleDateFormat YYYY_MM_DD_TIME = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
    public static final SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat DD_MM_YYYY_SLASH = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat HH_MM_SS_SS = new SimpleDateFormat("hh:mm:ss.SS");
    public static final SimpleDateFormat MM_DD_YYYY_TIME = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SS");
    public static final SimpleDateFormat MM_DD_YYYY = new SimpleDateFormat("MM/dd/yyyy");
    public static final SimpleDateFormat MM_DD_YYYY_TIME_SHORT = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
    public static final SimpleDateFormat DD_MM_YYYY_TIME_SHORT = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
    public static final SimpleDateFormat FULL_DATE_WITH_DAY = new SimpleDateFormat("EEE MMM dd, yyyy hh:mm a");
    public static final SimpleDateFormat MM_DD_YYYY_FULL_WITH_DAY = new SimpleDateFormat("MM/dd/yyyy EEE hh:mm a");
    public static final SimpleDateFormat TIME_FORMAT_ZONE = new SimpleDateFormat("K:mm a, z");
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");
    public static final SimpleDateFormat DD_MM_YYYY_hh_MM = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    public static final SimpleDateFormat DEFAULT_TIMESTAMP_FORMAT = MM_DD_YYYY_TIME;
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = DD_MM_YYYY_SLASH;
    public static final SimpleDateFormat DEFAULT_DATETIME_FORMAT = DD_MM_YYYY_TIME_SHORT;
    public static final SimpleDateFormat YYYY_MM_DD_HH_MM = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat dd_MMM_yyyy_FORMAT = new SimpleDateFormat("dd-MMM-yyyy");
    public static final SimpleDateFormat dd_MMM_yyyy_FORMAT1 = new SimpleDateFormat("dd MMM yyyy hh:mm a");
    public static final SimpleDateFormat YYMMDD_FORMAT = new SimpleDateFormat("yyMMdd");
    public static final SimpleDateFormat dd_MMM_yyyy_FORMAT3 = new SimpleDateFormat("dd.MMM.yyyy");
    public static final SimpleDateFormat dd_MMM_yyyy_FORMAT2 = new SimpleDateFormat("dd MMM yyyy hh:mm a");
    public static final SimpleDateFormat yyyy_MMM_dd_FORMAT3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat dd_MMM_yyyy_FORMAT4 = new SimpleDateFormat("dd MMM yyyy hh:mm:ss a");

    /**
     * Used to format the date from sal format to dd/mm/yyyy format
     * 
     * @param date
     * @return string in dd/mm/yyyy format
     */
    public static String formatDate(String date)
    {
        try
        {
            if (date != null)
            {
                SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");

                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                return df.format(dbFormat.parse(date));
            }
        }
        catch (ParseException e)
        {
            // e.printStackTrace();
            LOG.error("Date Parsing Exception : " + e.getMessage());
        }

        return date;
    }

    public static String formatDate(Date date, String dateFormat)
    {
        if (date != null)
        {
            if (dateFormat == null)
            {
                dateFormat = "dd/MM/yyyy";
            }
            DateFormat formatter = new SimpleDateFormat(dateFormat);

            return formatter.format(date);
        }
        return null;
    }

    /**
     * used to convert String date to sql date
     * 
     * @param date
     * @return
     */
    public static java.sql.Date getSqlDateFromString(String date)
    {
        try
        {
            if (date != null)
            {
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                java.util.Date dat = formatter.parse(date);
                Calendar c = formatter.getCalendar();
                c.setTime(dat);
                long l = c.getTimeInMillis();
                return new java.sql.Date(l);
            }
            return null;
        }
        catch (ParseException e)
        {
            LOG.error(e.getMessage(), e);
            return null;
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * used to convert String date to sql date
     * 
     * @param date
     * @return
     */
    public static java.sql.Date getSqlDateFromString(String date, String dateFormat)
    {
        try
        {
            if (date != null)
            {
                if (dateFormat == null)
                {
                    dateFormat = "dd/MM/yyyy";
                }
                DateFormat formatter = new SimpleDateFormat(dateFormat);
                java.util.Date dat = formatter.parse(date);
                Calendar c = formatter.getCalendar();
                c.setTime(dat);
                long l = c.getTimeInMillis();
                return new java.sql.Date(l);
            }
            return null;
        }
        catch (ParseException e)
        {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * To get previous day's date for the entered date string
     * 
     * @param dateStr
     * @return sql date
     */
    public static java.sql.Date getPreviousDayDate(String dateStr)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date yesterday = null;
        try
        {
            GregorianCalendar gc = new GregorianCalendar();
            java.util.Date d = sdf.parse(dateStr);
            gc.setTime(d);
            int dayBefore = gc.get(Calendar.DAY_OF_YEAR);
            gc.roll(Calendar.DAY_OF_YEAR, -1);
            int dayAfter = gc.get(Calendar.DAY_OF_YEAR);
            if (dayAfter > dayBefore)
            {
                gc.roll(Calendar.YEAR, -1);
            }
            gc.get(Calendar.DATE);
            yesterday = gc.getTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return getSqlDateFromString(sdf.format(yesterday));
    }

    public static String getAfterDayDate(String dateStr)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String tomarrow = null;
        try
        {
            GregorianCalendar gc = new GregorianCalendar();
            java.util.Date d = sdf.parse(dateStr);
            gc.setTime(d);
            gc.add(Calendar.DATE, 1);
            tomarrow = gc.get(Calendar.YEAR) + "-" + (gc.get(Calendar.MONTH) + 1) + "-" + gc.get(Calendar.DATE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tomarrow;
    }

    /**
     * method to getCurrent Time Stamp as String
     * 
     * @return
     */

    public static String getCurrentTimestamp()
    {
        Date date = new Date();
        return DEFAULT_TIMESTAMP_FORMAT.format(date);
    }

    /**
     * method to get timestamp according to dateformat given
     * 
     * @param format
     * @return
     */

    public static String getCurrentTimestamp(SimpleDateFormat format)
    {
        Date date = new Date();
        return DEFAULT_TIMESTAMP_FORMAT.format(date);
    }

    /**
     * uses the default DEFAULT_DATE_FORMAT or DEFAULT_DATETIME_FORMAT based on input length
     * 
     * @param dateString
     * @return java.util.Date
     * @see DEFAULT_DATE_FORMAT
     * @see DEFAULT_DATETIME_FORMAT
     */

    public static Date getDate(String dateString)
    {
        try
        {
            if (dateString != null && dateString.length() < 13)
            {
                return YYYY_MM_DD.parse(dateString); // return date only
            }
            else
            {
                //
                return DEFAULT_DATE_FORMAT.parse(dateString); // return
                                                              // timestamp
                                                              // column
            } // end if
        }
        catch (ParseException e)
        {
            LOG.error(e.getMessage());
            return null;
        }
    } // end

    /**
     * @param format
     * @return
     */
    public static Date getCurrentDate(String format)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try
        {
            return simpleDateFormat.parse(getCurrentDateAsString(format));
        }
        catch (ParseException e)
        {
            LOG.error(e.getStackTrace());
        }
        return null;
    }

    /**
     * @param format
     * @return
     */
    public static String getCurrentDateAsString(String format)
    {
        Date data = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(data);
    }

    /**
     * uses the default DEFAULT_DATE_FORMAT or DEFAULT_DATETIME_FORMAT based on input length
     * 
     * @param dateString
     * @return java.util.Date
     * @see DEFAULT_DATE_FORMAT
     * @see DEFAULT_DATETIME_FORMAT
     */

    public static Date getDate(String dateString, String format)
    {
        try
        {
            if (format == null)
            {
                format = "dd-MM-yyyy";
            }
            return new SimpleDateFormat(format).parse(dateString); // return
                                                                   // date only
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }
    } // end

    /**
     * to get java.sql.Date instance of current System date
     * 
     * @return
     */
    public static java.sql.Date getCurrentSystemDate()
    {
        return new java.sql.Date(Calendar.getInstance().getTime().getTime());
    }

    public static void main(String[] args)
    {
        System.out.println(getCurrentSystemDate());
    }

    /**
     * to get java.sql.Time from current System time
     * 
     * @return
     */
    public static java.sql.Time getCurrentSystemTime()
    {
        return new java.sql.Time(Calendar.getInstance().getTime().getTime());
    }

    /**
     * to get java.sql.Timestamp from current System time
     * 
     * @return
     */
    public static java.sql.Timestamp getCurrentSystemTimestamp()
    {
        return new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
    }

    /**
     * @param date
     * @return
     */
    public static java.sql.Date getSqlDateFromUtilDate(java.util.Date date)
    {
        return new java.sql.Date(date.getTime());
    }
    public static java.util.Date getUtilDateFromSqlDate(java.sql.Date date)
    {
        return new java.util.Date(date.getTime());
    }

    /**
     * @return
     */
    public static String getCurrentTimeInStringFormat()
    {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
        return ft.format(d);
    }

    public static String getCurrentDateInStringFormat()
    {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        return ft.format(d);
    }

    public static String getCurrentTimeInStringFormatWithMilliSec()
    {
        Date d = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return ft.format(d);
    }

    /**
     * @param monthValue
     * @param yearValue
     * @return
     */
    public static int getMaxDateInMonth(String monthValue, String yearValue)
    {
        int maxDay = 0;
        try
        {
            int month = Integer.parseInt(monthValue);
            int year = Integer.parseInt(yearValue);
            GregorianCalendar cal = new GregorianCalendar(year, (month - 1), 1); // Get
                                                                                 // the
                                                                                 // number
                                                                                 // of
                                                                                 // days
                                                                                 // in
                                                                                 // that
                                                                                 // month
            maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            LOG.info("Max Day: " + maxDay);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
        return maxDay;
    }

    /**
     * @param date1
     * @param date2
     * @return
     */
    public static String compareDates(java.sql.Date date1, java.sql.Date date2)
    {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        String str = "";
        long timeinmillis1 = calendar1.getTimeInMillis();
        long timeinmillis2 = calendar2.getTimeInMillis();
        if (timeinmillis1 > timeinmillis2)
        {
            str = "DATE1";
        }
        else
        {
            str = "DATE2";
        }

        return str;
    }

    /**
     * @param dateStr
     * @param format
     * @return
     */
    public static Time getSqlTimeStamp(String dateStr, String format)
    {
        Date date = getDate(dateStr, format);
        return new Time(date.getTime());
    }

    public static long getDateTimeInMilliSeconds(String dateStr, String timeStr)
    {
        // Make a String that has a date in it, with MEDIUM date format
        // and SHORT time format.
        String dateString = dateStr + " " + timeStr;

        // Get the default MEDIUM/SHORT DateFormat
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date date = null;
        // Parse the date
        try
        {
            date = format.parse(dateString);
        }
        catch (ParseException pe)
        {
            System.out.println("ERROR: could not parse date in string \"" + dateString + "\"");
        }
        return date.getTime();
    }

    public static int daysBetween(java.sql.Date d1, java.sql.Date d2)
    {
        return ((int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24))) + 1;
    }
    
    public static int getDaysDifferenceBetweenDays(java.sql.Date fromDate, java.sql.Date toDate)
    {
        return ((int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24)));
    }
    
    @SuppressWarnings("unused")
	public static String daysHoursSecsBetweenTwoTimeStamps(Timestamp t1, Timestamp t2)
    {
    	String difference="";
        long different = t2.getTime() - t1.getTime();
		
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		
		long elapsedSeconds = different / secondsInMilli;
		
		LOG.info(" XXXXXX----------->"+elapsedDays +"days"+elapsedHours+"hours"+elapsedMinutes+"minutes"+elapsedSeconds+"seconds");
		return  difference=elapsedDays+" days "+elapsedHours+" hours "+elapsedMinutes+" minutes "+elapsedSeconds+" seconds ";
    }
    
    /**
     * to get java.sql.Time from String time
     * 
     * @return
     */
    @SuppressWarnings("deprecation")
    public static java.sql.Time getSqlTimeFromString(String timeStr)
    {
        try
        {
            if (timeStr != null)
            {
                String[] ampmSplitArr = timeStr.split(" ");
                String time = ampmSplitArr[0];
                String[] actualTime = time.split(":");
                if (timeStr.contains("AM"))
                {
                    if (Integer.parseInt(actualTime[0]) == 12)
                    {
                        return new java.sql.Time(Integer.parseInt(actualTime[0]) + 12, Integer.parseInt(actualTime[1]), Integer.parseInt("00"));
                    }
                    else
                    {
                        return new java.sql.Time(Integer.parseInt(actualTime[0]), Integer.parseInt(actualTime[1]), Integer.parseInt("00"));
                    }
                }
                else
                {
                    if (Integer.parseInt(actualTime[0]) == 12)
                    {
                        return new java.sql.Time(Integer.parseInt(actualTime[0]), Integer.parseInt(actualTime[1]), Integer.parseInt("00"));
                    }
                    else
                    {
                        return new java.sql.Time(Integer.parseInt(actualTime[0]) - 12, Integer.parseInt(actualTime[1]), Integer.parseInt("00"));
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String formatTime(String timeStr)
    {
        String[] timeArr = timeStr.split(":");
        String time = "";
        int hours = Integer.parseInt(timeArr[0]);
        if (hours < 12 && hours != 0)
        {
            time = hours + ":" + timeArr[1] + " AM";
        }
        else if (hours == 12)
        {
            time = hours + ":" + timeArr[1] + " PM";
        }
        else if (hours == 0)
        {
            time = 12 + ":" + timeArr[1] + " AM";
        }
        else
        {
            hours = hours - 12;
            time = hours + ":" + timeArr[1] + " PM";
        }
        return time;
    }

    public static double timeDifference(long latertime, long earliertime)
    {
        double difference = latertime - earliertime;
        double noOfHours = (difference) / (1000 * 60 * 60);
        return noOfHours;
    }

    public static long timeDifferenceInHours(long latertime, long earliertime)
    {
        long noOfHours = (latertime - earliertime) / (1000 * 60 * 60);
        return noOfHours;
    }

    public static long timeDifferenceInMinutes(long laterTime, long earliyerTime)
    {
        long noOfMinuts = (long) Math.floor((laterTime - earliyerTime) / 1000 / 60);
        return noOfMinuts;
    }

    public static long ellapsedMinutes(long laterTime, long earliyerTime)
    {
        int noOfMinuts = (int) (Math.floor((laterTime - earliyerTime) / (1000 * 60)) % 60);
        return noOfMinuts;
    }

    @SuppressWarnings("unused")
    public static long dateDifference(long latertime, long earliertime)
    {
        double difference = latertime - earliertime;

        double daysDifference = Math.floor(difference / 1000 / 60 / 60 / 24);
        difference -= daysDifference * 1000 * 60 * 60 * 24;

        double hoursDifference = Math.floor(difference / 1000 / 60 / 60);
        difference -= hoursDifference * 1000 * 60 * 60;

        double minutesDifference = Math.floor(difference / 1000 / 60);
        difference -= minutesDifference * 1000 * 60;

        double secondsDifference = Math.floor(difference / 1000);

        return (long) daysDifference;
    }

    public static String getTime(String time)
    {
        String subTime = time.substring(0, time.length() - 3);
        if (time.contains("PM"))
        {
            int hours = Integer.parseInt(subTime.substring(0, subTime.indexOf(':')));
            subTime = subTime.replaceFirst("" + hours, "" + (hours + 12));
        }
        return subTime;
    }

    public static synchronized String getUniqueId()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssS");
        return dateFormat.format(new Date());
    }

    public static java.sql.Date addDays(Date d, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, (int) days);
        Date newDate = cal.getTime();
        return new java.sql.Date(newDate.getTime());
    }

    /**
     * used to convert String date to TimeStamp
     * 
     * @param date
     * @return
     */
    public static Timestamp getTimeStampDateFromString(String date)
    {
        try
        {
            if (date != null)
            {
                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                java.util.Date dat = formatter.parse(date);
                Calendar c = formatter.getCalendar();
                c.setTime(dat);
                long l = c.getTimeInMillis();
                return new Timestamp(new java.sql.Date(l).getTime());
            }
            return null;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }

    public static Timestamp getTimeStampFromString(String date, String format)
    {
        try
        {
            if (date != null)
            {
                DateFormat formatter = new SimpleDateFormat(format);
                java.util.Date dat = formatter.parse(date);
                Calendar c = formatter.getCalendar();
                c.setTime(dat);
                long l = c.getTimeInMillis();
                return new Timestamp(new java.sql.Date(l).getTime());
            }
            return null;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }

    public static String convertTimeIn24hourFormat(String date)
    {
        try
        {
            if (date != null)
            {
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                Date formatedDate = parseFormat.parse(date);
                System.out.println(parseFormat.format(formatedDate) + " = " + displayFormat.format(formatedDate));
                return displayFormat.format(formatedDate);
            }
            return null;
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public static String convertDateFormat(String dateStr, SimpleDateFormat sourceFormat, SimpleDateFormat destFormat)
    {
        try
        {
            Date date = sourceFormat.parse(dateStr);
            dateStr = destFormat.format(date);
            return dateStr;
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public static Timestamp addDays2CurrentDate(long noOfDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, (int) noOfDays);
        Date newDate = cal.getTime();
        return new Timestamp(newDate.getTime());
    }

    public static long getDaysDifference(String date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStop = simpleDateFormat.format(new Date());

        // HH converts hour in 24 hours format (0-23), day calculation
        Date d1 = null;
        Date d2 = null;

        try
        {
            d1 = simpleDateFormat.parse(date);
            d2 = simpleDateFormat.parse(dateStop);

            // in milliseconds
            long diff = d2.getTime() - d1.getTime();

            // long diffSeconds = diff / 1000 % 60;
            // long diffMinutes = diff / (60 * 1000) % 60;
            // long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            return diffDays;
        }
        catch (ParseException e)
        {
            return 0L;
        }
    }

    public static java.sql.Date addDays2GivenDate(String date, int days) throws ParseException
    {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        java.util.Date dat = formatter.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dat);
        cal.add(Calendar.DAY_OF_MONTH, (int) days);
        Date newDate = cal.getTime();
        return new java.sql.Date(newDate.getTime());
    }

    public static java.sql.Date addDaysToGivenDate(Date givenDate, int days)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(givenDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date newDate = calendar.getTime();
        return new java.sql.Date(newDate.getTime());
    }

    public static java.sql.Date getCurrentSqlDate()
    {
        Date d = new Date();
        return new java.sql.Date(d.getTime());
    }

    public static boolean validateDateFormat(String date)
    {
        if (date.matches("([0-9]{2})-([0-9]{2})-([0-9]{4})")) return true;
        else return false;
    }

    public static String format(Timestamp timestamp, String format)
    {
        try
        {
            if (format == null)
            {
                format = "yyyyMMdd-HHmm";
            }
            SimpleDateFormat sdf = YYYY_MM_DD_TIME;
            Date utilDate = sdf.parse(timestamp + "");
            return DateUtils.formatDate(utilDate, format);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
    
    public static String formatFromTimestamp(Timestamp timestamp, String format)
    {
        try
        {
            if (format == null)
            {
                format = "YYYY_MM_DD";
            }
            SimpleDateFormat sdf = YYYY_MM_DD;
            Date utilDate = sdf.parse(timestamp + "");
            return DateUtils.formatDate(utilDate, format);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
    public static String formatSqlDate(java.sql.Date date, String format)
    {
        SimpleDateFormat simpDate = new SimpleDateFormat(format);
        return simpDate.format(date);
    }

    public static String formatTo12Hour(Timestamp timestamp)
    {
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mma");
            return sdf.format(timestamp);
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    public static String convertStringToDate(Date indate, String format)
    {
        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat(format);
        /*
         * you can also use DateFormat reference instead of SimpleDateFormat like this: DateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
         */
        try
        {
            dateString = sdfr.format(indate);
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }
        return dateString;
    }

    /**
     * @author rabindranath.s
     * @param date
     * @return {int} Day Of Month
     */
    public static int getDayOfMonth(Date date)
    {
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        catch (Exception e)
        {
            LOG.info(e.getCause(), e);
            return 0;
        }
    }

    /**
     * @author rabindranath.s
     * @param value
     * @return @return {String} Day Of Month Suffix
     */
    public static String getOrdinalSuffix(int value)
    {
        int hunRem = value % 100;
        int tenRem = value % 10;

        if (hunRem - tenRem == 10)
        {
            return "th";
        }
        switch (tenRem)
        {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
    
    public static String getdatefromTimeStamp(Timestamp timestamp) {
    	if(timestamp!=null)
        return dd_MMM_yyyy_FORMAT.format(timestamp.getTime());
    	else
    		return null;
    }
    
    
    public static String getdateDDMMMYYYYFromTimestamp(Timestamp timestamp) {
    	if(timestamp!=null)
        return dd_MMM_yyyy_FORMAT1.format(timestamp.getTime());
    	else
    		return null;
    }
    
    public static java.sql.Timestamp addHoursToTimeStamp(int hrs) throws ParseException
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, (int) hrs);
        Date newDate = cal.getTime();
        return new java.sql.Timestamp(newDate.getTime());
    }
    
    public static Timestamp getTimeStampDateFromStringTimeStamp(String timestamp)
    {
        try
        {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
            return ts;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    public static Timestamp getTimeStampDateFromStringTimeStampMicroSS(String timestamp)
    {
        try
        {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
            return ts;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    public static Timestamp getTimeStampDateFromStringTimeStamp1(String timestamp)
    {
        try
        {
        	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
            return ts;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    
    
    public static Timestamp getTimeStampDateFromStringTimeStampWithFormat(String timestamp,String frmt)
    {
        try
        {
        	String fm="";
        	if(frmt!=null)
        		fm=frmt;
        	else
        		fm="yyyy-MMM-dd hh:mm:ss";
        	SimpleDateFormat dateFormat = new SimpleDateFormat(fm);
            Date parsedDate = dateFormat.parse(timestamp);
            Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
            return ts;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    
    public static String convertDateFormatyyyyMMddToddMMMyyyy(String strDate)
    {
        try
        {
        	//input date format
        	 SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
        	//output date format
        	 SimpleDateFormat dFormatFinal = new SimpleDateFormat("dd-MMM-yyyy");
        	 Date date1 = dFormat.parse(strDate);
     		String ddate = dFormatFinal.format(date1);
            return ddate;
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
    public static String convertDateFormatyyMMddToddMMMyyyy(String strDate)
    {
        try
        {
        	//input date format
        	 SimpleDateFormat dFormat = new SimpleDateFormat("yyMMdd");
        	//output date format
        	 SimpleDateFormat dFormatFinal = new SimpleDateFormat("dd MMM yyyy");
        	 Date date1 = dFormat.parse(strDate);
     		String ddate = dFormatFinal.format(date1);
            return ddate.toUpperCase();
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    public static String getdatefromTimeStampAsddMMMyyyy(Timestamp timestamp) {
    	if(timestamp!=null)
        return dd_MMM_yyyy_FORMAT1.format(timestamp.getTime());
    	else
    		return null;
    }
    public static String getdateAsYYMMDDfromTimeStamp(Timestamp timestamp) {
    	if(timestamp!=null)
        return YYMMDD_FORMAT.format(timestamp.getTime());
    	else
    		return null;
    }
    public static String getdatefromTimeStampAsddMMMyyyy3(Timestamp timestamp) {
    	if(timestamp!=null)
        return dd_MMM_yyyy_FORMAT3.format(timestamp.getTime());
    	else
    		return null;
    }
    public static String getdateddMMyyyyfromTimeStamp(Timestamp timestamp) {
    	if(timestamp!=null)
        return DD_MM_YYYY.format(timestamp.getTime());
    	else
    		return null;
    }
    
    public static String convertDateFormatyyMMddToyyyyMMdd(String strDate)
    {
        try
        {
        	//input date format
        	 SimpleDateFormat dFormat = new SimpleDateFormat("yyMMdd");
        	//output date format
        	 SimpleDateFormat dFormatFinal = new SimpleDateFormat("yyyy-MM-dd");
        	 Date date1 = dFormat.parse(strDate);
     		String ddate = dFormatFinal.format(date1);
            return ddate.toUpperCase();
        }
        catch (ParseException e)
        {
            LOG.info(e.getCause(), e);
            return null;
        }
    }
    
}
