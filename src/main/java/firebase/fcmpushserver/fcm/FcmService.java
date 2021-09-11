package firebase.fcmpushserver.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    @Value("${fcm.path}")
    private String path;

    /**
     * 파이어베이스 초기화 서버
     * 비공개 키(json)파일을 이용해 초기화 단계를 거친다.
     */
    @PostConstruct
    public void initFirebase() {

        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(path).getInputStream())).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.info("error={}",e.getMessage());
        }
    }

    /**
     * 메시지 전송(유니캐스트)
     */
    public void sendMessage(String token, String title, String body) throws ExecutionException, InterruptedException {
        Message message = makeMessage(token, title, body);

        //실제로 메시지가 보내지는 부분
        FirebaseMessaging.getInstance().sendAsync(message).get();
    }

    /**
     *  파이어베이스 Message 객체를 이용하여 메세지 생성
     *  setToken -> 각 기기마다 주어지는 토큰들(이 토큰을 이용해서 기기를 식별하고 알림을 보냄)
     *  setNotification -> title, body 를 작성(알림의 내용들)
     */
    private Message makeMessage(String token, String title, String body) {
        return Message.builder()
                .setToken(token)
                .setNotification(new Notification(title, body))
                .build();
    }

    /**
     * 메시지 전송(멀티캐스트)
     */
    public void sendMulticastMessage(List<String> fcmTokens, String title, String body) throws ExecutionException, InterruptedException {
        MulticastMessage multicastMessage = makeMulticastMessage(fcmTokens, title, body);

        //실제로 메시지가 보내지는 부분
        FirebaseMessaging.getInstance().sendMulticastAsync(multicastMessage).get();
    }

    /**
     *  파이어베이스 MulticastMessage 객체를 이용하여 메세지 생성
     *  setAllTokens -> 각 기기마다 주어지는 토큰들을 주입(List<String>)(이 토큰들을 이용해서 기기를 식별하고 알림을 보냄)
     *  setNotification -> title, body 를 작성(알림의 내용들)
     */
    private MulticastMessage makeMulticastMessage(List<String> fcmTokens, String title, String body) {
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(new Notification(title, body))
                .build();
    }

}

