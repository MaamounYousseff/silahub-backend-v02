import com.example.test.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {com.example.test.TestConfig.class}) // include config
@ComponentScan(basePackages = "com.example.test")
public class TestRestControllerTest {

    @Autowired
    private Producer messageProducer;
    @Autowired
    private SampleTableMongoRepository sampleTableMongoRepository;
    @Autowired
    private SampleTableJpaRepository sampleTableJpaRepository;


    //    @RepeatedTest(100000) // run this test 1000 times
    public void sendMessageRepeatedly() throws InterruptedException {
        // optional: small delay to ensure broker is fully started

        String message = "Hello from ActiveMQ!";
        messageProducer.sendMessage(message);
        System.out.println("Sent: " + message);
    }

/*
* TEST NO INDEX :
* COL1 AND COL20 IS NOT INDEXED
* */
    @Test
    void SampleTableMongoRepositoryFindByCol1() {
// worst case last row
        long start = System.currentTimeMillis();
        var result = sampleTableMongoRepository.findByCol1(377987L);
        long end = System.currentTimeMillis();
        System.out.println("mongo findByCol1 took: " + (end - start) + " ms");
        return;
    }

    @Test
    void SampleTableJpaRepositoryFindByCol1() {
// worst case last row
        long start = System.currentTimeMillis();
        var result = this.sampleTableJpaRepository.findByCol1(377987L);
        long end = System.currentTimeMillis();
        System.out.println("JPA findByCol1 took: " + (end - start) + " ms");
        return;
    }
    @Test
    void SampleTableMongoRepositoryFindByCol20() {
// worst case last row
        long start = System.currentTimeMillis();
        var result = sampleTableMongoRepository.findByCol20(470636L);
        long end = System.currentTimeMillis();
        System.out.println("mongo findByCol20 took: " + (end - start) + " ms");
        return;
    }

    @Test
    void SampleTableJpaRepositoryFindByCol20() {
// worst case last row
        long start = System.currentTimeMillis();
        var result = this.sampleTableJpaRepository.findByCol20(470636L);
        long end = System.currentTimeMillis();
        System.out.println("JPA findByCol20 took: " + (end - start) + " ms");
        return;
    }

    /*
    TEST PRIMARY INDEX  INDEX : SEQUENTIAL + UNIQUE(KEY) =>>> ID SEQUENTIAL
    */

    @Test
    void SampleTableMongoRepositoryFindById()
    {
// worst case last row
        long start = System.currentTimeMillis();
//        var result = this.sampleTableMongoRepository.findById(500000L); // ❌ only works for _id
        var result = this.sampleTableMongoRepository.findByIdEquals(500000L);
        long end = System.currentTimeMillis();
        System.out.println("Mongo findByCol20 took: " + (end - start) + " ms");
        return;
    }

    @Test
    void SampleTableJpaRepositoryFindById()
    {
// worst case last row
        long start = System.currentTimeMillis();
        var result = this.sampleTableJpaRepository.findById(500000L);
        long end = System.currentTimeMillis();
        System.out.println("JPA findByCol20 took: " + (end - start) + " ms");
        return;
    }

}



