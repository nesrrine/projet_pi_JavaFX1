package utils; // this should match the folder structure

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.Map;

public class CloudinaryUploader {
    public static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dzd00atxb",
            "api_key", "568478321695671",
            "api_secret", "hM37vZ6eGDW7uCrxxOo8719wGKk"));

    public static String uploadImage(String filePath) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(filePath, ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }

    public static String uploadVideo(String filePath) throws Exception {
        Map uploadResult = cloudinary.uploader().upload(filePath, ObjectUtils.asMap(
                "resource_type", "video"));
        return (String) uploadResult.get("secure_url");
    }
}
