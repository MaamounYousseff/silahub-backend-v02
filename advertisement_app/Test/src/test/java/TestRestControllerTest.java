import com.example.test.Producer;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest(classes = {com.example.test.TestConfig.class}) // include config
@ComponentScan(basePackages = "com.example.test")
public class TestRestControllerTest {

    @Autowired
    private Producer messageProducer;

    @RepeatedTest(100000) // run this test 1000 times
    public void sendMessageRepeatedly() throws InterruptedException {
        // optional: small delay to ensure broker is fully started

        String message = "Hello from ActiveMQ!";
        messageProducer.sendMessage(message);
        System.out.println("Sent: " + message);
    }
}
