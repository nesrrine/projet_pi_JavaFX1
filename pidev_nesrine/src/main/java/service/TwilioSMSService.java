package service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

/**
 * Service pour l'envoi de SMS via l'API REST de Twilio
 */
public class TwilioSMSService {

    private static final String ACCOUNT_SID = "ACb6e342f0b9fadd679827222f0a9e5599";
    private static final String AUTH_TOKEN = "3d3f3ca72fc714f917f9216efce86270";
    private static final String FROM_NUMBER = "+17756289792";




    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String toNumber, String messageBody) {
        Message message = Message.creator(
                        new PhoneNumber(toNumber),
                        new PhoneNumber(FROM_NUMBER),
                        messageBody)
                .create();
        System.out.println("SMS envoyé avec l'ID: " + message.getSid());


    }
}
