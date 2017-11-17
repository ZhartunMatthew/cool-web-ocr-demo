package com.itech.ocr.main;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;

/**
 * A sample application that runs multiple requests against the Drive API. The requests this sample
 * makes are:
 * <ul>
 * <li>Does a resumable media upload</li>
 * <li>Updates the uploaded file by renaming it</li>
 * <li>Does a resumable media download</li>
 * <li>Does a direct media upload</li>
 * <li>Does a direct media download</li>
 * </ul>
 *
 * @author rmistry@google.com (Ravi Mistry)
 */
public class UploadToGoogleDrive {

    /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "";

    private static final String UPLOAD_FILE_PATH = "D:\\server\\out.xlsx";
    private static final String DIR_FOR_DOWNLOADS = "D:\\server";
    private static final java.io.File UPLOAD_FILE = new java.io.File(UPLOAD_FILE_PATH);

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR =
            new java.io.File(System.getProperty("user.home"), ".store/drive_sample");

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
     * globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /** Global Drive API client. */
    private static Drive drive;

    /** Authorizes the installed application to access user's protected data. */
    private static Credential authorize() throws Exception {
        // load client secrets
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(UploadToGoogleDrive.class.getResourceAsStream("/client_secret.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                            + "into drive-cmdline-sample/src/main/resources/client_secret.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public static void main(String[] args) throws IOException {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize();
            // set up the global Drive instance
            drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                    APPLICATION_NAME).build();

            setPermission("1Y7lEcAijhd73wOW_hqMCl09R3M_FBP6b", drive);
            if(true) return;
            File uploadedFile = uploadFile(true);
            System.out.println(uploadedFile.getId());


            //View.header1("Success!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    public static String upload() {
        Preconditions.checkArgument(
                !UPLOAD_FILE_PATH.startsWith("Enter ") && !DIR_FOR_DOWNLOADS.startsWith("Enter "),
                "Please enter the upload file path and download directory in %s", UploadToGoogleDrive.class);

        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            // authorization
            Credential credential = authorize();
            // set up the global Drive instance
            drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                    APPLICATION_NAME).build();

            // run commands

            //View.header1("Starting Resumable Media Upload");
            //File uploadedFile = uploadFile(false);

            //View.header1("Updating Uploaded File Name");
            //File updatedFile = updateFileWithTestSuffix(uploadedFile.getId());

            //View.header1("Starting Resumable Media Download");
            //downloadFile(false, updatedFile);

            //View.header1("Starting Simple Media Upload");
            File uploadedFile = uploadFile(true);
            System.out.println(uploadedFile.getId());
            setPermission(uploadedFile.getId(), drive);
            return uploadedFile.getId();
            //View.header1("Starting Simple Media Download");
            //downloadFile(true, uploadedFile);

            //View.header1("Success!");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(1);
        return null;
    }

    /** Uploads a file using either resumable or direct media upload. */
    private static File uploadFile(boolean useDirectUpload) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(UPLOAD_FILE.getName());

        FileContent mediaContent = new FileContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", UPLOAD_FILE);

        Drive.Files.Create insert = drive.files().create(fileMetadata, mediaContent);
        MediaHttpUploader uploader = insert.getMediaHttpUploader();
        uploader.setDirectUploadEnabled(useDirectUpload);
        //uploader.setProgressListener();
        return insert.execute();
    }

    private static void setPermission(String id, Drive drive) throws IOException {
        String fileId = id;
        JsonBatchCallback<Permission> callback = new JsonBatchCallback<Permission>() {
            @Override
            public void onFailure(GoogleJsonError e,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                // Handle error
                System.err.println(e.getMessage());
            }

            @Override
            public void onSuccess(Permission permission,
                                  HttpHeaders responseHeaders)
                    throws IOException {
                System.out.println("Permission ID: " + permission.getId());
            }
        };
        BatchRequest batch = drive.batch();

        Permission domainPermission = new Permission()
                .setType("anyone")
                .setRole("reader");
        drive.permissions().create(fileId, domainPermission)
                .setFields("id")
                .queue(batch, callback);

        batch.execute();
    }

   /* *//** Updates the name of the uploaded file to have a "drivetest-" prefix. *//*
    private static File updateFileWithTestSuffix(String id) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setTitle("drivetest-" + UPLOAD_FILE.getName());

        Drive.Files.Update update = drive.files().update(id, fileMetadata);
        return update.execute();
    }*/

    /** Downloads a file using either resumable or direct media download. *//*
    private static void downloadFile(boolean useDirectDownload, File uploadedFile)
            throws IOException {
        // create parent directory (if necessary)
        java.io.File parentDir = new java.io.File(DIR_FOR_DOWNLOADS);
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("Unable to create parent directory");
        }
        OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getTitle()));

        MediaHttpDownloader downloader =
                new MediaHttpDownloader(httpTransport, drive.getRequestFactory().getInitializer());
        downloader.setDirectDownloadEnabled(useDirectDownload);
        downloader.setProgressListener(new FileDownloadProgressListener());
        downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
    }*/
}