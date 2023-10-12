package com.ks.mspring7.controller;

import com.ks.mspring7.entity.OrderInvoice;
import com.ks.mspring7.repo.OrderRepository;
import com.ks.mspring7.stateenum.OrderStates;
import com.ks.mspring7.stateevents.OrderEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WorkflowController {
	@Autowired
	private final OrderRepository orderRepository;
	
	@Autowired
	private final StateMachineFactory<OrderStates, OrderEvents> stateMachineFactory;

   @PutMapping("/change")
   public String changeState(@RequestBody OrderInvoice order){

        //making the machine in current state of the order
         StateMachine<OrderStates, OrderEvents> sm =    build(order);
         sm.getExtendedState().getVariables().put("paymentType",order.getPaymentType());
         sm.sendEvent(
                 MessageBuilder.withPayload(OrderEvents.valueOf(order.getEvent()))
                         .setHeader("orderId",order.getId())
                         .setHeader("state",order.getState())
                         .build()
                 );
        return "state changed";
    }
	
	/*
	*
	* (Most important step)
	*
	*
	*/
	public StateMachine<OrderStates,OrderEvents> build(final OrderInvoice orderDto){
	   var orderDb =  this.orderRepository.findById(orderDto.getId());
	   var stateMachine =  this.stateMachineFactory.getStateMachine(orderDto.getId().toString());
	   stateMachine.stop();
	   stateMachine.getStateMachineAccessor()
		  .doWithAllRegions(sma -> {
			sma.resetStateMachine(new DefaultStateMachineContext<>(OrderStates.valueOf(orderDb.get().getState()), null, null, null));
		 });
		stateMachine.start();
		return stateMachine;
	}
	
	/*
	* Data Fetching from DB.
	* we have added the addStateMAchineInterceptorAdaptor and override preStateChange method.
	*
	*/
	public StateMachine<OrderStates,OrderEvents> build(final OrderInvoice orderDto){
	   var orderDb =  this.orderRepository.findById(orderDto.getId());
	   var stateMachine =  this.stateMachineFactory.getStateMachine(orderDto.getId().toString());
	   stateMachine.stop();
	   stateMachine.getStateMachineAccessor()
			   .doWithAllRegions(sma -> {
				   sma.addStateMachineInterceptor(new StateMachineInterceptorAdapter<>() {
					   @Override
					   public void preStateChange(State<OrderStates, OrderEvents> state, Message<OrderEvents> message, Transition<OrderStates, OrderEvents> transition, StateMachine<OrderStates, OrderEvents> stateMachine, StateMachine<OrderStates, OrderEvents> rootStateMachine) {
						  var orderId = Long.class.cast(message.getHeaders().get("orderId"));
						  var order =  orderRepository.findById(orderId);
						   if(order.isPresent()){
							   order.get().setState(state.getId().name());
							   orderRepository.save(order.get());
						   }
					   }
				   });
				   sma.resetStateMachine(new DefaultStateMachineContext<>(OrderStates.valueOf(orderDb.get().getState()), null, null, null));
			   });

		stateMachine.start();
		return stateMachine;
	}
 }