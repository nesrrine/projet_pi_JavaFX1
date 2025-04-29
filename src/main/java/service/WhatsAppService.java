package service;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class WhatsAppService {
    
    private static final String API_URL = "https://graph.facebook.com/v22.0/";
    private static final String PHONE_NUMBER_ID = "616011074932752"; // Replace with your WhatsApp Business phone number ID
    private static final String ACCESS_TOKEN = "EAAJ0Rikhh0sBO0c38eZCdPhm3fEUotZAGyXWbj9TdRUqnH4pMyFoh7YTnK0btXSVLhae0GpHk2TmvZB9cBKkVEKUUEWrwR95Hfnv2ctFD6jCZChQZAfnmeARqchcyAulaOMgcaUItNlKELJRCH1EBd9HQcC62qvHzyrC6OOzy2WbZCZC1KViZAF00AfS4TZBVygQSwBafWP5asmLqcRR0WHB4r9zSGdAZD"; // Replace with your WhatsApp Business API access token
    
    private final OkHttpClient client = new OkHttpClient();
    
    /**
     * Sends a WhatsApp message to the specified phone number using a template
     * 
     * @param phoneNumber The recipient's phone number (with country code, no + or spaces)
     * @param message The message to send (not used with templates, but kept for compatibility)
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendMessage(String phoneNumber, String message) {
        try {
            // Create the JSON payload for a template message
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("messaging_product", "whatsapp");
            jsonBody.put("to", phoneNumber);
            jsonBody.put("type", "template");
            
            // Create the template object
            JSONObject templateObject = new JSONObject();
            templateObject.put("name", "hello_world"); // Use your approved template name
            
            // Set the language
            JSONObject languageObject = new JSONObject();
            languageObject.put("code", "en_US");
            templateObject.put("language", languageObject);
            
            // Add template to the main JSON body
            jsonBody.put("template", templateObject);
            
            System.out.println("Sending WhatsApp message to: " + phoneNumber);
            System.out.println("Request payload: " + jsonBody.toString());
            
            // Create the request
            RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                .url(API_URL + PHONE_NUMBER_ID + "/messages")
                .post(body)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();
            
            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    System.out.println("WhatsApp message sent successfully. Response: " + responseBody);
                    return true;
                } else {
                    System.err.println("Failed to send WhatsApp message: " + response.code() + " " + response.message());
                    if (response.body() != null) {
                        String errorBody = response.body().string();
                        System.err.println("Error response: " + errorBody);
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending WhatsApp message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sends a WhatsApp message with a custom template and parameters
     * 
     * @param phoneNumber The recipient's phone number (with country code, no + or spaces)
     * @param templateName The name of the approved template
     * @param parameters Array of parameters to fill in the template
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendTemplateMessage(String phoneNumber, String templateName, String[] parameters) {
        try {
            // Create the JSON payload for a template message
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("messaging_product", "whatsapp");
            jsonBody.put("to", phoneNumber);
            jsonBody.put("type", "template");
            
            // Create the template object
            JSONObject templateObject = new JSONObject();
            templateObject.put("name", templateName);
            
            // Set the language
            JSONObject languageObject = new JSONObject();
            languageObject.put("code", "en_US");
            templateObject.put("language", languageObject);
            
            // Add parameters if provided
            if (parameters != null && parameters.length > 0) {
                org.json.JSONArray componentsArray = new org.json.JSONArray();
                JSONObject componentObject = new JSONObject();
                componentObject.put("type", "body");
                
                org.json.JSONArray parametersArray = new org.json.JSONArray();
                for (String parameter : parameters) {
                    JSONObject parameterObject = new JSONObject();
                    parameterObject.put("type", "text");
                    parameterObject.put("text", parameter);
                    parametersArray.put(parameterObject);
                }
                
                componentObject.put("parameters", parametersArray);
                componentsArray.put(componentObject);
                templateObject.put("components", componentsArray);
            }
            
            // Add template to the main JSON body
            jsonBody.put("template", templateObject);
            
            System.out.println("Sending WhatsApp template message to: " + phoneNumber);
            System.out.println("Request payload: " + jsonBody.toString());
            
            // Create the request
            RequestBody body = RequestBody.create(
                jsonBody.toString(),
                MediaType.parse("application/json")
            );
            
            Request request = new Request.Builder()
                .url(API_URL + PHONE_NUMBER_ID + "/messages")
                .post(body)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();
            
            // Execute the request
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    System.out.println("WhatsApp template message sent successfully. Response: " + responseBody);
                    return true;
                } else {
                    System.err.println("Failed to send WhatsApp template message: " + response.code() + " " + response.message());
                    if (response.body() != null) {
                        String errorBody = response.body().string();
                        System.err.println("Error response: " + errorBody);
                    }
                    return false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error sending WhatsApp template message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Formats a phone number for the WhatsApp API
     * 
     * @param phoneNumber The phone number to format
     * @return The formatted phone number
     */
    public String formatPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        
        // If the phone number doesn't start with a country code, add Tunisia's country code (216)
        if (!phoneNumber.startsWith("+")) {
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "216" + phoneNumber.substring(1);
            } else {
                phoneNumber = "216" + phoneNumber;
            }
        } else {
            // If it has a plus, remove it
            phoneNumber = phoneNumber.substring(1);
        }
        
        return phoneNumber;
    }
}
