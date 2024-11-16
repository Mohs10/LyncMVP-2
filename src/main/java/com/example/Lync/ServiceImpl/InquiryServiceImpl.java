package com.example.Lync.ServiceImpl;

import com.example.Lync.DTO.InquiryDTO;
import com.example.Lync.DTO.SampleOrderDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.DTO.SellerReceiveInquiryDTO;
import com.example.Lync.Entity.*;
import com.example.Lync.Repository.*;
import com.example.Lync.Service.InquiryService;
import com.example.Lync.Service.SellerBuyerService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private SellerNegotiateRepository sellerNegotiateRepository;
    private BuyerNegotiateRepository buyerNegotiateRepository;
    private SellerBuyerService sellerBuyerService;

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
        inquiryDTO.setBuyerUId(inquiry.getBuyerId());
        inquiryDTO.setProductId(inquiry.getProductId());
        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(null);
        inquiryDTO.setProductName(product.getProductName());
        inquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
        inquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
        inquiryDTO.setProductFormId(inquiry.getProductFormId());
        inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());


        //Order Specification
        inquiryDTO.setQuantity(inquiry.getQuantity());
        inquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        inquiryDTO.setPriceTerms(inquiry.getPriceTerms());
//        inquiryDTO.setCertificate(inquiry.getCertificate());
        inquiryDTO.setAskMinPrice(inquiry.getAskMinPrice());
        inquiryDTO.setAskMaxPrice(inquiry.getAskMaxPrice());
        inquiryDTO.setPriceUnit(inquiry.getPriceUnit());
        inquiryDTO.setNpop(inquiry.getNpop());
        inquiryDTO.setNop(inquiry.getNop());
        inquiryDTO.setEu(inquiry.getEu());
        inquiryDTO.setGsdc(inquiry.getGsdc());
        inquiryDTO.setIpm(inquiry.getIpm());
        inquiryDTO.setOther(inquiry.getOther());
        inquiryDTO.setOtherCertification(inquiry.getOtherCertification());
        inquiryDTO.setPackagingMaterial(inquiry.getPackagingMaterial());
        inquiryDTO.setPaymentTerms(inquiry.getPaymentTerms());
        inquiryDTO.setTargetLeadTime(inquiry.getTargetLeadTime());
        inquiryDTO.setDeliveryAddress(inquiry.getDeliveryAddress());
        inquiryDTO.setCountry(inquiry.getCountry());
        inquiryDTO.setState(inquiry.getState());
        inquiryDTO.setCity(inquiry.getCity());
        inquiryDTO.setPincode(inquiry.getPincode());
        inquiryDTO.setSpecifyDeliveryDate(inquiry.getSpecifyDeliveryDate());

        //Product Specification
        inquiryDTO.setChalkyGrains(inquiry.getChalkyGrains());
        inquiryDTO.setGrainSize(inquiry.getGrainSize());
        inquiryDTO.setKettValue(inquiry.getKettValue());
        inquiryDTO.setMoistureContent(inquiry.getMoistureContent());
        inquiryDTO.setBrokenGrain(inquiry.getBrokenGrain());
        inquiryDTO.setAdmixing(inquiry.getAdmixing());
        inquiryDTO.setDd(inquiry.getDd());

        inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
        inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
        inquiryDTO.setSellerUId(inquiry.getSellerUId());
        inquiryDTO.setSellerFinalPrice(inquiry.getSellerFinalPrice());
        inquiryDTO.setSentDate(inquiry.getSentDate());
        inquiryDTO.setSentTime(inquiry.getSentTime());
        inquiryDTO.setUnit(inquiry.getUnit());

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
        inquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        inquiryDTO.setAskMinPrice(inquiry.getAskMinPrice());
        inquiryDTO.setAskMaxPrice(inquiry.getAskMaxPrice());
        inquiryDTO.setPriceUnit(inquiry.getPriceUnit());
        inquiryDTO.setDeliveryAddress(inquiry.getDeliveryAddress());
//        productRepository.findById(inquiry.getProductId()).get().getTypes().get(1);
//        inquiryDTO.setType(productRepository.findById(inquiryDTO.getProductId()));


        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
//        inquiryDTO.setCertificate(inquiry.getCertificate());
        inquiryDTO.setSellerUId(inquiry.getSellerUId());
        inquiryDTO.setSellerFinalPrice(inquiry.getSellerFinalPrice());
        inquiryDTO.setSentDate(inquiry.getSentDate());
        inquiryDTO.setSentTime(inquiry.getSentTime());
        inquiryDTO.setUnit(inquiry.getUnit());

        OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
