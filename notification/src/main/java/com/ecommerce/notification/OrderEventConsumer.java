package com.ecommerce.notification;

import com.ecommerce.notification.util.OrderCreatedEventDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OrderEventConsumer {

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void handleOrderEvent(OrderCreatedEventDto orderEvent)
    {
        System.out.println("Event Rreceived "+orderEvent);

        System.out.println(orderEvent.getUserId());
        System.out.println(orderEvent.getOrderId());
    }
}
