package com.cheche365.cheche.core.util

class CalendarUtil {

    /**
     * 传入一个整数，相应的加，减相应天数天数
     * @param day
     * @return
     */
    static Date appointDate(int day){
        Calendar  calendar = new GregorianCalendar()
        calendar.setTime(new Date())
        calendar.add(calendar.DATE,day)
        calendar.getTime()
    }


    static int differentDaysByMillisecond(Date date1,Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24))
        return days+1
    }

    static int dateToInt(Date date,int calendarType){
        Calendar ca = Calendar.getInstance()
        ca.setTime(date)
        ca.get(calendarType)
    }
}