//        inquiryDTO.setOsId(orderStatus.getOsId());
        inquiryDTO.setDescription(orderStatus.getDescription());
//        inquiryDTO.setImageUrl(orderStatus.getImageUrl());
//        inquiryDTO.setLocation(orderStatus.getLocation());

        return inquiryDTO;
    }

    private SellerReceiveInquiryDTO mapToSellerViewList(SellerNegotiate sellerNegotiate){
        SellerReceiveInquiryDTO sellerReceiveInquiryDTO = new SellerReceiveInquiryDTO();

        Inquiry inquiry = inquiryQIdCache.get(sellerNegotiate.getQId());

        sellerReceiveInquiryDTO.setProductId(inquiry.getProductId());
        sellerReceiveInquiryDTO.setSellerUId(sellerNegotiate.getSellerUId());
        sellerReceiveInquiryDTO.setQuantity(inquiry.getQuantity());
        sellerReceiveInquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        sellerReceiveInquiryDTO.setAdminInitialPrice(sellerNegotiate.getAdminInitialPrice());
        sellerReceiveInquiryDTO.setAdminAddressId(sellerNegotiate.getAdminAddressId());
        sellerReceiveInquiryDTO.setSellerName(sellerBuyerRepository.findById(sellerNegotiate.getSellerUId()).orElseThrow(null).getFullName());


        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(null);
        sellerReceiveInquiryDTO.setProductName(product.getProductName());
        sellerReceiveInquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
        sellerReceiveInquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
        sellerReceiveInquiryDTO.setProductFormId(inquiry.getProductFormId());
        sellerReceiveInquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());


        return sellerReceiveInquiryDTO;
    }

    @Override
    public String buyerAddInquiry(InquiryDTO inquiryDTO, String buyerUId) throws Exception { //status - 1
        // Retrieve buyer details from SellerBuyer table
        SellerBuyer buyer = sellerBuyerRepository.findById(buyerUId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + buyerUId));

        // Check if cancelledChequeUrl is null
        if (buyer.getCancelledChequeUrl() == null || buyer.getCancelledChequeUrl().isEmpty()) {
            throw new Exception("Inquiry cannot be added because the buyer has not provided a cancelled cheque.");
        } else {
            Inquiry inquiry = new Inquiry();
            OrderStatus orderStatus = new OrderStatus();
            LocalDate currentDate = LocalDate.now();

            Long inquiryCount = inquiryRepository.countInquiryByCurrentDate(currentDate);
            String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String nextInquiryNumber = String.format("%03d", inquiryCount + 1);
            String inquiryId = "I" + formattedDate + nextInquiryNumber;

            inquiry.setQId(inquiryId);
            inquiry.setBuyerId(buyerUId);
            inquiry.setProductId(inquiryDTO.getProductId());
            inquiry.setProductFormId(inquiryDTO.getProductFormId());
            inquiry.setProductVarietyId(inquiryDTO.getProductVarietyId());

            // Order Specification
            inquiry.setQuantity(inquiryDTO.getQuantity());
            inquiry.setQuantityUnit(inquiryDTO.getQuantityUnit());
            inquiry.setPriceTerms(inquiryDTO.getPriceTerms());
            inquiry.setAskMinPrice(inquiryDTO.getAskMinPrice());
            inquiry.setAskMaxPrice(inquiryDTO.getAskMaxPrice());
            inquiry.setPriceUnit(inquiryDTO.getPriceUnit());
            inquiry.setNpop(inquiryDTO.getNpop());
            inquiry.setNop(inquiryDTO.getNop());
            inquiry.setEu(inquiryDTO.getEu());
            inquiry.setGsdc(inquiryDTO.getGsdc());
            inquiry.setIpm(inquiryDTO.getIpm());
            inquiry.setOther(inquiryDTO.getOther());
            inquiry.setOtherCertification(inquiryDTO.getOtherCertification());
            inquiry.setPackagingMaterial(inquiryDTO.getPackagingMaterial());
            inquiry.setPaymentTerms(inquiryDTO.getPaymentTerms());
            inquiry.setTargetLeadTime(inquiryDTO.getTargetLeadTime());
            inquiry.setDeliveryAddress(inquiryDTO.getDeliveryAddress());
            inquiry.setCountry(inquiryDTO.getCountry());
            inquiry.setState(inquiryDTO.getState());
            inquiry.setCity(inquiryDTO.getCity());
            inquiry.setPincode(inquiryDTO.getPincode());
            inquiry.setSpecifyDeliveryDate(inquiryDTO.getSpecifyDeliveryDate());

            // Product Specification
            inquiry.setChalkyGrains(inquiryDTO.getChalkyGrains());
            inquiry.setGrainSize(inquiryDTO.getGrainSize());
            inquiry.setKettValue(inquiryDTO.getKettValue());
            inquiry.setMoistureContent(inquiryDTO.getMoistureContent());
            inquiry.setBrokenGrain(inquiryDTO.getBrokenGrain());
            inquiry.setAdmixing(inquiryDTO.getAdmixing());
            inquiry.setDd(inquiryDTO.getDd());

            inquiry.setUnit(inquiryDTO.getUnit());
            inquiry.setOrderStatus(statusRepository.findSMeaningBySId(1L));
            inquiryRepository.save(inquiry);

            orderStatus.setOId(inquiryId);
            orderStatus.setStatus(statusRepository.findSMeaningBySId(1L));
            orderStatus.setDescription(inquiryDTO.getDescription());
            orderStatus.setLocation(inquiryDTO.getLocation());
            orderStatusRepository.save(orderStatus);

            inquiry.setOsId(orderStatus.getOsId());
            inquiryRepository.save(inquiry);

            return "You raised an inquiry.";
        }
    }

    @Override
    public List<InquiryDTO> buyerGetsAllInquiry(String buyerUId) {
        return inquiryRepository.findAll().stream()

                .filter(inquiry -> inquiry.getBuyerId().equals(buyerUId))
                .map(inquiry -> {
                    InquiryDTO inquiryDTO = new InquiryDTO();
                    inquiryDTO.setQId(inquiry.getQId());
                    inquiryDTO.setProductId(inquiry.getProductId());
                    System.out.println(inquiryDTO.getProductId());
                    System.out.println(inquiry.getProductId());
                    Product product = productRepository.findById(20241113385085L).orElseThrow(null);
                    System.out.println(product);
                    inquiryDTO.setProductName(product.getProductName());
                    inquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
                    inquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
                    inquiryDTO.setProductFormId(inquiry.getProductFormId());
                    inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
                    inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
                    System.out.println( "serviceBuyerUid : "+ buyerUId);
                    System.out.println("ServiceInquiry : " + inquiryRepository.findAll());
                    return inquiryDTO;
                }).collect(Collectors.toList());
    }

