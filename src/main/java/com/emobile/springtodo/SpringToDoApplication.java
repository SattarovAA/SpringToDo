package com.emobile.springtodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringToDoApplication {
    /**
     * Возникли вопросы:
     * com.emobile.springtodo.service.impl.UserServiceImpl#update(User)
     * Непонятно нужно ли в таких случаях использовать self.findById(id).
     * Откатывать транзакцию только после чтения не имеет смысла...
     * Здесь используется для AOP logger-а.
     * com.emobile.springtodo.service.impl.UserServiceImpl#checkDuplicateUsername(String)
     * По большому счету подобные методы должны быть private.
     * Откатывать транзакцию только после чтения не имеет смысла...
     * Стоит ли их делать public ради работы @Transactional (вероятно нет).
     * Стоит ли их делать public ради работы logger-а (вероятно нет).
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringToDoApplication.class, args);
    }

}
