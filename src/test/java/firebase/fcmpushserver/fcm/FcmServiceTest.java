package firebase.fcmpushserver.fcm;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FcmServiceTest {

    @Autowired
    FcmService fcmService;

    @Value("${fcm.androidTestToken}")
    String token;

    @Test
    public void pushTest() throws ExecutionException, InterruptedException {
        fcmService.sendMessage(token, "title", "body");
    }

}