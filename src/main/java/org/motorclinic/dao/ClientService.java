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


    public boolean saveNewClient(Client newClient) {



        if (!repository.existsByPhone(newClient.getPhone())) {
            repository.save(newClient);
            return true;
        } else {
            //    logger.info("\uD83D\uDD34 ПОЛЬЗОВАТЕЛЬ С ТАКИМ НОМЕРОМ УЖЕ СЩЕСТВУЕТ : {}", newClient.getPhone());
            return false;
        }
    }
}
