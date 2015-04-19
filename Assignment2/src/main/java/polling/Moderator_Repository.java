package polling;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Service;

import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "example", path = "api/v1/moderators")
interface Moderator_Repository extends MongoRepository<Moderator, String> {


}
