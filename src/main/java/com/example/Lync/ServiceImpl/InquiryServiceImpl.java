package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.Entity.*;
import com.example.Lync.Repository.*;
import com.example.Lync.Service.InquiryService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class InquiryServiceImpl implements InquiryService {
    private InquiryRepository inquiryRepository;
    private StatusRepository statusRepository;
    private OrderStatusRepository orderStatusRepository;
    private SampleOrderRepository sampleOrderRepository;
    private SellerProductRepository sellerProductRepository;
    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private SellerBuyerRepository sellerBuyerRepository;

    private final Map<String, Inquiry> inquiryQIdCache = new HashMap<>();

    @PostConstruct
    public void init(){
        loadByQIdCache();
    }

    public void loadByQIdCache(){
        List<Inquiry> inquiries = inquiryRepository.findAll();
        for(Inquiry inquiry : inquiries){
            inquiryQIdCache.put(inquiry.getQId(), inquiry);
        }
    }

    private InquiryDTO mapToAdminDTO(Inquiry inquiry){
        InquiryDTO inquiryDTO = new InquiryDTO();
        inquiryDTO.setQId(inquiry.getQId());
        inquiryDTO.setBuyerUId(inquiry.getBuyerUId());
        inquiryDTO.setProductId(inquiry.getProductId());
        inquiryDTO.setQuantity(inquiry.getQuantity());
        inquiryDTO.setCertificate(inquiry.getCertificate());
        inquiryDTO.setAskPrice(inquiry.getAskPrice());
        inquiryDTO.setShipAddress(inquiry.getShipAddress());
        inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
        inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
        inquiryDTO.setSellerUId(inquiry.getSellerUId());
        inquiryDTO.setSentPrice(inquiry.getSentPrice());
        inquiryDTO.setSentDate(inquiry.getSentDate());
        inquiryDTO.setSentTime(inquiry.getSentTime());

        OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
        inquiryDTO.setOsId(orderStatus.getOsId());
        inquiryDTO.setDate(orderStatus.getDate());
        inquiryDTO.setTime(orderStatus.getTime());
        inquiryDTO.setDescription(orderStatus.getDescription());
        inquiryDTO.setImageUrl(orderStatus.getImageUrl());
        inquiryDTO.setLocation(orderStatus.getLocation());

        return inquiryDTO;
    }

    private InquiryDTO mapToSellerDTO(Inquiry inquiry){
        InquiryDTO inquiryDTO = new InquiryDTO();
        inquiryDTO.setRaiseTime(null);
        inquiryDTO.setRaiseDate(null);
        inquiryDTO.setQId(inquiry.getQId());
        inquiryDTO.setProductId(inquiry.getProductId());
        inquiryDTO.setQuantity(inquiry.getQuantity());
        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
        inquiryDTO.setCertificate(inquiry.getCertificate());
        inquiryDTO.setSellerUId(inquiry.getSellerUId());
        inquiryDTO.setSentPrice(inquiry.getSentPrice());
        inquiryDTO.setSentDate(inquiry.getSentDate());
        inquiryDTO.setSentTime(inquiry.getSentTime());

        OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
//        inquiryDTO.setOsId(orderStatus.getOsId());
        inquiryDTO.setDescription(orderStatus.getDescription());
//        inquiryDTO.setImageUrl(orderStatus.getImageUrl());
//        inquiryDTO.setLocation(orderStatus.getLocation());

        return inquiryDTO;
    }

    @Override
    public void addInquiry(InquiryDTO inquiryDTO, String buyerUId) { //status - 1
        Inquiry inquiry = new Inquiry();
        OrderStatus orderStatus = new OrderStatus();
        LocalDate currentDate = LocalDate.now();

        Long inquiryCount = inquiryRepository.countInquiryByCurrentDate(currentDate);
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nextInquiryNumber = String.format("%03d", inquiryCount + 1);
        String inquiryId = "I" + formattedDate + nextInquiryNumber;

        inquiry.setQId(inquiryId);
        System.out.println(inquiryId);
        System.out.println("dto" + inquiryDTO.getProductId());
        inquiry.setBuyerUId(buyerUId);
        inquiry.setProductId(inquiryDTO.getProductId());
        inquiry.setQuantity(inquiryDTO.getQuantity());
        inquiry.setCertificate(inquiryDTO.getCertificate());
        inquiry.setAskPrice(inquiryDTO.getAskPrice());
        inquiry.setShipAddress(inquiryDTO.getShipAddress());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(1L));
        inquiryRepository.save(inquiry);
        System.out.println("Entity" + inquiry.getProductId());


        orderStatus.setOId(inquiryId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(1L));
        orderStatus.setDescription(inquiryDTO.getDescription());
        orderStatus.setLocation(inquiryDTO.getLocation());
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiryRepository.save(inquiry);
    }

    @Override
    public List<InquiryDTO> getAllInquiries() { //admin
        return inquiryRepository.findAll().stream()
                .map(this::mapToAdminDTO).collect(Collectors.toList());

    }

