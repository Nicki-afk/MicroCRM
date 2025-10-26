package org.motorclinic.dao;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@Entity
@Table(name = "clients" , uniqueConstraints = @UniqueConstraint(columnNames = {"PHONE"}))
public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENT")
    private Long id;
    @Column(name = "DATE_CALL") private LocalDateTime callData;
    @Column(name = "NAME")  private String name;
    @Column(name = "PHONE") private String phone;
    @Column(name = "AUTO") private String auto;
    @Column(name = "SOURCE") private String source;
    @Column(name = "DETAIL") private String detail;
    @Column(name = "BOOKING_DATE") private LocalDateTime record;
    @Column(name = "MECHNIC" ) private String mechanic;




    public Client(){}

    public Client(LocalDateTime callData, String name, String phone, String auto, String source, String detail, LocalDateTime record, String mechanic) {
        this.callData = callData;
        this.name = name;
        this.phone = phone;
        this.auto = auto;
        this.source = source;
        this.detail = detail;
        this.record = record;
        this.mechanic = mechanic;
    }

    public LocalDateTime getCallData() {
        return callData;
    }

    public void setCallData(LocalDateTime callData) {
        this.callData = callData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getRecord() {
        return record;
    }

    public void setRecord(LocalDateTime record) {
        this.record = record;
    }

    public String getMechanic() {
        return mechanic;
    }

    public void setMechanic(String mechanic) {
        this.mechanic = mechanic;
    }

    @PrePersist
    public void setDefaults(){

        this.callData = this.callData ==  null ? LocalDateTime.now() : this.callData;
        this.auto = this.auto == null ? "НЕИЗВЕСТНО" : this.auto;
        this.name = this.name == null ? "НЕИЗВЕСТНО" : this.name;
        this.detail = this.detail == null ? "НЕИЗВЕСТНО" : this.detail;
        this.mechanic = this.mechanic == null ? "НЕИЗВЕСТНО" : this.mechanic;
        this.source = this.source == null ? "НЕИЗВЕСТНО" : this.source;

    }

    @Override
    public String toString() {
        return "Client{" +
                "callData=" + callData +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", auto='" + auto + '\'' +
                ", source='" + source + '\'' +
                ", detail='" + detail + '\'' +
                ", record=" + record +
                ", mechanic='" + mechanic + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class ClientBuilder{
        private Logger logger = LoggerFactory.getLogger("ClientBuilderLogger");
        public  Client client = new Client();

        public  ClientBuilder setCallDate(String callDate){
            try {
                client.setCallData(LocalDateTime.parse(callDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            }catch(DateTimeParseException dateTimeParseException){
                logger.warn("⚠\uFE0F ПОЛЕ ДАТА БЫЛО ЗАПОЛНЕНО НЕККОРЕКТНО. ДАТА И ВРЕМЯ БЫЛА ПРОСТАВЛЕНА АВТОМАТИЧЕСКИ");
                client.setCallData(LocalDateTime.now());
            }
            return this;
        }

        public ClientBuilder setName(String name){
            client.setName(name);
            return this;
        }

        public ClientBuilder setPhone(String phone){
            client.setPhone(phone);
            return this;
        }

        public ClientBuilder setAuto(String auto){
            client.setAuto(auto);
            return this;
        }

        public ClientBuilder setSource(String source){
            client.setSource(source);
            return this;
        }

        public ClientBuilder setDetail(String detail){
            client.setDetail(detail);
            return this;
        }
        public ClientBuilder setRecord(String record){
            client.setRecord(LocalDateTime.parse(record , DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
            return this;
        }

        public ClientBuilder setMechanic(String mechanic){
            client.setMechanic(mechanic);
            return this;
        }

        public Client build(){
            return client;
        }



    }


}

