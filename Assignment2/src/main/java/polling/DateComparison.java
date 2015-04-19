package polling;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateComparison {

    public static Boolean checkExpire(String dateInString)
    {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.format(currentDate);
        Boolean result= false;

        try {

             Date date = formatter.parse(dateInString);
             result = date.after(currentDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    }


