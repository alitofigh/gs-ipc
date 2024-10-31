package org.gsstation.novin.util.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
    private static String Farsi_Date = null;
    private static String English_Date = null;
    private static int The_Year;
    private static int The_Month;
    private static int The_Day;

    public static String currentDate() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("MMdd");
        String currentDate = dateFormat.format(date);
        return currentDate;
    }

    public static String currentTime() {
        return configCurrentTime();
    }

    public static String currentDateTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String currentDateTime = dateFormat.format(date);
        return currentDateTime;
    }

    public static String currentPrintedDateTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        String currentDateTime = dateFormat.format(date);
        return currentDateTime;
    }

    public static String configCurrentDate() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDateTime = dateFormat.format(date);
        return currentDateTime;
    }

    public static String configCurrentTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HHmmss");
        String currentDateTime = dateFormat.format(date);
        return currentDateTime;
    }

    public static String configCurrentTime2() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentDateTime = dateFormat.format(date);
        return currentDateTime;
    }

    public static String newWorkingDate() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MMdd");
        String tomorrowDateTime = dateFormat.format(tomorrow);
        return tomorrowDateTime;
    }

    public static String getCurrentDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

    public static String currentPersianDate() {
        String currentDate = currentDateTime().substring(0, 8);
        return getPersianDate(currentDate);
    }

    private static String getPersianDate(String En_Date) {
        int The_Select;
        try {
            if (En_Date.length() == 8) {
                The_Year = Integer.parseInt(En_Date.substring(0, 4));
                The_Month = Integer.parseInt(En_Date.substring(4, 6));
                The_Day = Integer.parseInt(En_Date.substring(6, 8));
            } else if (En_Date.length() == 10) {
                The_Year = Integer.parseInt(En_Date.substring(0, 4));
                The_Month = Integer.parseInt(En_Date.substring(5, 7));
                The_Day = Integer.parseInt(En_Date.substring(8, 10));
            } else {
                /**
                 * here it accepts the M/D/YYYY or MM/D/YYYY or M/DD/YYYY in
                 * case a user enters date in above format
                 */

                The_Year = Integer.parseInt(En_Date.substring(
                        En_Date.lastIndexOf("/") + 1,
                        En_Date.lastIndexOf("/") + 5));
                The_Month = Integer.parseInt(En_Date.substring(0,
                        En_Date.indexOf("/")));
                The_Day = Integer.parseInt(En_Date.substring(
                        En_Date.indexOf("/") + 1, En_Date.lastIndexOf("/")));
            }
        } catch (Exception e) {
            System.out.println("Can not recognized the given date:" + e);
            return null;
        }

        if (The_Day > 31 || The_Month > 12) {
            System.out.println("Can not recognized the given date");
            return null;
        }

        if (The_Year % 4 == 0)
            The_Select = 1;
        else
            The_Select = 2;

        if ((The_Year - 1) % 4 == 0)
            The_Select = 3;

        if (The_Select == 1) {
            switch (The_Month) {
                case 1: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 10;
                        The_Month = 10;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 11;
                        The_Year = The_Year - 622;
                    }

                    break;
                }
                case 2: {

                    if (The_Day >= 1 && The_Day <= 19) {
                        The_Day = The_Day + 11;
                        The_Month = 11;
                        The_Year = The_Year - 622;

                    } else if (The_Day >= 20 && The_Day <= 29) {
                        The_Day = The_Day - 19;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    }
                    break;
                }

                case 3: {
                    if (The_Day >= 1 && The_Day <= 19) {
                        The_Day = The_Day + 10;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 20 && The_Day <= 31) {
                        The_Day = The_Day - 19;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 4:

                {
                    if (The_Day >= 1 && The_Day <= 19) {
                        The_Day = The_Day + 12;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 20 && The_Day <= 30) {
                        The_Day = The_Day - 19;
                        The_Month = 2;
                        The_Year = The_Year - 621;
                    }
                    break;

                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 11;
                        The_Month = 2;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 3;
                        The_Year = The_Year - 621;
                    }

                    break;
                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 11;
                        The_Month = 3;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 30) {
                        The_Day = The_Day - 20;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 7: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    }
                    break;

                }

                case 8: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    }
                    break;

                }

                case 9: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 30) {
                        The_Day = The_Day - 21;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    }
                    break;

                }

                case 10: {

                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 9;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 11: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 10;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 30) {
                        The_Day = The_Day - 20;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 12: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 10;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 10;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                default:// in case a wrong date, it will come here.
                {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be MM/DD/YYYY");
                    return null;

                }
            }

        }// end of if in select->1
        // //////////////////////////////////////////////////////////////
        if (The_Select == 2) {

            switch (The_Month) {

                case 1: {

                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 10;
                        The_Month = 10;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 11;
                        The_Year = The_Year - 622;
                    }
                    break;
                }
                case 2: {
                    if (The_Day >= 1 && The_Day <= 19) {
                        The_Day = The_Day + 11;
                        The_Month = 11;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 19 && The_Day <= 28) {
                        The_Day = The_Day - 19;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    }
                    break;
                }
                case 3: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 9;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 4: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 11;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 30) {
                        The_Day = The_Day - 20;
                        The_Month = 2;
                        The_Year = The_Year - 621;

                    }
                    break;
                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 2;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 3;
                        The_Year = The_Year - 621;

                    }
                    break;
                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 3;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 30) {
                        The_Day = The_Day - 21;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 7: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    }

                    break;
                }
                case 8: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 9: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 30) {
                        The_Day = The_Day - 22;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 10: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 8;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 11: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 9;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 30) {
                        The_Day = The_Day - 21;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    }
                    break;

                }
                case 12: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 9;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 10;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                default: {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be MM/DD/YYYY");
                    return null;

                }

            }// End of switch
        }// end of if in select->2

        if (The_Select == 3) {
            switch (The_Month) {

                case 1: {
                    if (The_Day >= 1 && The_Day <= 19) {
                        The_Day = The_Day + 11;
                        The_Month = 10;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 20 && The_Day <= 31) {
                        The_Day = The_Day - 19;
                        The_Month = 11;
                        The_Year = The_Year - 622;
                    }
                    break;

                }
                case 2: {

                    if (The_Day >= 1 && The_Day <= 18) {
                        The_Day = The_Day + 12;
                        The_Month = 11;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 19 && The_Day <= 28) {
                        The_Day = The_Day - 18;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    }
                    break;
                }
                case 3: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 10;
                        The_Month = 12;
                        The_Year = The_Year - 622;
                    } else if (The_Day >= 21 && The_Day <= 31) {
                        The_Day = The_Day - 20;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 4: {
                    if (The_Day >= 1 && The_Day <= 20) {
                        The_Day = The_Day + 11;
                        The_Month = 1;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 21 && The_Day <= 30) {
                        The_Day = The_Day - 20;
                        The_Month = 2;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 2;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 3;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 10;
                        The_Month = 3;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 30) {
                        The_Day = The_Day - 21;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 7: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 4;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 8: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 5;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 9:// a
                {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 9;
                        The_Month = 6;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 30) {
                        The_Day = The_Day - 22;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 10: {
                    if (The_Day >= 1 && The_Day <= 22) {
                        The_Day = The_Day + 8;
                        The_Month = 7;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 23 && The_Day <= 31) {
                        The_Day = The_Day - 22;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    }
                    break;
                }
                case 11:// bb
                {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 9;
                        The_Month = 8;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 30) {
                        The_Day = The_Day - 21;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                case 12:// a
                {
                    if (The_Day >= 1 && The_Day <= 21) {
                        The_Day = The_Day + 9;
                        The_Month = 9;
                        The_Year = The_Year - 621;
                    } else if (The_Day >= 22 && The_Day <= 31) {
                        The_Day = The_Day - 21;
                        The_Month = 10;
                        The_Year = The_Year - 621;
                    }
                    break;
                }

                default:// s
                {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be MM/DD/YYYY");
                    return null;
                }
            }
        }
        // you may use different output
        String tmp_M = null;
        String tmp_D = null;

        tmp_M = The_Month < 10 ? "0" + The_Month : The_Month + "";
        tmp_D = The_Day < 10 ? "0" + The_Day : The_Day + "";
        //if(The_Month<10)tmp_M="0"+The_Month;
        //if(The_Day<10)tmp_D="0"+The_Day;
        Farsi_Date = The_Year + "/" + tmp_M + "/" + tmp_D;
        return Farsi_Date;

    }// end of function Fa->En

    public static String getPersianToGregorianDate(String Fa_Date) {

        try {
            if (Fa_Date.length() == 10) {
                The_Year = Integer.parseInt(Fa_Date.substring(0, 4));
                The_Month = Integer.parseInt(Fa_Date.substring(5, 7));
                The_Day = Integer.parseInt(Fa_Date.substring(8, 10));
            } else {
                The_Year = Integer.parseInt(Fa_Date.substring(0,
                        Fa_Date.indexOf("/")));
                The_Month = Integer.parseInt(Fa_Date.substring(
                        Fa_Date.indexOf("/") + 1, Fa_Date.lastIndexOf("/")));
                The_Day = Integer.parseInt(Fa_Date.substring(
                        Fa_Date.lastIndexOf("/") + 1, Fa_Date.length()));
            }
        } catch (Exception e) {
            System.out.println("Can not recognized the given date:" + e);
            return null;
        }

        if (The_Day > 31 || The_Month > 12) {
            System.out.println("Can not recognized the given date");
            return null;
        }

        int The_Select = The_Year % 4;

        if (The_Select == 0) {
            switch (The_Month) {
                case 1: {

                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 3;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 31) {
                        The_Day = The_Day - 11;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 2: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 20;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 3: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    }

                    break;
                }

                case 4: {

                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    }
                    break;

                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 8;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 6: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 8;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 9;
                        The_Year = The_Year + 621;
                    }
                    break;

                }
                case 7: {
                    if (The_Day >= 1 && The_Day <= 8) {
                        The_Day = The_Day + 22;
                        The_Month = 9;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 9 && The_Day <= 30) {
                        The_Day = The_Day - 8;
                        The_Month = 10;
                        The_Year = The_Year + 621;
                    }
                    break;

                }

                case 8: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 10;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 9: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 10: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    }

                }
                case 11: {
                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 12 && The_Day <= 30) {
                        The_Day = The_Day - 11;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    }
                    break;
                }

                case 12: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 19;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 3;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                default: {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be YYYY/MM/DD");
                    return null;
                }
            }
        }// end of if in select->0

        if (The_Select == 1) {
            switch (The_Month) {
                case 1: {

                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 3;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 31) {
                        The_Day = The_Day - 11;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 2: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 20;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 3: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 22;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 4: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    }

                    break;
                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 8;
                        The_Year = The_Year + 621;

                    }

                    break;
                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 8;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 9;
                        The_Year = The_Year + 621;

                    }
                    break;
                }
                case 7: {

                    if (The_Day >= 1 && The_Day <= 8) {
                        The_Day = The_Day + 22;
                        The_Month = 9;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 9 && The_Day <= 30) {
                        The_Day = The_Day - 8;
                        The_Month = 10;
                        The_Year = The_Year + 621;

                    }
                    break;
                }
                case 8: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 10;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 9: {

                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 10: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    }
                    break;

                }
                case 11: {
                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 12 && The_Day <= 30) {
                        The_Day = The_Day - 11;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                case 12: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 19;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 3;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                default: {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be YYYY/MM/DD");
                    return null;
                }
            }
        }// end of if in select->1

        if (The_Select == 2) {
            switch (The_Month) {
                case 1: {

                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 3;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 31) {
                        The_Day = The_Day - 11;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 2: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 20;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 3: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 4: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    }

                    break;
                }

                case 5: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 8;
                        The_Year = The_Year + 621;

                    }

                    break;
                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 8;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 9;
                        The_Year = The_Year + 621;

                    }
                    break;
                }

                case 7: {

                    if (The_Day >= 1 && The_Day <= 8) {
                        The_Day = The_Day + 22;
                        The_Month = 9;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 9 && The_Day <= 30) {
                        The_Day = The_Day - 8;
                        The_Month = 10;
                        The_Year = The_Year + 621;

                    }
                    break;
                }
                case 8: {
                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 22;
                        The_Month = 10;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 9: {

                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    }

                    break;
                }
                case 10: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                case 11: {
                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 12 && The_Day <= 30) {
                        The_Day = The_Day - 11;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                case 12: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 19;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 3;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                default: {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be YYYY/MM/DD");
                    return null;
                }
            }
        }// end of if in select->2

        if (The_Select == 3) {
            switch (The_Month) {
                case 1: {

                    if (The_Day >= 1 && The_Day <= 12) {
                        The_Day = The_Day + 19;
                        The_Month = 3;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 13 && The_Day <= 31) {
                        The_Day = The_Day - 12;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    }
                    break;
                }

                case 2: {

                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 19;
                        The_Month = 4;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 31) {
                        The_Day = The_Day - 11;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 3: {
                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 5;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 31) {
                        The_Day = The_Day - 11;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 4: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 20;
                        The_Month = 6;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 9;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    }
                    break;

                }
                case 5: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 7;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 8;
                        The_Year = The_Year + 621;

                    }
                    break;

                }
                case 6: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 8;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 31) {
                        The_Day = The_Day - 10;
                        The_Month = 9;
                        The_Year = The_Year + 621;

                    }
                    break;
                }
                case 7: {

                    if (The_Day >= 1 && The_Day <= 9) {
                        The_Day = The_Day + 21;
                        The_Month = 9;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 10 && The_Day <= 30) {
                        The_Day = The_Day - 9;
                        The_Month = 10;
                        The_Year = The_Year + 621;

                    }
                    break;
                }
                case 8: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 21;
                        The_Month = 10;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 9: {

                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 20;
                        The_Month = 11;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    }
                    break;
                }
                case 10: {

                    if (The_Day >= 1 && The_Day <= 11) {
                        The_Day = The_Day + 20;
                        The_Month = 12;
                        The_Year = The_Year + 621;
                    } else if (The_Day >= 12 && The_Day <= 30) {
                        The_Day = The_Day - 11;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                case 11: {
                    if (The_Day >= 1 && The_Day <= 12) {
                        The_Day = The_Day + 19;
                        The_Month = 1;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 13 && The_Day <= 30) {
                        The_Day = The_Day - 12;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                case 12: {
                    if (The_Day >= 1 && The_Day <= 10) {
                        The_Day = The_Day + 18;
                        The_Month = 2;
                        The_Year = The_Year + 622;
                    } else if (The_Day >= 11 && The_Day <= 30) {
                        The_Day = The_Day - 10;
                        The_Month = 3;
                        The_Year = The_Year + 622;
                    }
                    break;
                }
                default: {
                    System.out
                            .println("Seems the given date is wrong\n The Format should be YYYY/MM/DD");
                    return null;
                }
            }
        }// end if
        // output of english date format
        English_Date = The_Day + "/" + The_Month + "/" + The_Year;
        return English_Date;
    }// end of Function En->Fa

    private static String getPersianCurrentDate() {
        String date = getPersianDate(DateHelper.getCurrentDate("yyyy/MM/dd"));
        return date;
    }

    /*public static String getCurrentTime() {
        Date date = new Date();
        String hours = date.getHours() < 10 ? "0" + date.getHours() : "" + date.getHours();
        String min = date.getMinutes() < 10 ? "0" + date.getMinutes() : "" + date.getMinutes();
        String sec = date.getSeconds() < 10 ? "0" + date.getSeconds() : "" + date.getSeconds();
        return hours + min + sec;

    }*/
//	private static String getCurrentTime() {
//		Date date = new Date();
//		String hours=date.getHours()<10?"0"+date.getHours():""+date.getHours(); 
//		String min=date.getMinutes()<10?"0"+date.getMinutes():""+date.getMinutes();
//		String sec=date.getSeconds()<10?"0"+date.getSeconds():""+date.getSeconds();
//		return  hours+":"+min+":"+sec;
//
//	}

//	public static String getTimeAndDate(){
//		String tmp;
//		tmp=getPersianCurrentDate().replace("/", "");
//		tmp+=getCurrentTime().replace(":", "");
//		return tmp;
//	}
}
