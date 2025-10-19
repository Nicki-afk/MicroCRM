package org.motorclinic.google_api;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import jakarta.annotation.PostConstruct;
import org.motorclinic.dao.Client;
import org.motorclinic.dao.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class GoogleSheets {


    private Logger logger = LoggerFactory.getLogger(GoogleSheets.class);
    @Autowired private ClientService clientService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final String spreadsheetId = "1d1kfu-VAFN-Vn5tl21Njc70Gw0hZpmCD4uONrmGZ-Mo";
    final String range = "CLIENT_DATA!A2:ZZZ";


    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private List<List<Object>> values;

    public GoogleSheets() throws GeneralSecurityException, IOException {
    }


    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheets.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }


    @PostConstruct
    public void startService(){

        logger.info("\uD83D\uDFE1 ИНИЦИАЛИЗАЦИЯ СТРУКТУРЫ GOOGLE SHEETS ... ");
        Runnable task = () -> {
            try {
                Sheets service =
                        new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                                .setApplicationName(APPLICATION_NAME)
                                .build();
                logger.info("✔\uFE0F КОМПОНЕНТ СОЗДАН УСПЕШНО.");
                logger.info("🔁 ОТПРАВКА ЗАПРОСА В GOOGLE SHEETS ... ");

                ValueRange response = service.spreadsheets().values()
                        .get(spreadsheetId, range)
                        .execute();
                List<List<Object>> responseValues = response.getValues();

                logger.info("✔\uFE0F ДАННЫЕ УСПЕШНО ПОЛУЧЕНЫ");
              //  System.out.println("🔁 Опрашиваю Google Sheets...");
                this.values = responseValues;
              //  System.out.println("Записей в таблице  : " + values.size());

                logger.info("✔\uFE0F ДАННЫЕ УСПЕШНО ОБНОВЛЕНЫ В ЛОКАЛЬНОЙ ПАМЯТИ , РАЗМЕР CLIENT_CASH : " + this.values.size());

                if(this.values != null){
                    updateOnDb();
                }

            } catch (Exception e) {
                // e.printStackTrace(); // или логгер
                logger.error("\uD83E\uDE7A ПРОИЗОШЛА ОШИБКА ПРИ ОПРОСЕ СЕРВЕРА. ТРЕБУЕТСЯ ВМЕШАТЕЛЬСТВО --> " , e);
            }
        };

        // Запускаем с задержкой 0 и периодом 3 минуты

        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

    }

    private void updateOnDb(){

        int rowCounter = 0;
        logger.info("\uD83D\uDD04 ОБНОВЛЕНИЕ ДАННЫХ В БАЗЕ ...");
        List<Client> clients = generateClientList();


        for (Client client : clients) {
            this.clientService.saveNewClient(client); rowCounter++;
        }


        logger.info("✔\uFE0F ОБНОВЛЕНО СТРОК В БАЗЕ ДАННЫХ : " +  rowCounter);


    }

    public Client generateClient(){


        List<Object> clientData = this.values.get(0);

        return new Client.ClientBuilder()
                .setCallDate(clientData.get(0).toString())
                .setName(clientData.get(1).toString())
                .setPhone(clientData.get(2).toString())
                .setAuto(clientData.get(3).toString())
                .setSource(clientData.get(4).toString())
                .setDetail(clientData.get(5).toString())
                .setRecord(clientData.get(6).toString())
                .setMechanic(clientData.get(7).toString())
                .build();

    }

    public Client generateClient(List<List<Object>> values , int index){


        List<Object> clientData = values.get(index);
        Client client = null;
        try{
            client = new Client.ClientBuilder()
                .setCallDate(clientData.get(0).toString())
                .setName(clientData.get(1).toString())
                .setPhone(clientData.get(2).toString())
                .setAuto(clientData.get(3).toString())
                .setSource(clientData.get(4).toString())
                .setDetail(clientData.get(5).toString())
                .setRecord(clientData.get(6).toString())
                .setMechanic(clientData.get(7).toString())
                .build();

            return client;

        }catch (IndexOutOfBoundsException e){
            logger.error("❌ НЕ УДАЛОСЬ СОХРАНИТЬ ПОЛЬЗОВАТЛЯ , ЗАПОЛНЕНЫ НЕ ВСЕ ПОЛЯ ");
            return null;

        }

//        return new Client.ClientBuilder()
//                .setCallDate(clientData.get(0).toString())
//                .setName(clientData.get(1).toString())
//                .setPhone(clientData.get(2).toString())
//                .setAuto(clientData.get(3).toString())
//                .setSource(clientData.get(4).toString())
//                .setDetail(clientData.get(5).toString())
//                .setRecord(clientData.get(6).toString())
//                .setMechanic(clientData.get(7).toString())
//                .build();

    }


    public List<Client> generateClientList(){

        List<Client> clients = new ArrayList<>(this.values.size());

        for (int x =0;x<this.values.size();x++){
            Client client = generateClient(this.values , x);
            if(client != null){
                clients.add(client);
            }
        }

        return  clients;
    }









}