//    @Override
//    public List<InquiryDTO> buyerGetsInquiries(String buyerUId) {
//        return inquiryRepository.findAll().stream()
//                .filter(inquiry -> inquiry.getBuyerId().equals(buyerUId)) // Filter inquiries by buyer ID
//                .map(inquiry -> {
//                    // Create a new InquiryDTO
//                    InquiryDTO inquiryDTO = new InquiryDTO();
//                    inquiryDTO.setQId(inquiry.getQId());
//                    inquiryDTO.setProductId(inquiry.getProductId());
//
//                    // Debugging information
//                    System.out.println("Inquiry Product ID (DTO): " + inquiryDTO.getProductId());
//                    System.out.println("Inquiry Product ID (Entity): " + inquiry.getProductId());
//
//                    // Fetch the product by its ID
//                    Product product = productRepository.findById(inquiry.getProductId())
//                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
//
//                    // Debugging information
//                    System.out.println("Fetched Product: " + product);
//
//                    // Populate product-related details
//                    inquiryDTO.setProductName(product.getProductName());
//                    inquiryDTO.setVarietyName(
//                            product.getVarieties().stream()
//                                    .map(Variety::getVarietyName)
//                                    .collect(Collectors.joining(", ")) // Use joining for better readability
//                    );
//                    inquiryDTO.setFormName(
//                            product.getForms().stream()
//                                    .map(Form::getFormName)
//                                    .collect(Collectors.joining(", "))
//                    );
//
//                    // Populate other fields
//                    inquiryDTO.setProductFormId(inquiry.getProductFormId());
//                    inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
//                    inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
//
//                    // Debugging information
//                    System.out.println("Service Buyer UID: " + buyerUId);
//
//                    return inquiryDTO;
//                })
//                .collect(Collectors.toList()); // Collect the stream to a list
//
//    }

    @Override
    public List<InquiryDTO> buyerGetsInquiries(String buyerUId) {
//        System.out.println("Fetching inquiries for buyerUId: " + buyerUId);


        // Fetch inquiries for the specific buyer
        List<Inquiry> inquiries = inquiryRepository.findByBuyerId(buyerUId);
//        System.out.println("Fetched inquiries: " + inquiries);

//        return null;

        List<InquiryDTO> inquiryDTOS = new ArrayList<>();

        for (Inquiry inquiry : inquiries) {
            try {
                InquiryDTO inquiryDTO = new InquiryDTO();
                inquiryDTO.setQId(inquiry.getQId());
                inquiryDTO.setProductId(inquiry.getProductId());

                // Debugging prints
//                System.out.println("Product ID from DTO: " + inquiryDTO.getProductId());
//                System.out.println("Product ID from Inquiry: " + inquiry.getProductId());

                // Fetch product from repository
                Product product = productRepository.findById(inquiry.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));


                // Debugging print
//                System.out.println("Fetched Product: " + product);

                // Set product-related fields
                inquiryDTO.setProductName(product.getProductName());
                inquiryDTO.setVarietyName(
                        product.getVarieties().stream().map(Variety::getVarietyName).toList().toString()
                );
                inquiryDTO.setFormName(
                        product.getForms().stream().map(Form::getFormName).toList().toString()
                );

                // Set other fields
                inquiryDTO.setProductFormId(inquiry.getProductFormId());
                inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
                inquiryDTO.setOrderStatus(inquiry.getOrderStatus());

                // Debugging prints
//                System.out.println("Service Buyer UId: " + buyerUId);
//                System.out.println("Service Inquiry: " + inquiryRepository.findAll());

                // Add to the list
                inquiryDTOS.add(inquiryDTO);

            } catch (Exception e) {
                // Handle any exceptions and log the error
//                System.err.println("Error processing inquiry with QId " + inquiry.getQId() + ": " + e.getMessage());
            }
        }


        return inquiryDTOS;

    }


    @Override
    public InquiryDTO buyerGetsInquiryById(String buyerUId, String qId) {
        Inquiry inquiry = inquiryQIdCache.get(qId);
        InquiryDTO inquiryDTO = new InquiryDTO();

        //Order Specification
        inquiryDTO.setQId(qId);
        inquiryDTO.setBuyerUId(inquiry.getBuyerId());
        inquiryDTO.setProductId(inquiry.getProductId());
        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(null);
        inquiryDTO.setProductName(product.getProductName());
        inquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
        inquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
        inquiryDTO.setProductFormId(inquiry.getProductFormId());
        inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
        inquiryDTO.setQuantity(inquiry.getQuantity());
        inquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        inquiryDTO.setPriceTerms(inquiry.getPriceTerms());
        inquiryDTO.setAskMinPrice(inquiry.getAskMinPrice());
        inquiryDTO.setAskMaxPrice(inquiry.getAskMaxPrice());
        inquiryDTO.setPriceUnit(inquiry.getPriceUnit());
        inquiryDTO.setNpop(inquiry.getNpop());
        inquiryDTO.setNop(inquiry.getNop());
        inquiryDTO.setEu(inquiry.getEu());
        inquiryDTO.setGsdc(inquiry.getGsdc());
        inquiryDTO.setIpm(inquiry.getIpm());
        inquiryDTO.setOther(inquiry.getOther());
        inquiryDTO.setOtherCertification(inquiry.getOtherCertification());
        inquiryDTO.setPackagingMaterial(inquiry.getPackagingMaterial());
        inquiryDTO.setPaymentTerms(inquiry.getPaymentTerms());
        inquiryDTO.setTargetLeadTime(inquiry.getTargetLeadTime());
        inquiryDTO.setDeliveryAddress(inquiry.getDeliveryAddress());
        inquiryDTO.setCountry(inquiry.getCountry());
        inquiryDTO.setState(inquiry.getState());
        inquiryDTO.setCity(inquiry.getCity());
        inquiryDTO.setPincode(inquiry.getPincode());
        inquiryDTO.setSpecifyDeliveryDate(inquiry.getSpecifyDeliveryDate());

        //Product Specification
        inquiryDTO.setChalkyGrains(inquiry.getChalkyGrains());
        inquiryDTO.setGrainSize(inquiry.getGrainSize());
        inquiryDTO.setKettValue(inquiry.getKettValue());
        inquiryDTO.setMoistureContent(inquiry.getMoistureContent());
        inquiryDTO.setBrokenGrain(inquiry.getBrokenGrain());
        inquiryDTO.setAdmixing(inquiry.getAdmixing());
        inquiryDTO.setDd(inquiry.getDd());

        inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
        inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());

        //Buyer Negotiate
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        if(negotiate != null) {
            inquiryDTO.setAdminInitialPrice(negotiate.getAdminInitialPrice());
            inquiryDTO.setComment(negotiate.getComment());
            inquiryDTO.setPaymentTerm(negotiate.getPaymentTerm());
            inquiryDTO.setAipDate(negotiate.getAipDate());
            inquiryDTO.setAipTime(negotiate.getAipTime());
            inquiryDTO.setBuyerNegotiatePrice(negotiate.getBuyerNegotiatePrice());
            inquiryDTO.setBnpDate(negotiate.getBnpDate());
            inquiryDTO.setBnpTime(negotiate.getBnpTime());
            inquiryDTO.setAdminFinalPrice(negotiate.getAdminFinalPrice());
            inquiryDTO.setAfpDate(negotiate.getAfpDate());
            inquiryDTO.setAfpTime(negotiate.getAipTime());
            inquiryDTO.setStatus(negotiate.getStatus());
        }
        return inquiryDTO;
    }


    @Override
    public List<InquiryDTO> adminGetAllInquiry() {
//        System.out.println("Fetching all inquiries for admin.");

        List<Inquiry> inquiries = inquiryRepository.findAll();
//        System.out.println("Fetched inquiries: " + inquiries);

        List<InquiryDTO> inquiryDTOS = new ArrayList<>();

        for (Inquiry inquiry : inquiries) {
            try {
                InquiryDTO inquiryDTO = new InquiryDTO();

                // Set basic inquiry details
                inquiryDTO.setQId(inquiry.getQId());
                inquiryDTO.setBuyerUId(inquiry.getBuyerId());
                inquiryDTO.setProductId(inquiry.getProductId());

                // Debugging prints
//                System.out.println("Processing Inquiry with QId: " + inquiry.getQId());

                // Fetch product details
                Product product = productRepository.findById(inquiry.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));

                // Set product-related fields
                inquiryDTO.setProductName(product.getProductName());
                inquiryDTO.setVarietyName(
                        product.getVarieties().stream().map(Variety::getVarietyName).collect(Collectors.joining(", "))
                );
                inquiryDTO.setFormName(
                        product.getForms().stream().map(Form::getFormName).collect(Collectors.joining(", "))
                );

                // Set order specifications
                inquiryDTO.setProductFormId(inquiry.getProductFormId());
                inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
                inquiryDTO.setQuantity(inquiry.getQuantity());
                inquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
                inquiryDTO.setPriceTerms(inquiry.getPriceTerms());
                inquiryDTO.setAskMinPrice(inquiry.getAskMinPrice());
                inquiryDTO.setAskMaxPrice(inquiry.getAskMaxPrice());
                inquiryDTO.setPriceUnit(inquiry.getPriceUnit());
                inquiryDTO.setNpop(inquiry.getNpop());
                inquiryDTO.setNop(inquiry.getNop());
                inquiryDTO.setEu(inquiry.getEu());
                inquiryDTO.setGsdc(inquiry.getGsdc());
                inquiryDTO.setIpm(inquiry.getIpm());
                inquiryDTO.setOther(inquiry.getOther());
                inquiryDTO.setOtherCertification(inquiry.getOtherCertification());
                inquiryDTO.setPackagingMaterial(inquiry.getPackagingMaterial());
                inquiryDTO.setPaymentTerms(inquiry.getPaymentTerms());
                inquiryDTO.setTargetLeadTime(inquiry.getTargetLeadTime());
                inquiryDTO.setDeliveryAddress(inquiry.getDeliveryAddress());
                inquiryDTO.setCountry(inquiry.getCountry());
                inquiryDTO.setState(inquiry.getState());
                inquiryDTO.setCity(inquiry.getCity());
                inquiryDTO.setPincode(inquiry.getPincode());
                inquiryDTO.setSpecifyDeliveryDate(inquiry.getSpecifyDeliveryDate());

                // Set product specifications
                inquiryDTO.setChalkyGrains(inquiry.getChalkyGrains());
                inquiryDTO.setGrainSize(inquiry.getGrainSize());
                inquiryDTO.setKettValue(inquiry.getKettValue());
                inquiryDTO.setMoistureContent(inquiry.getMoistureContent());
                inquiryDTO.setBrokenGrain(inquiry.getBrokenGrain());
                inquiryDTO.setAdmixing(inquiry.getAdmixing());
                inquiryDTO.setDd(inquiry.getDd());

                // Set raise details
                inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
                inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
                inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
                inquiryDTO.setSellerUId(inquiry.getSellerUId());
                inquiryDTO.setSellerFinalPrice(inquiry.getSellerFinalPrice());
                inquiryDTO.setSentDate(inquiry.getSentDate());
                inquiryDTO.setSentTime(inquiry.getSentTime());
                inquiryDTO.setUnit(inquiry.getUnit());

                // Fetch and set order status details
                OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
                if (orderStatus != null) {
                    inquiryDTO.setOsId(orderStatus.getOsId());
                    inquiryDTO.setDate(orderStatus.getDate());
                    inquiryDTO.setTime(orderStatus.getTime());
                    inquiryDTO.setDescription(orderStatus.getDescription());
                    inquiryDTO.setImageUrl(orderStatus.getImageUrl());
                    inquiryDTO.setLocation(orderStatus.getLocation());
                }

                // Add to the list
                inquiryDTOS.add(inquiryDTO);

            } catch (Exception e) {
                // Handle any exceptions and log the error
//                System.err.println("Error processing inquiry with QId " + inquiry.getQId() + ": " + e.getMessage());
            }
        }

        return inquiryDTOS;
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
    public InquiryDTO adminGetInquiryByQId(String qId) throws Exception {
//        Inquiry inquiry = inquiryRepository.findByQId(qId);
        Inquiry inquiry = inquiryQIdCache.get(qId);
        if(inquiry == null){
            throw new Exception("Inquiry not found with qId: " + qId);
        }
        return mapToAdminDTO(inquiry);
    }


    @Override
    public String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO) { //status - 2
        Inquiry inquiry = inquiryQIdCache.get(qId);
        List<String> successfulSellers = new ArrayList<>();

        // Get all sellers selling the specified product
        List<SellerProduct> validSellerProducts = sellerProductRepository
                .findByProductIdAndProductFormIdAndProductVarietyId(
                        inquiry.getProductId(),
                        inquiry.getProductFormId(),
                        inquiry.getProductVarietyId()
                );

        // Extract the seller IDs from the validSellerProducts list
        Set<String> validSellerIds = validSellerProducts.stream()
                .map(SellerProduct::getSellerId)
                .collect(Collectors.toSet());

            for(String sellerUId : inquiryDTO.getSellerUIds()) {
                if (validSellerIds.contains(sellerUId)) { //to be changed

                    inquiry.setOrderStatus(statusRepository.findSMeaningBySId(2L));
                    inquiry.setSentDate(LocalDate.now());
                    inquiry.setSentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
                    inquiryRepository.save(inquiry);

                    // Create a new entry in SellerNegotiate for tracking
                    SellerNegotiate sellerNegotiate = new SellerNegotiate();
                    sellerNegotiate.setQId(qId);
                    sellerNegotiate.setSellerUId(sellerUId);
                    sellerNegotiate.setAdminInitialPrice(inquiryDTO.getAdminInitialPrice());
                    sellerNegotiate.setAipDate(LocalDate.now());
                    sellerNegotiate.setAipTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
//                    sellerNegotiate.setInstruction(inquiryDTO.getDescription());
                    sellerNegotiate.setAdminAddressId(inquiryDTO.getAdminAddressId());
                    sellerNegotiate.setStatus("Inquiry send to Seller");
                    sellerNegotiateRepository.save(sellerNegotiate);

                    OrderStatus orderStatus = new OrderStatus();
                    orderStatus.setOId(qId);
                    orderStatus.setStatus(statusRepository.findSMeaningBySId(2L));
                    orderStatus.setDescription(inquiryDTO.getDescription());
                    orderStatusRepository.save(orderStatus);

                    inquiry.setOsId(orderStatus.getOsId());
                    inquiryRepository.save(inquiry);

                    successfulSellers.add(sellerUId);
                } else {
                    return "Seller with ID: " + sellerUId + " is not selling the specified product.";
                }
            }
        return "Inquiry sent to sellers with IDs: " + (successfulSellers.isEmpty() ? "None" : String.join(", ", successfulSellers));
    }

