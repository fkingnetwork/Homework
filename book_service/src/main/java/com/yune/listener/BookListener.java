package com.yune.listener;

import com.yune.domain.Inventory;
import com.yune.service.BookService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookListener {
    @Autowired
    private BookService bookService;
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "QueueMashware",durable = "true"),
            exchange = @Exchange(name = "ExchangerMashware"),
            key = "Mashware"
    ))
    public void listen(Inventory inventory){
        //添加锁
        bookService.dropOneStock(inventory);

    }
}
