package polling;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduleTask  {

    @Autowired
    public static final ModeratorController mc = new ModeratorController();

    @Scheduled(fixedRate = 3000)//for every 5 minutes
    public void checkPollAndMessage() throws UnknownHostException {

        ArrayList<String> result;
        result = mc.getExpiredPolls();

    }
}