//    @Override
//    public String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO) { //status - 2
////        Inquiry inquiry = inquiryRepository.findByQId(qId);
//        Inquiry inquiry = inquiryQIdCache.get(qId);
//
//
//        if(sellerProductRepository.findBySellerIdAndProductId(inquiryDTO.getSellerUId(), inquiry.getProductId()).isPresent()){
//            inquiry.setSellerUId(inquiryDTO.getSellerUId());
//            inquiry.setOrderStatus(statusRepository.findSMeaningBySId(2L));
//            inquiry.setSentPrice(inquiryDTO.getSentPrice());
//            inquiry.setSentDate(LocalDate.now());
//            inquiry.setSentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
//            inquiryRepository.save(inquiry);
//            System.out.println(inquiry.getSentPrice());
//
//            OrderStatus orderStatus = new OrderStatus();
//            orderStatus.setOId(qId);
//            orderStatus.setStatus(statusRepository.findSMeaningBySId(2L));
//            orderStatus.setDescription(inquiryDTO.getDescription());
//            orderStatusRepository.save(orderStatus);
//
//            inquiry.setOsId(orderStatus.getOsId());
//            inquiryRepository.save(inquiry);
//
//            return "Inquiry sent to seller successfully.";
//        }else {
//            return "Seller is not selling the specified product.";
//        }
//
//    }

    //Admin checks all sellers selling a particular product
    @Override
    public List<SellerProductDTO> sellersSellingProduct(Long productId, Long productFormId, Long productVarietyId) {
        List<SellerProduct> sellerProducts = sellerProductRepository
                .findByProductIdAndProductFormIdAndProductVarietyId(productId, productFormId, productVarietyId);
        List<SellerProductDTO> productDTOS = sellerProducts.stream()
                .map(sellerBuyerService::toDTO) // Method reference
                .collect(Collectors.toList());

//        for(SellerProduct sellerProduct : sellerProducts){
//            SellerProductDTO dto = new SellerProductDTO();
//            Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found with ID :" + productId));
//            SellerBuyer sellerBuyer = sellerBuyerRepository.findById(sellerProduct.getSellerId()).orElseThrow(() -> new RuntimeException("Seller not found with ID: " + sellerProduct.getSellerId()));
////            dto.setProductName(product.getProductName());
//////            dto.setProductCategory(categoryRepository.findById(product.getCategory()));
////            dto.setSpId(sellerProduct.getSpId());
////            dto.setSellerId(sellerProduct.getSellerId());
////            dto.setSellerName(sellerBuyer.getFullName());
////            dto.setSellerName(sellerBuyer.getEmail());
////            dto.setSellerNumber(sellerBuyer.getPhoneNumber());
////            dto.setMaxPricePerTon(sellerProduct.getMaxPricePerTon());
////            dto.setMinPricePerTon(sellerProduct.getMinPricePerTon());
//            dto.setDeliveryCharges(sellerProduct.getDeliveryCharges());
//
//            productDTOS.add(dto);
//        }
        return productDTOS;
    }


    @Override
    public List<SellerReceiveInquiryDTO> sellerAllInquiries(String sellerUId) {
        return sellerNegotiateRepository.findAll().stream()
                .map(this::mapToSellerViewList)
                .filter(sellerReceiveInquiryDTO ->
                sellerReceiveInquiryDTO.getSellerUId().equals(sellerUId))
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
    public SellerReceiveInquiryDTO sellerOpenInquiry(Long snId) throws Exception {
        SellerReceiveInquiryDTO sellerReceiveInquiryDTO = new SellerReceiveInquiryDTO();
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        Inquiry inquiry = inquiryQIdCache.get(sellerNegotiate.getQId());

        sellerReceiveInquiryDTO.setQId(sellerNegotiate.getQId());
        sellerReceiveInquiryDTO.setProductId(sellerReceiveInquiryDTO.getProductId());
        sellerReceiveInquiryDTO.setSellerUId(sellerNegotiate.getSellerUId());
        sellerReceiveInquiryDTO.setSellerName(sellerBuyerRepository.findById(sellerNegotiate.getSellerUId()).orElseThrow(null).getFullName());

        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(null);
        sellerReceiveInquiryDTO.setProductName(product.getProductName());
        sellerReceiveInquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
        sellerReceiveInquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
        sellerReceiveInquiryDTO.setProductFormId(inquiry.getProductFormId());
        sellerReceiveInquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());


        sellerReceiveInquiryDTO.setQuantity(inquiry.getQuantity());
        sellerReceiveInquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        sellerReceiveInquiryDTO.setNpop(inquiry.getNpop());
        sellerReceiveInquiryDTO.setNop(inquiry.getNop());
        sellerReceiveInquiryDTO.setEu(inquiry.getEu());
        sellerReceiveInquiryDTO.setGsdc(inquiry.getGsdc());
        sellerReceiveInquiryDTO.setIpm(inquiry.getIpm());
        sellerReceiveInquiryDTO.setOther(inquiry.getOther());
        sellerReceiveInquiryDTO.setOtherCertification(inquiry.getOtherCertification());
        sellerReceiveInquiryDTO.setPackagingMaterial(inquiry.getPackagingMaterial());
        sellerReceiveInquiryDTO.setAdminInitialPrice(sellerNegotiate.getAdminInitialPrice());
        sellerReceiveInquiryDTO.setAipDate(sellerNegotiate.getAipDate());
        sellerReceiveInquiryDTO.setAipTime(sellerNegotiate.getAipTime());
        sellerReceiveInquiryDTO.setAvgLeadTime(sellerNegotiate.getAvgLeadTime());
        sellerReceiveInquiryDTO.setAdminAddressId(sellerNegotiate.getAdminAddressId());
        sellerReceiveInquiryDTO.setSellerNegotiatePrice(sellerNegotiate.getSellerNegotiatePrice());
        sellerReceiveInquiryDTO.setSnpDate(sellerNegotiate.getSnpDate());
        sellerReceiveInquiryDTO.setSnpTime(sellerNegotiate.getSnpTime());
        sellerReceiveInquiryDTO.setAdminFinalPrice(sellerNegotiate.getAdminFinalPrice());
        sellerReceiveInquiryDTO.setAfpDate(sellerNegotiate.getAfpDate());
        sellerReceiveInquiryDTO.setAfpTime(sellerNegotiate.getAfpTime());
        sellerReceiveInquiryDTO.setStatus(sellerNegotiate.getStatus());


        return sellerReceiveInquiryDTO;
    }

    @Override
    public String sellerNegotiatePrice(Long snId, String sellerUId, Double amount) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        sellerNegotiate.setSellerNegotiatePrice(amount);
        sellerNegotiate.setSnpDate(LocalDate.now());
        sellerNegotiate.setSnpTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sellerNegotiate.setStatus("Seller Negotiated");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You negotiated with amount :" + amount;
    }

    @Override
    public String adminFinalPriceToSeller(Long snId, Double amount) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        sellerNegotiate.setAdminFinalPrice(amount);
        sellerNegotiate.setAfpDate(LocalDate.now());
        sellerNegotiate.setAfpTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sellerNegotiate.setStatus("Admin send the Final price");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You gave the final price of :" + amount;
    }


    @Override
    public String sellerAcceptInquiry(Long snId, String buyerUId) throws Exception {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);

        sellerNegotiate.setStatus("Seller Accepted the Inquiry");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You Accepted the Inquiry";
    }

    @Override
    public String adminSelectsSeller(Long snId) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        Inquiry inquiry = inquiryRepository.findByQId(sellerNegotiate.getQId());

        sellerNegotiate.setStatus("Selected Seller");
        sellerNegotiateRepository.save(sellerNegotiate);

        inquiry.setSellerUId(sellerNegotiate.getSellerUId());
        inquiry.setSentDate(LocalDate.now());
        inquiry.setSentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        inquiry.setSellerFinalPrice(sellerNegotiate.getAdminFinalPrice());
        inquiryRepository.save(inquiry);
        return "Seller with ID : " + sellerNegotiate.getSellerUId() + " is selected for the Query";
    }

    @Override
    public String adminQuoteToBuyer(String qId, InquiryDTO inquiryDTO) {
        Inquiry inquiry = inquiryRepository.findByQId(qId);
        BuyerNegotiate buyerNegotiate = new BuyerNegotiate();
        buyerNegotiate.setQId(qId);
        buyerNegotiate.setBuyerUId(inquiry.getBuyerId());
        buyerNegotiate.setAdminInitialPrice(inquiryDTO.getAdminInitialPrice());
        buyerNegotiate.setComment(inquiryDTO.getComment());
        buyerNegotiate.setAipDate(LocalDate.now());
        buyerNegotiate.setAipTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        buyerNegotiate.setStatus("Admin sent the quotation to buyer.");
        buyerNegotiateRepository.save(buyerNegotiate);
        return "Quotation has been sent to the Buyer of amount : " + inquiryDTO.getAdminInitialPrice();
    }

    @Override
    public String buyerNegotiatePrice(String qId, String buyerUId, Double amount) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setBuyerNegotiatePrice(amount);
        negotiate.setBnpDate(LocalDate.now());
        negotiate.setBnpTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        negotiate.setStatus("Buyer negotiated the price.");
        buyerNegotiateRepository.save(negotiate);

        return "You negotiated with the amount of" + amount;
    }

    @Override
    public String adminFinalPriceToBuyer(String qId, Double amount) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setAdminFinalPrice(amount);
        negotiate.setAfpDate(LocalDate.now());
        negotiate.setAfpTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        negotiate.setStatus("The final amount sent by the admin to the buyer: " + amount);
        buyerNegotiateRepository.save(negotiate);
        return "admin sent the final price to buyer";
    }

    @Override
    public String buyerAcceptQuery(String qId, String buyerUId) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setStatus("Buyer has accepted the query.");
        buyerNegotiateRepository.save(negotiate);

        Inquiry inquiry = inquiryQIdCache.get(qId);
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(7L));
        inquiry.setBuyerFinalPrice(negotiate.getAdminFinalPrice());
        inquiryRepository.save(inquiry);

        return "You accepted the Query";
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
        sampleOrder.setBuyerUId(inquiry.getBuyerId());
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












