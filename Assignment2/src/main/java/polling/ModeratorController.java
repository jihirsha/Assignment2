package polling;

import com.fasterxml.jackson.annotation.JsonView;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BSON;
//import org.bson.Document;
import com.mongodb.client.MongoDatabase;
import java.util.List;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;


//import static org.springframework.data.mongodb.core.query.Query;
//import static org.springframework.data.mongodb.core.query.Update;
import javax.validation.Valid;
import java.net.UnknownHostException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class ModeratorController {

    @Autowired
    MongoOperations m_repo;

    AtomicInteger count = new AtomicInteger();
    AtomicInteger count_poll = new AtomicInteger(123456789);
    public static List expire_poll = new ArrayList<Poll>();

    //1. creating moderator
    @RequestMapping(name = "api/v1/moderators", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity method0(@Valid @RequestBody Moderator mod, BindingResult result) {

        List errorlist = new ArrayList();
        Moderator mod_new = new Moderator();

        if (mod.getName() != "" || mod.getName() != null) {

            mod_new.setName(mod.getName());
            mod_new.setEmail(mod.getEmail());
            mod_new.setPassword(mod.getPassword());
            mod_new.id = count.incrementAndGet();
            mod_new.setCreated_at();

            m_repo.save(mod_new);

        }
        if (mod.getName() == "" || mod.getName() == null) {
            String strName = "Name field can not be empty/null";
            errorlist.add(strName);
        }

        if (result.hasErrors() || mod.getName() == "" || mod.getName() == null) {

            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                error = fielderror.getField() + "  " + fielderror.getDefaultMessage();
                errorlist.add(error);
            }
            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<Moderator>(mod_new, HttpStatus.CREATED);
        }
    }
    //2. view moderator resource

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "api/v1/moderators/{id}", method = RequestMethod.GET)
    public ResponseEntity<Moderator> findModerator(@PathVariable("id") int id) {

        Query query = new Query(where("id").is(id));
        Moderator mod = m_repo.findOne(query, Moderator.class);
        HttpHeaders header = new HttpHeaders();
        header.add("Accept", "Application/json");
        return new ResponseEntity<Moderator>(mod, header, HttpStatus.OK);
    }

    //3. update moderator
    @RequestMapping(value = "api/v1/moderators/{id}", method = RequestMethod.PUT,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<Moderator> updateModerator(@PathVariable("id") int id, @Valid @RequestBody Moderator m, BindingResult result) {

        Query query = new Query(where("id").is(id));
        Moderator mod = m_repo.findOne(query, Moderator.class);
        mod.setEmail(m.getEmail());
        mod.setPassword(m.getPassword());

        if (result.hasErrors()) {
            List errorlist = new ArrayList();
            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                /*if(fielderror.getField().compareTo("name")==0)
                {
                    continue;
                }*/
                error = fielderror.getField() + "  " + fielderror.getDefaultMessage();
                errorlist.add(error);

            }
            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            m_repo.save(mod);
            return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
        }
    }

    //4. create a poll
    @JsonView(View.results.class)
    @RequestMapping(value = "api/v1/moderators/{id}/polls", method = RequestMethod.POST)
    public ResponseEntity<Poll> addNewPoll(@PathVariable("id") int id, @Valid @RequestBody Poll poll, BindingResult result) {

        Poll p = new Poll();
        p.setQuestion(poll.getQuestion());
        p.setChoice(poll.getChoice());
        p.setStarted_at(poll.getstarted_at());
        p.setExpired_at(poll.getExpired_at());
        p.id = Integer.toString(count_poll.incrementAndGet(), 36);
        p.setMid(id);
        p.setMsgFlag(false);


        if (result.hasErrors()) {
            List errorlist = new ArrayList();
            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                error = fielderror.getField() + " " + fielderror.getDefaultMessage();
                errorlist.add(error);
            }

            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            Query query = new Query(where("id").is(id));
            Update update = new Update().addToSet("Polls", p);

            m_repo.save(p);
            return new ResponseEntity<Poll>(p, HttpStatus.CREATED);
        }
    }
    //5. View a poll without result

    @JsonView(View.results.class)
    @RequestMapping(value = "api/v1/polls/{id}", method = RequestMethod.GET)
    public ResponseEntity<Poll> viewPoll(@PathVariable("id") String id) {

        Query query = new Query(where("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    //6. View a poll with result
    @JsonView(View.viewwithresults.class)
    @RequestMapping(value = "api/v1/moderators/{moderator_id}/polls/{id}", method = RequestMethod.GET)
    public ResponseEntity viewPollWithResults(@PathVariable("moderator_id") int moderator_id, @PathVariable("id") String id) {

        Query query = new Query(where("mid").is(moderator_id).and("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    //7.List all polls
    @JsonView(View.viewwithresults.class)
    @RequestMapping(value = "api/v1/moderators/{id}/polls", method = RequestMethod.GET)
    public ResponseEntity viewPoll(@PathVariable("id") int id) {

        List all_Polls = new ArrayList();
        Query query = new Query(where("mid").is(id));
        query.fields().exclude("mid");
        all_Polls = m_repo.find(query, Poll.class);
        return new ResponseEntity(all_Polls, HttpStatus.OK);
    }

    //8.Delete a poll
    @RequestMapping(value = "api/v1/moderators/{moderator_id}/polls/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deletePoll(@PathVariable("id") String id, @PathVariable("moderator_id") int mid) {

        Query query = new Query(where("mid").is(mid).and("_id").is(id));
        m_repo.remove(query, Poll.class);
        return new ResponseEntity("", HttpStatus.NO_CONTENT);
    }
    //9.Vote a poll

    @RequestMapping(value = "api/v1/polls/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity doVote(@RequestParam("choice") int choice, @PathVariable("id") String id) {

        Query query = new Query(where("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);

        if (p != null) {

            ArrayList<Integer> results = p.getResults();
            int toIncrement = results.get(choice);
            results.set(choice, ++toIncrement);
            p.setresults(results);
            m_repo.save(p);
            return new ResponseEntity("", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity("Not valid Poll", HttpStatus.BAD_REQUEST);


    }

    /* Get List of Expired Polls and send message to KafkaServer*/
    public ArrayList<String> getExpiredPolls() throws UnknownHostException{
        ArrayList<String> arr = new ArrayList<String>();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("test");
        DBCollection collection = db.getCollection("poll");
        DBCollection mcollection = db.getCollection("moderator");

        try {
            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                BasicDBObject mObj = (BasicDBObject) cursor.next();
                String string = mObj.getString("expired_at");
                Boolean getMsgFlag = mObj.getBoolean("msgFlag");

                Boolean result = DateComparison.checkExpire(string);
                if (!result && !getMsgFlag) {
                    mObj.replace("msgFlag",false,true);
                    int mid = mObj.getInt("mid");
                    BasicDBObject whereQuery = new BasicDBObject();
                    whereQuery.put("_id", mid);
                    BasicDBObject fields = new BasicDBObject();
                    fields.put("email",4);

                    DBCursor cursor1 = mcollection.find(whereQuery, fields);
                    String email = "";
                    while (cursor1.hasNext()) {

                        BasicDBObject objTemp = (BasicDBObject) cursor1.next();
                        email = objTemp.getString("email");

                    }
                    String str = email + ":010030943:Poll Result [";
                    List<BasicDBObject> resultObj = (List<BasicDBObject>) mObj.get("results");
                    List<BasicDBObject> choiceObj = (List<BasicDBObject>) mObj.get("choice");
                    for (int i = 0; i < choiceObj.size(); i++) {
                        str = str + choiceObj.get(i) + "=" + resultObj.get(i) + ",";
                    }
                    str = str.substring(0, str.length() - 1);
                    str = str + "]";
                    arr.add(str);
                    System.out.println(arr);
                    //KafkaProducer kp = new KafkaProducer();
                    //kp.sendMessage(str);

                }
            }
        }
        catch (Exception e) {

        }
        return arr;
    }
}

