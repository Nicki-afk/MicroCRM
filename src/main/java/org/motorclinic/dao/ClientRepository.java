package org.motorclinic.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client , Long> {

     boolean existsByPhone(String phone);



    // boolean updateByPhone(String phone);

}