//    @Override
//    public List<InquiryDTO> getAllInquiries() {
//        List<Inquiry> inquiries = inquiryRepository.findAll();
//        List<InquiryDTO> inquiryDTOS = new ArrayList<>();
//        for(Inquiry inquiry : inquiries){
//            InquiryDTO inquiryDTO = new InquiryDTO();
//            inquiryDTO.setQId(inquiry.getQId());
//            inquiryDTO.setBuyerUId(inquiry.getBuyerUId());
//            inquiryDTO.setPId(inquiry.getPId());
//            inquiryDTO.setQuantity(inquiry.getQuantity());
//            inquiryDTO.setCertificate(inquiry.getCertificate());
//            inquiryDTO.setAskPrice(inquiry.getAskPrice());
//            inquiryDTO.setShipAddress(inquiry.getShipAddress());
//            inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
//            inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
//            inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
//            inquiryDTO.setSellerUId(inquiry.getSellerUId());
//            inquiryDTO.setSentPrice(inquiry.getSentPrice());
//            inquiryDTO.setSentDate(inquiry.getSentDate());
//            inquiryDTO.setSentTime(inquiry.getSentTime());
//
//            inquiryDTOS.add(inquiryDTO);
//        }
//        return inquiryDTOS;
//    }



    @Override
    public InquiryDTO getInquiryByQId(String qId) throws Exception {
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }
        return mapToAdminDTO(inquiry);
    }


    @Override
    public String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO) { //status - 2
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(sellerProductRepository.findBySellerIdAndProductId(inquiryDTO.getSellerUId(), inquiry.getProductId()).isPresent()){
            inquiry.setSellerUId(inquiryDTO.getSellerUId());
            inquiry.setOrderStatus(statusRepository.findSMeaningBySId(2L));
            inquiry.setSentPrice(inquiryDTO.getSentPrice());
            inquiry.setSentDate(LocalDate.now());
            inquiry.setSentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
            inquiryRepository.save(inquiry);
            System.out.println(inquiry.getSentPrice());

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOId(qId);
            orderStatus.setStatus(statusRepository.findSMeaningBySId(2L));
            orderStatus.setDescription(inquiryDTO.getDescription());
            orderStatusRepository.save(orderStatus);

            inquiry.setOsId(orderStatus.getOsId());
            inquiryRepository.save(inquiry);

            return "Inquiry sent to seller successfully.";
        }else {
            return "Seller is not selling the specified product.";
        }

    }

    //Admin checks all sellers selling a particular product
    @Override
    public List<SellerProductDTO> sellersSellingProduct(Long productId) {
        List<SellerProduct> sellerProducts = sellerProductRepository.findByPId(productId);
        List<SellerProductDTO> productDTOS = new ArrayList<>();

        for(SellerProduct sellerProduct : sellerProducts){
            SellerProductDTO dto = new SellerProductDTO();
            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with ID :" + productId));
            SellerBuyer sellerBuyer = sellerBuyerRepository.findById(sellerProduct.getSellerId()).orElseThrow(() -> new RuntimeException("Seller not found with ID: " + sellerProduct.getSellerId()));
            dto.setProductName(product.getProductName());
//            dto.setProductCategory(categoryRepository.findById(product.getCategory()));
            dto.setSpId(sellerProduct.getSpId());
            dto.setSellerId(sellerProduct.getSellerId());
            dto.setSellerName(sellerBuyer.getFullName());
            dto.setSellerName(sellerBuyer.getEmail());
            dto.setSellerNumber(sellerBuyer.getPhoneNumber());
            dto.setMaxPricePerTon(sellerProduct.getMaxPricePerTon());
            dto.setMinPricePerTon(sellerProduct.getMinPricePerTon());
            dto.setDeliveryCharges(sellerProduct.getDeliveryCharges());

            productDTOS.add(dto);
        }
        return productDTOS;
    }


    @Override
    public List<InquiryDTO> sellerAllInquiries(String sellerUId) {
        return inquiryRepository.findAll().stream()
                .map(this::mapToSellerDTO)
                .filter(inquiryDTO -> inquiryDTO.getSellerUId().equals(sellerUId))
                .collect(Collectors.toList());
    }

    @Override
    public List<InquiryDTO> sellerNewInquiries(String sellerUId) {
        return inquiryRepository.findAll().stream()
                .map(this::mapToSellerDTO)
                .filter(inquiryDTO -> inquiryDTO.getOrderStatus().equals(statusRepository.findSMeaningBySId(2L)))
                .filter(inquiryDTO -> inquiryDTO.getSellerUId().equals(sellerUId))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<InquiryDTO> buyerGetsStatus1n2Inquiries() {
//        return inquiryRepository.findAll().stream()
//                .map(this::mapToAdminDTO)
//                .filter(inquiryDTO -> inquiryDTO.getOrderStatus().equals(statusRepository.findSMeaningBySId(1L)) ||
//                                      inquiryDTO.getOrderStatus().equals(statusRepository.findSMeaningBySId(2L)))
//
//                .collect(Collectors.toList());
//    }

    //Buyer can reject status 1, 2 inquiries
    @Override
    public void buyerRejectsInquiries(String qId, String description, String buyerUId) throws Exception {
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }

        if(inquiry.getOrderStatus().equals(statusRepository.findSMeaningBySId(1L)) || //Buyer Raised Inquiry
                inquiry.getOrderStatus().equals(statusRepository.findSMeaningBySId(2L))) { //Admin sent inquiry to seller

            inquiry.setOrderStatus(statusRepository.findSMeaningBySId(6L));

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOId(qId);
            orderStatus.setStatus(statusRepository.findSMeaningBySId(6L));
            orderStatus.setDescription(description);
            orderStatusRepository.save(orderStatus);

            inquiry.setOsId(orderStatus.getOsId());
            inquiryRepository.save(inquiry);
        } else {
            throw new Exception("Inquiry cannot be rejected as Seller Accepted the inquiry.");
        }
    }

    @Override
    public InquiryDTO sellerOpenInquiry(String qId) throws Exception {
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }
        return mapToSellerDTO(inquiry);
    }

    @Override
    public void sellerAcceptQuery(String qId, String description) throws Exception {
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }

        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(3L));

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(3L));
        orderStatus.setDescription(description);
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiryRepository.save(inquiry);
    }

    @Override
    public void sellerRejectQuery(String qId, String description) throws Exception {
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(4L));

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(4L));
        orderStatus.setDescription(description);
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiryRepository.save(inquiry);
    }

    @Override
    public void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception {
        SampleOrder sampleOrder = new SampleOrder();
        OrderStatus orderStatus = new OrderStatus();
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }
        LocalDate currentDate = LocalDate.now();

        Long count = sampleOrderRepository.countSampleOrderByCurrentDate(currentDate);
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nextInquiryNumber = String.format("%03d", count + 1);
        String soId = "SO" + formattedDate + nextInquiryNumber;

        sampleOrder.setSoId(soId);
        sampleOrder.setQId(qId);
        sampleOrder.setBuyerUId(inquiry.getBuyerUId());
        sampleOrder.setSellerUId(inquiry.getSellerUId());
        sampleOrder.setProductId(inquiry.getProductId());
        sampleOrder.setSOQuantity(sampleOrderDTO.getSOQuantity());
        sampleOrder.setStockLocation(sampleOrderDTO.getStockLocation());
        sampleOrderRepository.save(sampleOrder);

        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(5L));
        orderStatus.setDescription(sampleOrderDTO.getDescription());
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(5L));
        inquiryRepository.save(inquiry);

    }


}











