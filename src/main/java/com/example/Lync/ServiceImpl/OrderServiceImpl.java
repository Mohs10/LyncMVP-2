package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.MessageConfig;
import com.example.Lync.Config.S3Service;
import com.example.Lync.Entity.Inquiry;
import com.example.Lync.Entity.Notification;
import com.example.Lync.Entity.Order;
import com.example.Lync.Entity.SampleOrder;
import com.example.Lync.Exception.UnauthorizedException;
import com.example.Lync.Repository.InquiryRepository;
import com.example.Lync.Repository.NotificationRepository;
import com.example.Lync.Repository.OrderRepository;
import com.example.Lync.Repository.SampleOrderRepository;
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


    @Override
    public String buyerUploadPurchaseOrder(String qId, MultipartFile file, String buyerId) throws IOException {
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        if (!buyerId.equals(inquiry.getBuyerId())) {
            throw new UnauthorizedException("Unauthorized: The buyer Id does not match with the respective query's buyer Id.");
        }
        SampleOrder sampleOrder = sampleOrderRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Sample Order not found with given Id : " + qId));

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
        order.setAdminAddressId(sampleOrder.getAdminAddressId());
        order.setBuyerAddressId(sampleOrder.getBuyerAddressId());
        order.setBuyerPurchaseOrderURL(key);
        order.setBuyerPurchaseOrderURLDate(LocalDate.now());
        order.setBuyerPurchaseOrderURLTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        orderRepository.save(order);

        Notification notification = new Notification();
        notification.setNotificationId(UUID.randomUUID().toString());
        notification.setMessage("Buyer with ID : " + buyerId + " has uploaded the purchase order for query ID : " + sampleOrder.getQId());
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
        return "You uploaded the Transaction Certificate";
    }

}
