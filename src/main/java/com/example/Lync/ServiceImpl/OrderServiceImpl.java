package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.MessageConfig;
import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.OrderDTO;
import com.example.Lync.Entity.*;
import com.example.Lync.Exception.UnauthorizedException;
import com.example.Lync.Repository.*;
import com.example.Lync.Service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private InquiryRepository inquiryRepository;
    private S3Service s3Service;
    private OrderRepository orderRepository;
    private SampleOrderRepository sampleOrderRepository;
    private NotificationRepository notificationRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private ProductRepository productRepository;


    @Override
    public String buyerUploadPurchaseOrder(String qId, MultipartFile file, String buyerId) throws IOException {
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        if (!buyerId.equals(inquiry.getBuyerId())) {
            throw new UnauthorizedException("Unauthorized: The buyer Id does not match with the respective query's buyer Id.");
        }
//        SampleOrder sampleOrder = sampleOrderRepository.findByQId(qId)
//                .orElseThrow(() -> new RuntimeException("Sample Order not found with given Id : " + qId));
        String key = s3Service.buyerUploadPurchaseOrder(qId, file);
        Order order = new Order();
        Long orderCount = orderRepository.countOrderByCurrentDate(LocalDate.now());
        String dateFormatter = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nextOrderNumber = String.format("%03d", orderCount + 1);
        String orderId = "OD" + dateFormatter + nextOrderNumber;

        order.setOId(orderId);
        order.setQId(qId);
        order.setBuyerUId(inquiry.getBuyerId());
        order.setSellerUId(inquiry.getSellerUId());
        order.setPId(inquiry.getProductId());
        order.setProductQuantity(inquiry.getQuantity());
        order.setBuyerFinalPrice(inquiry.getBuyerFinalPrice());
        order.setSellerFinalPrice(inquiry.getSellerFinalPrice());
//        order.setAdminAddressId(sampleOrder.getAdminAddressId());
//        order.setBuyerAddressId(sampleOrder.getBuyerAddressId());
        order.setBuyerPurchaseOrderURL(key);
        order.setBuyerPurchaseOrderURLDate(LocalDate.now());
        order.setBuyerPurchaseOrderURLTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Buyer with ID : " + buyerId + " has uploaded the purchase order for query ID : " + qId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(orderId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);

        return "You uploaded the purchase order successfully";
    }

    @Override
    public String adminUploadPurchaseInvoice(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));

        String key = s3Service.adminUploadPurchaseInvoice(oId, file);

        order.setAdminPurchaseInvoiceURL(key);
        order.setAdminPurchaseInvoiceURLDate(LocalDate.now());
        order.setAdminPurchaseInvoiceURLTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc uploaded Purchase Invoice for query ID : " + order.getQId());
        notification.setBuyerId(order.getBuyerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), notification);
        notificationRepository.save(notification);

        return "You uploaded the purchase invoice successfully";
    }

    @Override
    public String adminUploadPurchaseOrder(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminUploadPurchaseOrder(oId, file);

        order.setAdminPurchaseOrderURL(key);
        order.setAdminPurchaseOrderURLDate(LocalDate.now());
        order.setAdminPurchaseOrderURLTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc uploaded Purchase Order for query ID : " + order.getQId());
        notification.setSellerId(order.getSellerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.SELLER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/seller/" + order.getSellerUId(), notification);
        notificationRepository.save(notification);
        return "You uploaded the purchase order successfully";
    }


    @Override
    public String adminNotifyBuyerToPay(String oId, Double amount) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        order.setAdminNotifyBuyerToPay(amount);
        order.setAdminNotifyBuyerToPayDate(LocalDate.now());
        order.setAdminNotifyBuyerToPayTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc requested you to pay : " + amount + "for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You requested the buyer with Id " + order.getBuyerUId() + " to pay " + amount;
    }

