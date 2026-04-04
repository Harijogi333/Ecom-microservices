package com.ecommerce.notification;

import com.ecommerce.notification.util.OrderCreatedEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(OrderCreatedEventDto orderEvent)
//    {
//        System.out.println("Event Rreceived "+orderEvent);
//
//        System.out.println(orderEvent.getUserId());
//        System.out.println(orderEvent.getOrderId());
//    }


      @Bean
      public Consumer<OrderCreatedEventDto> orderCreated()
      {
          return event ->{
              log.info("order received with order id: {}",event.getOrderId());
              log.info("order received for the user: {}",event.getUserId());
          };
      }




}
