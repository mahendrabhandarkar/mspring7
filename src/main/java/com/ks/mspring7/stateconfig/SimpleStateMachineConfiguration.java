package com.ks.mspring7.stateconfig;

import com.ks.mspring7.stateenum.OrderStates;
import com.ks.mspring7.stateevents.OrderEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@EnableStateMachineFactory
class SimpleStateMachineConfiguration extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

	@Override
	public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states) throws Exception {
		states.withStates()
				.initial(OrderStates.SUBMITTED)
				.state(OrderStates.PAID)
				.end(OrderStates.FULFILLED)
				.end(OrderStates.CANCELLED);
	}
	
	@Override
	public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
		transitions
		   .withExternal()
		   .source(OrderStates.SUBMITTED)
		   .target(OrderStates.PAID)
		   .event(OrderEvents.PAY)
		   .guard(ctx -> {
			   log.info(" true -> statechanged. false -> do not change ");
			   var paymentType = String.class.cast(ctx.getExtendedState()
					   .getVariables().get("paymentType"));
			   if (!StringUtils.isEmpty(paymentType) && paymentType.equals("cod"))
				   return false;
			   else return true;
		   })

		   .and()
		   .withExternal()
		   .source(OrderStates.PAID)
		   .target(OrderStates.FULFILLED)
		   .event(OrderEvents.FULFILL)
		   .action(ctx -> {
			 log.info("This PAID handler where we can perform some logging");
		   })

		   .and()
		   .withExternal()
		   .source(OrderStates.SUBMITTED)
		   .target(OrderStates.CANCELLED)
		   .event(OrderEvents.CANCEL)
		   .action(ctx -> {
			 log.info("This SUBMITTED handler where we can perform some logging");
		   })

		   .and()
		   .withExternal()
		   .source(OrderStates.PAID)
		   .target(OrderStates.CANCELLED)
		   .event(OrderEvents.CANCEL)
		   .action(ctx -> {
			   log.info("This PAID handler where we can perform some logging");
		   });
	}
	@Bean
	public Guard<OrderStates, OrderEvents> guard() {
	   return ctx -> true;
	}
/*
	@Bean
	public Action<OrderStates, OrderEvents> guard() {
		return ctx -> log.info("logging");
	}
*/
}
