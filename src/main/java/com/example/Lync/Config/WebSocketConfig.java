package com.example.Lync.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration
    @EnableWebSocketMessageBroker
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic");
            config.setApplicationDestinationPrefixes("/app");
        }

//        @Override
//        public void registerStompEndpoints(StompEndpointRegistry registry) {
//            registry.addEndpoint("/ws")
//                    .setAllowedOriginPatterns(
//                            "http://localhost:5173",
//                            "http://lyncorganikness.ap-south-1.elasticbeanstalk.com",
//                            "http://lync-reactjs-bucket.s3-website.ap-south-1.amazonaws.com",
//                            "http://buyerwebportal.s3-website.ap-south-1.amazonaws.com").withSockJS();
//
////            registry.addEndpoint("/ws").setAllowedOrigins("https://your-react-app.com", "https://your-flutter-app.com").withSockJS();
//
//        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("*").withSockJS();
        }
    }

