package polling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class Poll{

    @JsonView(View.excludemoderator.class)
    int mid;

    @JsonView(View.results.class)
    String id ;
    @JsonView(View.results.class)
    String question;

    @JsonView(View.results.class)
    String started_at;
    @JsonView(View.results.class)
    String expired_at;
    @JsonView(View.results.class)
    ArrayList<String> choice;
    @JsonView(View.viewwithresults.class)
    ArrayList<Integer> results;

    public Boolean getMsgFlag() {
        return msgFlag;
    }

    public void setMsgFlag(Boolean msgFlag) {
        this.msgFlag = msgFlag;
    }

    @JsonView(View.excludemoderator.class)
    Boolean msgFlag;

    //AtomicInteger count = new AtomicInteger(100000);

    public Poll()
    {
        this.results = new ArrayList<Integer>(Collections.nCopies(2,0));
    }
    /*public void initializeArray()
    {
        Arrays.fill(results,0);
    }*/
    public void setMid(int id)
    {
        this.mid= id;
    }
    public int getMid()
    {
        return mid;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getId()
    {
        return id;
    }
    public String getQuestion()
    {
        return question;
    }
    public void setQuestion(String question)
    {
        this.question = question;
    }
    public void setChoice(ArrayList<String> choice)
    {
        this.choice = choice;
    }
    public ArrayList<String> getChoice()
    {
        return choice;
    }
    public ArrayList<Integer> getResults()
    {
        return results;
    }
    public void setresults(ArrayList<Integer> results)
    {
        this.results = results;
    }
    public void setStarted_at(String date)
    {
        this.started_at = date;
    }
    public String getstarted_at()
    {
        return started_at;
    }
    public void setExpired_at(String expired_at)
    {
        this.expired_at = expired_at;
    }
    public String getExpired_at()
    {
        return expired_at;
    }


}