//    @Override
//    public String buyer1stPayment(String oId, String buyerId) {
//        return "";
//    }


    @Override
    public String adminNotifySellerToDispatch(String oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        order.setAdminNotifySellerToDispatch(true);
        order.setAdminNotifySellerToDispatchDate(LocalDate.now());
        order.setAdminNotifySellerToDispatchTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc requested you to dispatch order for Order ID : " + oId);
        notification.setSellerId(order.getSellerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.SELLER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/seller/" + order.getSellerUId(), notification);
        notificationRepository.save(notification);
        return "You notified seller with Id : " + order.getSellerUId() + "to dispatch order with Order Id : " + oId;
    }

    @Override
    public String sellerUploadPurchaseInvoice(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String key = s3Service.sellerPurchaseInvoice(oId, file);
        order.setSellerPurchaseInvoiceURL(key);
        order.setSellerPurchaseInvoiceURLDate(LocalDate.now());
        order.setSellerPurchaseInvoiceURLTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the purchase invoice for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded purchase invoice for Order ID : " + oId;
    }

    @Override
    public String sellerProcessingOrder(String oId, String sellerId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        order.setSellerProcessingOrderDate(LocalDate.now());
        order.setSellerProcessingOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " is processing the product for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You started processing the product with Order ID : " + oId;
    }

    @Override
    public String sellerDispatchedOrder(String oId, String sellerId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        order.setSellerDispatchOrderDate(LocalDate.now());
        order.setSellerDispatchOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has dispatched the product for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You dispatched the product with Order ID : " + oId;
    }

    @Override
    public String sellerAddTransportation(String oId, String sellerId, OrderDTO orderDTO) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        order.setSellerCourierCompany(orderDTO.getSellerCourierCompany());
        order.setSellerOrderTrackerId(orderDTO.getSellerOrderTrackerId());
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has updated transportation details for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You updated the transportation details for Order ID : " + oId;
    }


    @Override
    public String sellerUploadOrderLoadedVehicleImg(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerOrderLoadingVehicleImg(oId, file);
        order.setSellerOrderLoadingVehicleImg(s3Key);
        order.setSellerOrderLoadingVehicleImgDate(LocalDate.now());
        order.setSellerOrderLoadingVehicleImgTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the order loaded vehicle image for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded order loaded vehicle image";
    }

    @Override
    public String sellerUploadLoadedSealedVehicleImg(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerLoadedSealedVehicleImg(oId, file);
        order.setSellerLoadedSealedVehicleImg(s3Key);
        order.setSellerLoadedSealedVehicleImgDate(LocalDate.now());
        order.setSellerLoadedSealedVehicleImgTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the loaded & sealed vehicle image for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded loaded & sealed vehicle image";
    }

    @Override
    public String sellerUploadEWayBill(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerEWayBill(oId, file);
        order.setSellerEWayBill(s3Key);
        order.setSellerEWayBillDate(LocalDate.now());
        order.setSellerEWayBillTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the E-way Bill for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded the E-way bill";
    }

    @Override
    public String sellerUploadPaymentInvoice(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerPaymentInvoice(oId, file);
        order.setSellerPaymentInvoice(s3Key);
        order.setSellerPaymentInvoiceDate(LocalDate.now());
        order.setSellerPaymentInvoiceTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the payment invoice for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded the payment invoice";
    }

    @Override
    public String sellerUploadLRCopy(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerLRCopy(oId, file);
        order.setSellerLRCopy(s3Key);
        order.setSellerLRCopyDate(LocalDate.now());
        order.setSellerLRCopyTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the LR Copy for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You have uploaded the LR Copy";
    }

    @Override
    public String sellerUploadWeightSlipPreLoad(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerWeightSlipPreLoad(oId, file);
        order.setSellerWeightSlipPreLoad(s3Key);
        order.setSellerWeightSlipPreLoadDate(LocalDate.now());
        order.setSellerWeightSlipPreLoadTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the weight slip for the pre-loaded vehicle for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded the weight slip of the pre-loaded vehicle";
    }

    @Override
    public String sellerUploadWeightSlipPostLoad(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerWeightSlipPostLoad(oId, file);
        order.setSellerWeightSlipPostLoad(s3Key);
        order.setSellerWeightSlipPostLoadDate(LocalDate.now());
        order.setSellerWeightSlipPostLoadTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the weight slip for the post-loaded vehicle for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded the weight slip of post-loaded vehicle";
    }

    @Override
    public String adminReceivedOrder(String oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        order.setAdminReceivedOrderDate(LocalDate.now());
        order.setAdminReceivedOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc received the product with Order ID : " + oId);
        notification.setSellerId(order.getSellerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.SELLER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/seller/" + order.getSellerUId(), notification);
        notificationRepository.save(notification);
        return "You received the product with Order Id : " + oId;
    }

    @Override
    public String adminProcessingOrder(String oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        order.setAdminProcessingOrderDate(LocalDate.now());
        order.setAdminProcessingOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc processing the product with Order ID : " + oId);
        notification.setSellerId(order.getBuyerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

        // Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), notification);
        notificationRepository.save(notification);
        return "You started processing the product with Order Id : " + oId;
    }

    @Override
    public String adminDispatchedOrder(String oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        order.setAdminDispatchedOrderDate(LocalDate.now());
        order.setAdminDispatchedOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Lyncc dispatched the product with Order ID : " + oId);
        notification.setSellerId(order.getBuyerUId());
        notification.setIsRead(false);
        notification.setIsAdmin(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setOId(oId);

        // Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), notification);
        notificationRepository.save(notification);
        return "You dispatched the product with Order Id : " + oId;
    }

    @Override
    public String adminUploadEWayBill(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminEWayBill(oId, file);
        order.setAdminEWayBill(key);
        order.setAdminEWayBillDate(LocalDate.now());
        order.setAdminEWayBillTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded E-Way bill for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the E-way bill for Order ID : " + oId;
    }

    @Override
    public String adminUploadPaymentInvoice(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminPaymentInvoice(oId, file);
        order.setAdminPaymentInvoice(key);
        order.setAdminPaymentInvoiceDate(LocalDate.now());
        order.setAdminPaymentInvoiceTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded payment invoice for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the payment invoice for Order ID : " + oId;
    }

    @Override
    public String adminUploadLRCopy(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminLRCopy(oId, file);
        order.setAdminLRCopy(key);
        order.setAdminLRCopyDate(LocalDate.now());
        order.setAdminLRCopyTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded LR copy for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the LR copy for the Order ID : " + oId;
    }

    @Override
    public String adminUploadWeightSlipPreLoad(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminWeightSlipPreLoaded(oId, file);
        order.setAdminWeightSlipPreLoad(key);
        order.setAdminWeightSlipPreLoadDate(LocalDate.now());
        order.setAdminWeightSlipPreLoadTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded weight slip of the pre-loaded vehicle for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the weight slip of the pre-loaded vehicle for the Order ID : " + oId;
    }

    @Override
    public String adminUploadWeightSlipPostLoad(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminWeightSlipPostLoaded(oId, file);
        order.setAdminWeightSlipPostLoad(key);
        order.setAdminWeightSlipPostLoadDate(LocalDate.now());
        order.setAdminWeightSlipPostLoadTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded weight slip of the post-loaded vehicle for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the weight slip of the post-loaded vehicle for the Order ID : " + oId;
    }

    @Override
    public String sellerUploadTransactionCertificate(String oId, String sellerId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!sellerId.equals(order.getSellerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        String s3Key = s3Service.sellerUploadTransactionCertificate(oId, file);
        order.setSellerTransactionCertificate(s3Key);
        order.setSellerTransactionCertificateDate(LocalDate.now());
        order.setSellerTransactionCertificateTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Seller with ID : " + sellerId + " has uploaded the Transaction Certificate for Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You uploaded the Transaction Certificate";
    }

    @Override
    public String adminUploadTransactionCertificate(String oId, MultipartFile file) throws IOException {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        String key = s3Service.adminUploadTransactionCertificate(oId, file);
        order.setAdminTransactionCertificate(key);
        order.setAdminTransactionCertificateDate(LocalDate.now());
        order.setAdminTransactionCertificateTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification noti = new Notification();
        noti.setNotificationId(UUID.randomUUID().toString());
        noti.setMessage("Lyncc uploaded the Transaction Certificate for Order Id : " + oId);
        noti.setBuyerId(order.getBuyerUId());
        noti.setIsRead(false);
        noti.setIsAdmin(false);
        noti.setDate(LocalDate.now());
        noti.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        noti.setOId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.BUYER_ROUTING_KEY, noti);
        messagingTemplate.convertAndSend("/topic/notifications/buyer/" + order.getBuyerUId(), noti);
        notificationRepository.save(noti);
        return "You uploaded the Transaction Certificate";
    }

    @Override
    public String buyerReceivedOrder(String oId, String buyerId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found with given Order Id: " + oId));
        if (!buyerId.equals(order.getBuyerUId())) {
            throw new UnauthorizedException("Unauthorized: Seller ID does not match with the inquiry.");
        }
        order.setBuyerReceivedOrderDate(LocalDate.now());
        order.setBuyerReceivedOrderTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Buyer with ID : " + buyerId + " has received the product with Order ID: " + oId);
        notification.setIsAdmin(true);
        notification.setIsRead(false);
        notification.setDate(LocalDate.now());
        notification.setTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        notification.setSoId(oId);

// Send the notification to the 'notification.queue' with the correct routing key
        rabbitTemplate.convertAndSend(MessageConfig.EXCHANGE, MessageConfig.ADMIN_ROUTING_KEY, notification);
        messagingTemplate.convertAndSend("/topic/notifications", notification);
        notificationRepository.save(notification);
        return "You received the Product";
    }

    @Override
    public List<OrderDTO> buyerGetAllOrders(String buyerId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getBuyerUId().equals(buyerId))
                .map(order -> {
                    OrderDTO orderDTO = new OrderDTO();
                    Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                            .orElseThrow(() -> new RuntimeException("Query Id not found"));
                    Product product = productRepository.findById(inquiry.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
                    orderDTO.setOId(order.getOId());
                    orderDTO.setQId(order.getQId());
                    orderDTO.setProductName(product.getProductName());
                    orderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
                    orderDTO.setFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());
                return orderDTO;
                }).toList();
    }

    @Override
    public OrderDTO buyerGetOrderDetails(String oId, String buyerId) {
         Order order = orderRepository.findById(oId)
                 .orElseThrow(() -> new RuntimeException("Order not found"));
         if (! order.getBuyerUId().equals(buyerId)) {
             throw new UnauthorizedException("Unauthorized: Buyer ID does not match with the inquiry.");
         }
         OrderDTO orderDTO = new OrderDTO();
         orderDTO.setOId(oId);
         orderDTO.setQId(order.getQId());
         orderDTO.setBuyerUId(order.getBuyerUId());
        Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                .orElseThrow(() -> new RuntimeException("Query Id not found"));
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
        orderDTO.setProductName(product.getProductName());
        orderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
        orderDTO.setFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());
        orderDTO.setProductQuantity(order.getProductQuantity());
        orderDTO.setBuyerPaid(order.getBuyerPaid());
        orderDTO.setBuyerAddressId(order.getBuyerAddressId());

        orderDTO.setBuyerPurchaseOrderURL(order.getBuyerPurchaseOrderURL());
        orderDTO.setBuyerPurchaseOrderURLDate(order.getBuyerPurchaseOrderURLDate());
        orderDTO.setBuyerPurchaseOrderURLTime(order.getBuyerPurchaseOrderURLTime());
        orderDTO.setAdminPurchaseInvoiceURL(order.getAdminPurchaseInvoiceURL());
        orderDTO.setAdminPurchaseInvoiceURLDate(order.getAdminPurchaseInvoiceURLDate());
        orderDTO.setAdminPurchaseInvoiceURLTime(order.getAdminPurchaseInvoiceURLTime());
        orderDTO.setAdminNotifyBuyerToPay(order.getAdminNotifyBuyerToPay());
        orderDTO.setAdminNotifyBuyerToPayDate(order.getAdminNotifyBuyerToPayDate());
        orderDTO.setAdminNotifyBuyerToPayTime(order.getAdminNotifyBuyerToPayTime());
        orderDTO.setBuyer1stPayment(order.getBuyer1stPayment());
        orderDTO.setBuyer1stPaymentDate(order.getBuyer1stPaymentDate());
        orderDTO.setBuyer1stPaymentTime(order.getBuyer1stPaymentTime());
        orderDTO.setBuyerClearedPayment(order.getBuyerClearedPayment());
        orderDTO.setAdminReceivedOrderDate(order.getAdminReceivedOrderDate());
        orderDTO.setAdminReceivedOrderTime(order.getAdminReceivedOrderTime());
        orderDTO.setAdminProcessingOrderDate(order.getAdminProcessingOrderDate());
        orderDTO.setAdminProcessingOrderTime(order.getAdminProcessingOrderTime());
        orderDTO.setAdminDispatchedOrderDate(order.getAdminDispatchedOrderDate());
        orderDTO.setAdminDispatchedOrderTime(order.getAdminDispatchedOrderTime());
        orderDTO.setAdminCourierCompany(order.getAdminCourierCompany());
        orderDTO.setAdminOrderTrackerId(order.getAdminOrderTrackerId());
        orderDTO.setAdminEWayBill(order.getAdminEWayBill());
        orderDTO.setAdminEWayBillDate(order.getAdminEWayBillDate());
        orderDTO.setAdminEWayBillTime(order.getAdminEWayBillTime());
        orderDTO.setAdminPaymentInvoice(order.getAdminPaymentInvoice());
        orderDTO.setAdminPaymentInvoiceDate(order.getAdminPaymentInvoiceDate());
        orderDTO.setAdminPaymentInvoiceTime(order.getAdminPaymentInvoiceTime());
        orderDTO.setAdminLRCopy(order.getAdminLRCopy());
        orderDTO.setAdminLRCopyDate(order.getAdminLRCopyDate());
        orderDTO.setAdminLRCopyTime(order.getAdminLRCopyTime());
        orderDTO.setAdminWeightSlipPreLoad(order.getAdminWeightSlipPreLoad());
        orderDTO.setAdminWeightSlipPreLoadDate(order.getAdminWeightSlipPreLoadDate());
        orderDTO.setAdminWeightSlipPreLoadTime(order.getAdminWeightSlipPreLoadTime());
        orderDTO.setAdminWeightSlipPostLoad(order.getAdminWeightSlipPostLoad());
        orderDTO.setAdminWeightSlipPostLoadDate(order.getAdminWeightSlipPostLoadDate());
        orderDTO.setAdminWeightSlipPostLoadTime(order.getAdminWeightSlipPostLoadTime());
        orderDTO.setAdminTransactionCertificate(order.getAdminTransactionCertificate());
        orderDTO.setAdminTransactionCertificateDate(order.getAdminTransactionCertificateDate());
        orderDTO.setAdminTransactionCertificateTime(order.getAdminTransactionCertificateTime());
        orderDTO.setBuyerReceivedOrderDate(order.getBuyerReceivedOrderDate());
        orderDTO.setBuyerReceivedOrderTime(order.getBuyerReceivedOrderTime());

        return orderDTO;
    }

    @Override
    public List<OrderDTO> adminGetAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    OrderDTO orderDTO = new OrderDTO();
                    Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                            .orElseThrow(() -> new RuntimeException("Query Id not found"));
                    Product product = productRepository.findById(inquiry.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
                    orderDTO.setOId(order.getOId());
                    orderDTO.setQId(order.getQId());
                    orderDTO.setProductName(product.getProductName());
                    orderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
                    orderDTO.setFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());
                    orderDTO.setProductQuantity(order.getProductQuantity());
                    return orderDTO;
                }).toList();
    }

    @Override
    public OrderDTO adminGetOrderDetails(String oId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOId(oId);
        orderDTO.setQId(order.getQId());
        orderDTO.setBuyerUId(order.getBuyerUId());
        orderDTO.setSellerUId(order.getSellerUId());
        orderDTO.setProductQuantity(order.getProductQuantity());
        orderDTO.setBuyerFinalPrice(order.getBuyerFinalPrice());
        orderDTO.setBuyerPaid(order.getBuyerPaid());
        orderDTO.setSellerFinalPrice(order.getSellerFinalPrice());
        orderDTO.setAdminAddressId(order.getAdminAddressId());
        orderDTO.setBuyerAddressId(order.getBuyerAddressId());
        orderDTO.setPaymentId(order.getPaymentId());
        Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                .orElseThrow(() -> new RuntimeException("Query Id not found"));
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
        orderDTO.setProductName(product.getProductName());
        orderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
        orderDTO.setFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());

        orderDTO.setBuyerPurchaseOrderURL(order.getBuyerPurchaseOrderURL());
        orderDTO.setBuyerPurchaseOrderURLDate(order.getBuyerPurchaseOrderURLDate());
        orderDTO.setBuyerPurchaseOrderURLTime(order.getBuyerPurchaseOrderURLTime());

        orderDTO.setAdminPurchaseInvoiceURL(order.getAdminPurchaseInvoiceURL());
        orderDTO.setAdminPurchaseInvoiceURLDate(order.getAdminPurchaseInvoiceURLDate());
        orderDTO.setAdminPurchaseInvoiceURLTime(order.getAdminPurchaseInvoiceURLTime());

        orderDTO.setAdminPurchaseOrderURL(order.getAdminPurchaseOrderURL());
        orderDTO.setAdminPurchaseOrderURLDate(order.getAdminPurchaseOrderURLDate());
        orderDTO.setAdminPurchaseOrderURLTime(order.getAdminPurchaseOrderURLTime());

        orderDTO.setAdminNotifyBuyerToPay(order.getAdminNotifyBuyerToPay());
        orderDTO.setAdminNotifyBuyerToPayDate(order.getAdminNotifyBuyerToPayDate());
        orderDTO.setAdminNotifyBuyerToPayTime(order.getAdminNotifyBuyerToPayTime());

        orderDTO.setBuyer1stPayment(order.getBuyer1stPayment());
        orderDTO.setBuyer1stPaymentDate(order.getBuyer1stPaymentDate());
        orderDTO.setBuyer1stPaymentTime(order.getBuyer1stPaymentTime());

        orderDTO.setBuyerClearedPayment(order.getBuyerClearedPayment());
        orderDTO.setAdminNotifySellerToDispatch(order.getAdminNotifySellerToDispatch());
        orderDTO.setAdminNotifySellerToDispatchDate(order.getAdminNotifySellerToDispatchDate());
        orderDTO.setAdminNotifySellerToDispatchTime(order.getAdminNotifySellerToDispatchTime());

        orderDTO.setSellerPurchaseInvoiceURL(order.getSellerPurchaseInvoiceURL());
        orderDTO.setSellerPurchaseInvoiceURLDate(order.getSellerPurchaseInvoiceURLDate());
        orderDTO.setSellerPurchaseInvoiceURLTime(order.getSellerPurchaseInvoiceURLTime());

        orderDTO.setSellerProcessingOrderDate(order.getSellerProcessingOrderDate());
        orderDTO.setSellerProcessingOrderTime(order.getSellerProcessingOrderTime());
        orderDTO.setSellerDispatchOrderDate(order.getSellerDispatchOrderDate());
        orderDTO.setSellerDispatchOrderTime(order.getSellerDispatchOrderTime());
        orderDTO.setSellerDispatchPeriod(order.getSellerDispatchPeriod());

        orderDTO.setSellerCourierCompany(order.getSellerCourierCompany());
        orderDTO.setSellerOrderTrackerId(order.getSellerOrderTrackerId());
        orderDTO.setSellerOrderLoadingVehicleImg(order.getSellerOrderLoadingVehicleImg());
        orderDTO.setSellerOrderLoadingVehicleImgDate(order.getSellerOrderLoadingVehicleImgDate());
        orderDTO.setSellerOrderLoadingVehicleImgTime(order.getSellerOrderLoadingVehicleImgTime());

        orderDTO.setSellerLoadedSealedVehicleImg(order.getSellerLoadedSealedVehicleImg());
        orderDTO.setSellerLoadedSealedVehicleImgDate(order.getSellerLoadedSealedVehicleImgDate());
        orderDTO.setSellerLoadedSealedVehicleImgTime(order.getSellerLoadedSealedVehicleImgTime());

        orderDTO.setSellerEWayBill(order.getSellerEWayBill());
        orderDTO.setSellerEWayBillDate(order.getSellerEWayBillDate());
        orderDTO.setSellerEWayBillTime(order.getSellerEWayBillTime());

        orderDTO.setSellerPaymentInvoice(order.getSellerPaymentInvoice());
        orderDTO.setSellerPaymentInvoiceDate(order.getSellerPaymentInvoiceDate());
        orderDTO.setSellerPaymentInvoiceTime(order.getSellerPaymentInvoiceTime());

        orderDTO.setSellerLRCopy(order.getSellerLRCopy());
        orderDTO.setSellerLRCopyDate(order.getSellerLRCopyDate());
        orderDTO.setSellerLRCopyTime(order.getSellerLRCopyTime());

        orderDTO.setSellerWeightSlipPreLoad(order.getSellerWeightSlipPreLoad());
        orderDTO.setSellerWeightSlipPreLoadDate(order.getSellerWeightSlipPreLoadDate());
        orderDTO.setSellerWeightSlipPreLoadTime(order.getSellerWeightSlipPreLoadTime());

        orderDTO.setSellerWeightSlipPostLoad(order.getSellerWeightSlipPostLoad());
        orderDTO.setSellerWeightSlipPostLoadDate(order.getSellerWeightSlipPostLoadDate());
        orderDTO.setSellerWeightSlipPostLoadTime(order.getSellerWeightSlipPostLoadTime());

        orderDTO.setAdminReceivedOrderDate(order.getAdminReceivedOrderDate());
        orderDTO.setAdminReceivedOrderTime(order.getAdminReceivedOrderTime());

        orderDTO.setAdminProcessingOrderDate(order.getAdminProcessingOrderDate());
        orderDTO.setAdminProcessingOrderTime(order.getAdminProcessingOrderTime());

        orderDTO.setAdminDispatchedOrderDate(order.getAdminDispatchedOrderDate());
        orderDTO.setAdminDispatchedOrderTime(order.getAdminDispatchedOrderTime());

        orderDTO.setAdminCourierCompany(order.getAdminCourierCompany());
        orderDTO.setAdminOrderTrackerId(order.getAdminOrderTrackerId());

        orderDTO.setAdminEWayBill(order.getAdminEWayBill());
        orderDTO.setAdminEWayBillDate(order.getAdminEWayBillDate());
        orderDTO.setAdminEWayBillTime(order.getAdminEWayBillTime());

        orderDTO.setAdminPaymentInvoice(order.getAdminPaymentInvoice());
        orderDTO.setAdminPaymentInvoiceDate(order.getAdminPaymentInvoiceDate());
        orderDTO.setAdminPaymentInvoiceTime(order.getAdminPaymentInvoiceTime());

        orderDTO.setAdminLRCopy(order.getAdminLRCopy());
        orderDTO.setAdminLRCopyDate(order.getAdminLRCopyDate());
        orderDTO.setAdminLRCopyTime(order.getAdminLRCopyTime());

        orderDTO.setAdminWeightSlipPreLoad(order.getAdminWeightSlipPreLoad());
        orderDTO.setAdminWeightSlipPreLoadDate(order.getAdminWeightSlipPreLoadDate());
        orderDTO.setAdminWeightSlipPreLoadTime(order.getAdminWeightSlipPreLoadTime());

        orderDTO.setAdminWeightSlipPostLoad(order.getAdminWeightSlipPostLoad());
        orderDTO.setAdminWeightSlipPostLoadDate(order.getAdminWeightSlipPostLoadDate());
        orderDTO.setAdminWeightSlipPostLoadTime(order.getAdminWeightSlipPostLoadTime());

        orderDTO.setSellerTransactionCertificate(order.getSellerTransactionCertificate());
        orderDTO.setSellerTransactionCertificateDate(order.getSellerTransactionCertificateDate());
        orderDTO.setSellerTransactionCertificateTime(order.getSellerTransactionCertificateTime());

        orderDTO.setAdminTransactionCertificate(order.getAdminTransactionCertificate());
        orderDTO.setAdminTransactionCertificateDate(order.getAdminTransactionCertificateDate());
        orderDTO.setAdminTransactionCertificateTime(order.getAdminTransactionCertificateTime());

        orderDTO.setBuyerReceivedOrderDate(order.getBuyerReceivedOrderDate());
        orderDTO.setBuyerReceivedOrderTime(order.getBuyerReceivedOrderTime());

        return orderDTO;
    }

    @Override
    public String getOIdByQId(String qId) {
        return orderRepository.findOIdByQId(qId);
    }

    @Override
    public List<OrderDTO> sellerGetAllOrders(String sellerId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getBuyerUId().equals(sellerId))
                .map(order -> {
                    OrderDTO orderDTO = new OrderDTO();
                    Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                            .orElseThrow(() -> new RuntimeException("Query Id not found"));
                    Product product = productRepository.findById(inquiry.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
                    orderDTO.setOId(order.getOId());
                    orderDTO.setQId(order.getQId());
                    orderDTO.setProductName(product.getProductName());
                    orderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
                    orderDTO.setFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());
                    return orderDTO;
                }).toList();
    }

    @Override
    public OrderDTO sellerGetOrderDetails(String oId, String sellerId) {
        Order order = orderRepository.findById(oId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (! order.getSellerUId().equals(sellerId)) {
            throw new UnauthorizedException("Unauthorized : Seller Id is not matching.");
        }
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOId(oId);
        orderDTO.setQId(order.getQId());
        orderDTO.setSellerUId(order.getSellerUId());
        orderDTO.setProductQuantity(order.getProductQuantity());
        orderDTO.setSellerFinalPrice(order.getSellerFinalPrice());
        orderDTO.setAdminAddressId(order.getAdminAddressId());
        Inquiry inquiry = inquiryRepository.findByQId(order.getQId())
                .orElseThrow(() -> new RuntimeException("Query Id not found"));
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
        orderDTO.setProductName(product.getProductName());
        orderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
        orderDTO.setFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());

        orderDTO.setAdminPurchaseOrderURL(order.getAdminPurchaseOrderURL());
        orderDTO.setAdminPurchaseOrderURLDate(order.getAdminPurchaseOrderURLDate());
        orderDTO.setAdminPurchaseOrderURLTime(order.getAdminPurchaseOrderURLTime());

        orderDTO.setAdminNotifySellerToDispatch(order.getAdminNotifySellerToDispatch());
        orderDTO.setAdminNotifySellerToDispatchDate(order.getAdminNotifySellerToDispatchDate());
        orderDTO.setAdminNotifySellerToDispatchTime(order.getAdminNotifySellerToDispatchTime());

        orderDTO.setSellerPurchaseInvoiceURL(order.getSellerPurchaseInvoiceURL());
        orderDTO.setSellerPurchaseInvoiceURLDate(order.getSellerPurchaseInvoiceURLDate());
        orderDTO.setSellerPurchaseInvoiceURLTime(order.getSellerPurchaseInvoiceURLTime());

        orderDTO.setSellerProcessingOrderDate(order.getSellerProcessingOrderDate());
        orderDTO.setSellerProcessingOrderTime(order.getSellerProcessingOrderTime());
        orderDTO.setSellerDispatchOrderDate(order.getSellerDispatchOrderDate());
        orderDTO.setSellerDispatchOrderTime(order.getSellerDispatchOrderTime());
        orderDTO.setSellerDispatchPeriod(order.getSellerDispatchPeriod());

        orderDTO.setSellerCourierCompany(order.getSellerCourierCompany());
        orderDTO.setSellerOrderTrackerId(order.getSellerOrderTrackerId());
        orderDTO.setSellerOrderLoadingVehicleImg(order.getSellerOrderLoadingVehicleImg());
        orderDTO.setSellerOrderLoadingVehicleImgDate(order.getSellerOrderLoadingVehicleImgDate());
        orderDTO.setSellerOrderLoadingVehicleImgTime(order.getSellerOrderLoadingVehicleImgTime());

        orderDTO.setSellerLoadedSealedVehicleImg(order.getSellerLoadedSealedVehicleImg());
        orderDTO.setSellerLoadedSealedVehicleImgDate(order.getSellerLoadedSealedVehicleImgDate());
        orderDTO.setSellerLoadedSealedVehicleImgTime(order.getSellerLoadedSealedVehicleImgTime());

        orderDTO.setSellerEWayBill(order.getSellerEWayBill());
        orderDTO.setSellerEWayBillDate(order.getSellerEWayBillDate());
        orderDTO.setSellerEWayBillTime(order.getSellerEWayBillTime());

        orderDTO.setSellerPaymentInvoice(order.getSellerPaymentInvoice());
        orderDTO.setSellerPaymentInvoiceDate(order.getSellerPaymentInvoiceDate());
        orderDTO.setSellerPaymentInvoiceTime(order.getSellerPaymentInvoiceTime());

        orderDTO.setSellerLRCopy(order.getSellerLRCopy());
        orderDTO.setSellerLRCopyDate(order.getSellerLRCopyDate());
        orderDTO.setSellerLRCopyTime(order.getSellerLRCopyTime());

        orderDTO.setSellerWeightSlipPreLoad(order.getSellerWeightSlipPreLoad());
        orderDTO.setSellerWeightSlipPreLoadDate(order.getSellerWeightSlipPreLoadDate());
        orderDTO.setSellerWeightSlipPreLoadTime(order.getSellerWeightSlipPreLoadTime());

        orderDTO.setSellerWeightSlipPostLoad(order.getSellerWeightSlipPostLoad());
        orderDTO.setSellerWeightSlipPostLoadDate(order.getSellerWeightSlipPostLoadDate());
        orderDTO.setSellerWeightSlipPostLoadTime(order.getSellerWeightSlipPostLoadTime());

        orderDTO.setAdminReceivedOrderDate(order.getAdminReceivedOrderDate());
        orderDTO.setAdminReceivedOrderTime(order.getAdminReceivedOrderTime());

        orderDTO.setSellerTransactionCertificate(order.getSellerTransactionCertificate());
        orderDTO.setSellerTransactionCertificateDate(order.getSellerTransactionCertificateDate());
        orderDTO.setSellerTransactionCertificateTime(order.getSellerTransactionCertificateTime());

        return orderDTO;
    }

}
