package org.motorclinic.dao;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository repository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public void saveNewClient(Client newClient) {

        if (!repository.existsByPhone(newClient.getPhone())) {
            repository.save(newClient);
            logger.info("\uD83D\uDFE2 КЛИЕНТ УСПЕШНО СОХРАНЕН ID НОВОГО КЛИЕНТА : {}", newClient.getId());
        } else {
            logger.info("\uD83D\uDD34 ПОЛЬЗОВАТЕЛЬ С ТАКИМ НОМЕРОМ УЖЕ СЩЕСТВУЕТ : {}", newClient.getPhone());
        }
    }
}
