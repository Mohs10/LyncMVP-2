package com.example.Lync.ServiceImpl;

import com.example.Lync.Config.S3Service;
import com.example.Lync.DTO.*;
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
    private FormRepository formRepository;
    private VarietyRepository varietyRepository;
    private InquirySpecificationRepository inquirySpecificationRepository;
    private  S3Service s3Service;

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

        Inquiry inquiry = inquiryRepository.findByQId(sellerNegotiate.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sellerNegotiate.getQId()));

        sellerReceiveInquiryDTO.setSnId(sellerNegotiate.getSnId());
        sellerReceiveInquiryDTO.setQId(inquiry.getQId());
        sellerReceiveInquiryDTO.setProductId(inquiry.getProductId());
        sellerReceiveInquiryDTO.setSellerUId(sellerNegotiate.getSellerUId());
        sellerReceiveInquiryDTO.setQuantity(inquiry.getQuantity());
        sellerReceiveInquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
        sellerReceiveInquiryDTO.setAdminInitialPrice(sellerNegotiate.getAdminInitialPrice());
        sellerReceiveInquiryDTO.setAdminAddressId(sellerNegotiate.getAdminAddressId());
        sellerReceiveInquiryDTO.setSellerName(sellerBuyerRepository.findById(sellerNegotiate.getSellerUId()).orElseThrow(null).getFullName());
        sellerReceiveInquiryDTO.setStatus(sellerNegotiate.getStatus());
        sellerReceiveInquiryDTO.setSellerNegotiatePrice(sellerNegotiate.getSellerNegotiatePrice());
        sellerReceiveInquiryDTO.setAdminFinalPrice(sellerNegotiate.getAdminFinalPrice());


        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(null);
        sellerReceiveInquiryDTO.setProductName(product.getProductName());
        sellerReceiveInquiryDTO.setVarietyName(product.getVarieties().stream().map(Variety::getVarietyName).toList().toString());
        sellerReceiveInquiryDTO.setFormName(product.getForms().stream().map(Form::getFormName).toList().toString());
        sellerReceiveInquiryDTO.setProductFormId(inquiry.getProductFormId());
        sellerReceiveInquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());

        // Fetch specifications for the inquiry
        List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

        // Map specifications to SpecificationDTO
        List<SpecificationDTO> specificationDTOs = specifications.stream()
                .map(spec -> {
                    SpecificationDTO dto = new SpecificationDTO();
                    dto.setSpecificationName(spec.getSpecificationName());
                    dto.setSpecificationValue(spec.getSpecificationValue());
                    dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                    return dto;
                })
                .toList();

        sellerReceiveInquiryDTO.setSpecifications(specificationDTOs);

        return sellerReceiveInquiryDTO;
    }

    @Override
    public List<SpecificationDTO> buyerGetsSpecificationsByProductId(Long productId) {
        List<SpecificationDTO> specificationDTOS = new ArrayList<>();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found at ID: " + productId));

        List<Specification> specifications = product.getSpecifications();
        for(Specification specification : specifications){
            SpecificationDTO specificationDTO = new SpecificationDTO();
            specificationDTO.setSpecificationName(specification.getSpecificationName());
            specificationDTO.setSpecificationValue(specification.getSpecificationValue());
            specificationDTO.setSpecificationValueUnits(specification.getSpecificationValueUnits());
            specificationDTOS.add(specificationDTO);
        }
        return specificationDTOS;
    }

    @Override
    public String buyerAddInquiry(InquiryDTO inquiryDTO, String buyerUId) throws Exception { // status - 1
        // Retrieve buyer details from SellerBuyer table
        SellerBuyer buyer = sellerBuyerRepository.findById(buyerUId)
                .orElseThrow(() -> new RuntimeException("Buyer not found with ID: " + buyerUId));

        // Check if cancelledChequeUrl is null
        if (buyer.getCancelledChequeUrl() == null || buyer.getCancelledChequeUrl().isEmpty()) {
            throw new Exception("Inquiry cannot be added because the buyer has not provided a cancelled cheque.");
        }

        // Validate product, form, and variety IDs
        if (!productRepository.existsById(inquiryDTO.getProductId())) {
            throw new RuntimeException("Product not found for ID: " + inquiryDTO.getProductId());
        }
        if (!formRepository.existsById(inquiryDTO.getProductFormId())) {
            throw new RuntimeException("Form not found for ID: " + inquiryDTO.getProductFormId());
        }
        if (!varietyRepository.existsById(inquiryDTO.getProductVarietyId())) {
            throw new RuntimeException("Variety not found for ID: " + inquiryDTO.getProductVarietyId());
        }

        // Create and populate the Inquiry object
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

//        Product product = productRepository.findById(inquiryDTO.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found at ID: " + inquiryDTO.getProductId()));

        List<InquirySpecification> inquirySpecifications = new ArrayList<>();
        List<SpecificationDTO> specifications = inquiryDTO.getSpecifications();
        for(SpecificationDTO specification : specifications){
            InquirySpecification inquirySpecification = new InquirySpecification();
            inquirySpecification.setQId(inquiryId);
            inquirySpecification.setSpecificationName(specification.getSpecificationName());
            inquirySpecification.setSpecificationValue(specification.getSpecificationValue());
            inquirySpecification.setSpecificationValueUnits(specification.getSpecificationValueUnits());
            inquirySpecifications.add(inquirySpecificationRepository.save(inquirySpecification));
        }


        // Product Specification
//        inquiry.setChalkyGrains(inquiryDTO.getChalkyGrains());
//        inquiry.setGrainSize(inquiryDTO.getGrainSize());
//        inquiry.setKettValue(inquiryDTO.getKettValue());
//        inquiry.setMoistureContent(inquiryDTO.getMoistureContent());
//        inquiry.setBrokenGrain(inquiryDTO.getBrokenGrain());
//        inquiry.setAdmixing(inquiryDTO.getAdmixing());
//        inquiry.setDd(inquiryDTO.getDd());

        inquiry.setUnit(inquiryDTO.getUnit());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(1L));
        inquiryRepository.save(inquiry);

        orderStatus.setOId(inquiryId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(1L));
        orderStatus.setDescription(inquiryDTO.getDescription());
        orderStatus.setLocation(inquiryDTO.getLocation());
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setSpecifications(inquirySpecifications);
        inquiryRepository.save(inquiry);

        return "You raised an inquiry.";
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
        // Fetch inquiries for the specific buyer
        List<Inquiry> inquiries = inquiryRepository.findByBuyerId(buyerUId);

        List<InquiryDTO> inquiryDTOS = new ArrayList<>();

        for (Inquiry inquiry : inquiries) {
            try {
                InquiryDTO inquiryDTO = new InquiryDTO();
                inquiryDTO.setQId(inquiry.getQId());
                inquiryDTO.setBuyerUId(inquiry.getBuyerId());
                inquiryDTO.setProductId(inquiry.getProductId());

                // Fetch product from repository
                Product product = productRepository.findById(inquiry.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));

                // Set product-related fields
                inquiryDTO.setProductName(product.getProductName());
                inquiryDTO.setVarietyName(
                        product.getVarieties().stream()
                                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId()))
                                .findFirst()
                                .map(Variety::getVarietyName)
                                .orElse(null)
                );
                inquiryDTO.setFormName(
                        product.getForms().stream()
                                .filter(form -> form.getFormId().equals(inquiry.getProductFormId()))
                                .findFirst()
                                .map(Form::getFormName)
                                .orElse(null)
                );

                inquiryDTO.setProductFormId(inquiry.getProductFormId());
                inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
                inquiryDTO.setOrderStatus(inquiry.getOrderStatus());

                // Fetch specifications for the inquiry
                List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

                // Map specifications to SpecificationDTO
                List<SpecificationDTO> specificationDTOs = specifications.stream()
                        .map(spec -> {
                            SpecificationDTO dto = new SpecificationDTO();
                            dto.setSpecificationName(spec.getSpecificationName());
                            dto.setSpecificationValue(spec.getSpecificationValue());
                            dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                            return dto;
                        })
                        .toList();

                inquiryDTO.setSpecifications(specificationDTOs);

                // Add the InquiryDTO to the list
                inquiryDTOS.add(inquiryDTO);

            } catch (Exception e) {
                // Handle any exceptions and log the error
                System.err.println("Error processing inquiry with QId " + inquiry.getQId() + ": " + e.getMessage());
            }
        }

        return inquiryDTOS;
    }



    @Override
    public InquiryDTO buyerGetsInquiryById(String buyerUId, String qId) {
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        InquiryDTO inquiryDTO = new InquiryDTO();

        //Order Specification
        inquiryDTO.setQId(qId);
        inquiryDTO.setBuyerUId(inquiry.getBuyerId());
        inquiryDTO.setProductId(inquiry.getProductId());
        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with product Id : " + inquiry.getProductId()));
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

// Fetch specifications for the inquiry
        List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

        // Map specifications to SpecificationDTO
        List<SpecificationDTO> specificationDTOs = specifications.stream()
                .map(spec -> {
                    SpecificationDTO dto = new SpecificationDTO();
                    dto.setSpecificationName(spec.getSpecificationName());
                    dto.setSpecificationValue(spec.getSpecificationValue());
                    dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                    return dto;
                })
                .toList();

        inquiryDTO.setSpecifications(specificationDTOs);

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

                // Fetch specifications for the inquiry
                List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

                // Map specifications to SpecificationDTO
                List<SpecificationDTO> specificationDTOs = specifications.stream()
                        .map(spec -> {
                            SpecificationDTO dto = new SpecificationDTO();
                            dto.setSpecificationName(spec.getSpecificationName());
                            dto.setSpecificationValue(spec.getSpecificationValue());
                            dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                            return dto;
                        })
                        .toList();

                inquiryDTO.setSpecifications(specificationDTOs);

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
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));

        return mapToAdminDTO(inquiry);
    }


    private InquiryDTO mapToAdminDTO(Inquiry inquiry) {
        InquiryDTO inquiryDTO = new InquiryDTO();
        inquiryDTO.setQId(inquiry.getQId());
        inquiryDTO.setBuyerUId(inquiry.getBuyerId());
        inquiryDTO.setProductId(inquiry.getProductId());

        Product product = productRepository.findById(inquiry.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
        inquiryDTO.setProductName(product.getProductName());
        inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
        inquiryDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
        inquiryDTO.setProductFormId(inquiry.getProductFormId());
        inquiryDTO.setFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());

        // Map other inquiry fields
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
        inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
        inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
        inquiryDTO.setSellerUId(inquiry.getSellerUId());
        inquiryDTO.setSellerFinalPrice(inquiry.getSellerFinalPrice());
        inquiryDTO.setSentDate(inquiry.getSentDate());
        inquiryDTO.setSentTime(inquiry.getSentTime());
        inquiryDTO.setUnit(inquiry.getUnit());

        // Fetch specifications for the inquiry
        List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

        // Map specifications to SpecificationDTO
        List<SpecificationDTO> specificationDTOs = specifications.stream()
                .map(spec -> {
                    SpecificationDTO dto = new SpecificationDTO();
                    dto.setSpecificationName(spec.getSpecificationName());
                    dto.setSpecificationValue(spec.getSpecificationValue());
                    dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                    return dto;
                })
                .toList();

        inquiryDTO.setSpecifications(specificationDTOs);


        // Fetch OrderStatus
        OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
        inquiryDTO.setOsId(orderStatus.getOsId());
        inquiryDTO.setDate(orderStatus.getDate());
        inquiryDTO.setTime(orderStatus.getTime());
        inquiryDTO.setDescription(orderStatus.getDescription());
        inquiryDTO.setImageUrl(orderStatus.getImageUrl());
        inquiryDTO.setLocation(orderStatus.getLocation());

        // Fetch and map SellerNegotiates
        List<SellerNegotiate> negotiations = sellerNegotiateRepository.findByQId(inquiry.getQId());
        List<SellerNegotiateDTO> negotiationDTOs = negotiations.stream()
                .flatMap(neg -> {
                    // Fetch all SellerProducts for the given sellerId and productId
                    List<SellerProduct> sellerProducts = sellerProductRepository.findBySellerIdAndProductId(neg.getSellerUId(), inquiry.getProductId());

                    if (sellerProducts.isEmpty()) {
                        throw new RuntimeException("SellerProduct not found for sellerId: " + neg.getSellerUId() + " and productId: " + inquiry.getProductId());
                    }

                    // For each SellerProduct, create a SellerNegotiateDTO
                    return sellerProducts.stream().map(sellerProduct -> {
                        SellerNegotiateDTO dto = new SellerNegotiateDTO();
                        dto.setSellerUId(neg.getSellerUId());

                        // Fetch and map seller details
                        SellerBuyer seller = sellerBuyerRepository.findById(neg.getSellerUId())
                                .orElseThrow(() -> new RuntimeException("Seller not found for ID: " + neg.getSellerUId()));
                        dto.setSellerName(seller.getFullName());
                        dto.setEmail(seller.getEmail());
                        dto.setPhoneNumber(seller.getPhoneNumber());
                        dto.setAdminCountry(seller.getCountry());
                        dto.setAdminState(seller.getState());
                        dto.setAdminCity(seller.getCity());
                        dto.setAdminPinCode(seller.getPinCode());
                        dto.setAdminAddress(seller.getAddress());

                        // Set specific SellerProduct details
                        dto.setAvailableAmount(sellerProduct.getAvailableAmount());
                        dto.setMaxPrice(sellerProduct.getMaxPrice());
                        dto.setMinPrice(sellerProduct.getMinPrice());
                        dto.setOriginOfProduce(sellerProduct.getOriginOfProduce());

                        // Map negotiation-specific fields
                        dto.setSnId(neg.getSnId());
                        dto.setAdminInitialPrice(neg.getAdminInitialPrice());
                        dto.setAipDate(neg.getAipDate());
                        dto.setAipTime(neg.getAipTime());
                        dto.setAvgLeadTime(neg.getAvgLeadTime());
                        dto.setAdminDeliveryAddress(neg.getAdminDeliveryAddress());
                        dto.setInstruction(neg.getInstruction());
                        dto.setSellerNegotiatePrice(neg.getSellerNegotiatePrice());
                        dto.setSnpDate(neg.getSnpDate());
                        dto.setSnpTime(neg.getSnpTime());
                        dto.setAdminFinalPrice(neg.getAdminFinalPrice());
                        dto.setAfpDate(neg.getAfpDate());
                        dto.setAfpTime(neg.getAfpTime());
                        dto.setStatus(neg.getStatus());
                        dto.setAdminAddressId(neg.getAdminAddressId());
                        return dto;
                    });
                })
                .toList();
        inquiryDTO.setSellerNegotiations(negotiationDTOs);

        // Fetch BuyerNegotiate entity
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(inquiry.getQId());

        if (negotiate != null) {
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
            inquiryDTO.setAfpTime(negotiate.getAfpTime());
            inquiryDTO.setStatus(negotiate.getStatus());
        } else {
            // Set fields to null or provide default values
            inquiryDTO.setAdminInitialPrice(null);
            inquiryDTO.setComment(null);
            inquiryDTO.setPaymentTerm(null);
            inquiryDTO.setAipDate(null);
            inquiryDTO.setAipTime(null);
            inquiryDTO.setBuyerNegotiatePrice(null);
            inquiryDTO.setBnpDate(null);
            inquiryDTO.setBnpTime(null);
            inquiryDTO.setAdminFinalPrice(null);
            inquiryDTO.setAfpDate(null);
            inquiryDTO.setAfpTime(null);
            inquiryDTO.setStatus(null);
        }

        return inquiryDTO;
    }





//    private InquiryDTO mapToAdminDTO(Inquiry inquiry){
//        System.out.println("map to dto Inquiry" + inquiry);
//        InquiryDTO inquiryDTO = new InquiryDTO();
//        System.out.println("map to Dto InquiryDTO" + inquiryDTO);
//        inquiryDTO.setQId(inquiry.getQId());
//        inquiryDTO.setBuyerUId(inquiry.getBuyerId());
//        inquiryDTO.setProductId(inquiry.getProductId());
//
//        Product product = productRepository.findById(inquiry.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + inquiry.getProductId()));
//        System.out.println("maptoDto" + product);
//        inquiryDTO.setProductName(product.getProductName());
//        inquiryDTO.setProductVarietyId(inquiry.getProductVarietyId());
//        inquiryDTO.setVarietyName(product.getVarieties().stream()
//                .filter(variety -> variety.getVarietyId().equals(inquiry.getProductVarietyId())).findFirst()
//                .orElseThrow(() -> new RuntimeException("Product variety not found with ID: " + inquiry.getProductVarietyId())).getVarietyName());
//        inquiryDTO.setProductFormId(inquiry.getProductFormId());
//        inquiryDTO.setFormName(product.getForms().stream()
//                .filter(form -> form.getFormId().equals(inquiry.getProductFormId())).findFirst()
//                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + inquiry.getProductFormId())).getFormName());
//
//
//        //Order Specification
//        inquiryDTO.setQuantity(inquiry.getQuantity());
//        inquiryDTO.setQuantityUnit(inquiry.getQuantityUnit());
//        inquiryDTO.setPriceTerms(inquiry.getPriceTerms());
////        inquiryDTO.setCertificate(inquiry.getCertificate());
//        inquiryDTO.setAskMinPrice(inquiry.getAskMinPrice());
//        inquiryDTO.setAskMaxPrice(inquiry.getAskMaxPrice());
//        inquiryDTO.setPriceUnit(inquiry.getPriceUnit());
//        inquiryDTO.setNpop(inquiry.getNpop());
//        inquiryDTO.setNop(inquiry.getNop());
//        inquiryDTO.setEu(inquiry.getEu());
//        inquiryDTO.setGsdc(inquiry.getGsdc());
//        inquiryDTO.setIpm(inquiry.getIpm());
//        inquiryDTO.setOther(inquiry.getOther());
//        inquiryDTO.setOtherCertification(inquiry.getOtherCertification());
//        inquiryDTO.setPackagingMaterial(inquiry.getPackagingMaterial());
//        inquiryDTO.setPaymentTerms(inquiry.getPaymentTerms());
//        inquiryDTO.setTargetLeadTime(inquiry.getTargetLeadTime());
//        inquiryDTO.setDeliveryAddress(inquiry.getDeliveryAddress());
//        inquiryDTO.setCountry(inquiry.getCountry());
//        inquiryDTO.setState(inquiry.getState());
//        inquiryDTO.setCity(inquiry.getCity());
//        inquiryDTO.setPincode(inquiry.getPincode());
//        inquiryDTO.setSpecifyDeliveryDate(inquiry.getSpecifyDeliveryDate());
//
// Fetch specifications for the inquiry
//        List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());
//        System.out.println(specifications);
//
//        // Map specifications to SpecificationDTO
//        List<SpecificationDTO> specificationDTOs = specifications.stream()
//                .map(spec -> {
//                    SpecificationDTO dto = new SpecificationDTO();
//                    dto.setSpecificationName(spec.getSpecificationName());
//                    dto.setSpecificationValue(spec.getSpecificationValue());
//                    dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
//                    return dto;
//                })
//                .toList();
//
//        inquiryDTO.setSpecifications(specificationDTOs);
//
//        inquiryDTO.setRaiseDate(inquiry.getRaiseDate());
//        inquiryDTO.setRaiseTime(inquiry.getRaiseTime());
//        inquiryDTO.setOrderStatus(inquiry.getOrderStatus());
//        inquiryDTO.setSellerUId(inquiry.getSellerUId());
//        inquiryDTO.setSellerFinalPrice(inquiry.getSellerFinalPrice());
//        inquiryDTO.setSentDate(inquiry.getSentDate());
//        inquiryDTO.setSentTime(inquiry.getSentTime());
//        inquiryDTO.setUnit(inquiry.getUnit());
//
//        OrderStatus orderStatus = orderStatusRepository.findByOsId(inquiry.getOsId());
//        inquiryDTO.setOsId(orderStatus.getOsId());
//        inquiryDTO.setDate(orderStatus.getDate());
//        inquiryDTO.setTime(orderStatus.getTime());
//        inquiryDTO.setDescription(orderStatus.getDescription());
//        inquiryDTO.setImageUrl(orderStatus.getImageUrl());
//        inquiryDTO.setLocation(orderStatus.getLocation());
//        System.out.println(orderStatus);
//
//        // Fetch SellerNegotiates and map them
//        List<SellerNegotiate> negotiations = sellerNegotiateRepository.findByQId(inquiry.getQId());
//
//
//
//        List<SellerNegotiateDTO> negotiationDTOs = negotiations.stream()
//                .map(neg -> {
//                    SellerNegotiateDTO dto = new SellerNegotiateDTO();
//                    dto.setSellerUId(neg.getSellerUId());
//                    SellerBuyer seller = sellerBuyerRepository.findById(neg.getSellerUId())
//                            .orElseThrow(() -> new RuntimeException("Seller not found with given ID : " + neg.getSellerUId()));
//                    dto.setSellerName(seller.getFullName());
//                    dto.setEmail(seller.getEmail());
//                    dto.setPhoneNumber(seller.getPhoneNumber());
//                    dto.setAdminCountry(seller.getCountry());
//                    dto.setAdminState(seller.getState());
//                    dto.setAdminCity(seller.getCity());
//                    dto.setAdminPinCode(seller.getPinCode());
//                    dto.setAdminAddress(seller.getAddress());
//
//                    SellerProduct sellerProduct = sellerProductRepository.findBySellerIdAndProductId(neg.getSellerUId(), inquiry.getProductId())
//                            .orElseThrow(() -> new RuntimeException("SellerProduct not found."));
//                    System.out.println(sellerProduct);
//                    dto.setAvailableAmount(sellerProduct.getAvailableAmount());
//                    dto.setMaxPrice(sellerProduct.getMaxPrice());
//                    dto.setMinPrice(sellerProduct.getMinPrice());
//
//                    dto.setSnId(neg.getSnId());
//                    dto.setAdminInitialPrice(neg.getAdminInitialPrice());
//                    dto.setAipDate(neg.getAipDate());
//                    dto.setAipTime(neg.getAipTime());
//                    dto.setAvgLeadTime(neg.getAvgLeadTime());
//                    dto.setAdminDeliveryAddress(neg.getAdminDeliveryAddress());
//                    dto.setInstruction(neg.getInstruction());
//                    dto.setSellerNegotiatePrice(neg.getSellerNegotiatePrice());
//                    dto.setSnpDate(neg.getSnpDate());
//                    dto.setSnpTime(neg.getSnpTime());
//                    dto.setAdminFinalPrice(neg.getAdminFinalPrice());
//                    dto.setAfpDate(neg.getAfpDate());
//                    dto.setAfpTime(neg.getAfpTime());
//                    dto.setStatus(neg.getStatus());
//                    dto.setAdminAddressId(neg.getAdminAddressId());
//                    return dto;
//                })
//                .toList();
//        inquiryDTO.setSellerNegotiations(negotiationDTOs);
//
//
//
       // Fetch BuyerNegotiate entity
//        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(inquiry.getQId());
//        System.out.println(negotiate);
//
//        if (negotiate != null) {
//            inquiryDTO.setAdminInitialPrice(negotiate.getAdminInitialPrice());
//            inquiryDTO.setComment(negotiate.getComment());
//            inquiryDTO.setPaymentTerm(negotiate.getPaymentTerm());
//            inquiryDTO.setAipDate(negotiate.getAipDate());
//            inquiryDTO.setAipTime(negotiate.getAipTime());
//            inquiryDTO.setBuyerNegotiatePrice(negotiate.getBuyerNegotiatePrice());
//            inquiryDTO.setBnpDate(negotiate.getBnpDate());
//            inquiryDTO.setBnpTime(negotiate.getBnpTime());
//            inquiryDTO.setAdminFinalPrice(negotiate.getAdminFinalPrice());
//            inquiryDTO.setAfpDate(negotiate.getAfpDate());
//            inquiryDTO.setAfpTime(negotiate.getAfpTime());
//            inquiryDTO.setStatus(negotiate.getStatus());
//        } else {
//            // Set fields to null or provide default values
//            inquiryDTO.setAdminInitialPrice(null);
//            inquiryDTO.setComment(null);
//            inquiryDTO.setPaymentTerm(null);
//            inquiryDTO.setAipDate(null);
//            inquiryDTO.setAipTime(null);
//            inquiryDTO.setBuyerNegotiatePrice(null);
//            inquiryDTO.setBnpDate(null);
//            inquiryDTO.setBnpTime(null);
//            inquiryDTO.setAdminFinalPrice(null);
//            inquiryDTO.setAfpDate(null);
//            inquiryDTO.setAfpTime(null);
//            inquiryDTO.setStatus(null);
//        }
//
//
//
//        System.out.println("map to Dto InquiryDTO" + inquiryDTO);
//        return inquiryDTO;
//
//    }



    @Override
    public String sendInquiryToSeller(String qId, InquiryDTO inquiryDTO) { //status - 2
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));

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
                    sellerNegotiate.setAvgLeadTime(inquiryDTO.getAvgLeadTime());
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
    public List<SellerProductDTO> sellersSellingProduct(Long productId, Long productFormId, Long productVarietyId, List<String> specificationNames) {
        List<SellerProduct> sellerProducts = new ArrayList<>();
        sellerProducts = sellerProductRepository.findBySpecificationAndProductAttributes(specificationNames, productId, productFormId, productVarietyId);

        if(sellerProducts.isEmpty()) {
            sellerProducts = sellerProductRepository
                    .findByProductIdAndProductFormIdAndProductVarietyId(productId, productFormId, productVarietyId);
        }
        if(sellerProducts.isEmpty()){
            sellerProducts = sellerProductRepository.findByProductIdAndProductVarietyId(productId, productVarietyId);
        }

        if(sellerProducts.isEmpty()){
            sellerProducts = sellerProductRepository.findByPId(productId);
        }

        return sellerProducts.stream()
                .map(sellerBuyerService::toDTO) // Method reference
                .collect(Collectors.toList());

    }
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
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));

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
    public SellerReceiveInquiryDTO sellerOpenInquiry(Long snId, String sellerUId) throws Exception {
        SellerReceiveInquiryDTO sellerReceiveInquiryDTO = new SellerReceiveInquiryDTO();
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        Inquiry inquiry = inquiryRepository.findByQId(sellerNegotiate.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Id : " + sellerNegotiate.getQId()));

        sellerReceiveInquiryDTO.setQId(sellerNegotiate.getQId());
        sellerReceiveInquiryDTO.setSnId(sellerNegotiate.getSnId());
        sellerReceiveInquiryDTO.setProductId(inquiry.getProductId());
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

        // Fetch specifications for the inquiry
        List<InquirySpecification> specifications = inquirySpecificationRepository.findByQId(inquiry.getQId());

        // Map specifications to SpecificationDTO
        List<SpecificationDTO> specificationDTOs = specifications.stream()
                .map(spec -> {
                    SpecificationDTO dto = new SpecificationDTO();
                    dto.setSpecificationName(spec.getSpecificationName());
                    dto.setSpecificationValue(spec.getSpecificationValue());
                    dto.setSpecificationValueUnits(spec.getSpecificationValueUnits());
                    return dto;
                })
                .toList();

        sellerReceiveInquiryDTO.setSpecifications(specificationDTOs);

        return sellerReceiveInquiryDTO;
    }

    @Override
    public String sellerRejectQuery(Long snId, String sellerUId) throws Exception {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId)
                .orElseThrow(()-> new RuntimeException("Negotiation not found with given Id : " + snId));

        sellerNegotiate.setStatus("Seller Rejected the Inquiry");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You Rejected the Inquiry";
    }

    @Override
    public String sellerAcceptInquiry(Long snId, String buyerUId) throws Exception {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId)
                .orElseThrow(()-> new RuntimeException("Negotiation not found with given Id : " + snId));

        sellerNegotiate.setStatus("Seller Accepted the Inquiry");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You Accepted the Inquiry";
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
    public String sellerAcceptAdminPrice(Long snId, String sellerUId) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId)
                .orElseThrow(()-> new RuntimeException("Negotiation not found with given Id : " + snId));

        sellerNegotiate.setStatus("Seller Accepted Admin Price");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You Accepted the Admin Price";
    }

    @Override
    public String sellerRejectAdminPrice(Long snId, String sellerUId) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId)
                .orElseThrow(()-> new RuntimeException("Negotiation not found with given Id : " + snId));

        sellerNegotiate.setStatus("Seller Rejected Admin Price");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "You Rejected the Admin Price";
    }

    @Override
    public String adminSelectsSeller(Long snId) {
        // Fetch the SellerNegotiate entity by snId or throw an exception if not found
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId)
                .orElseThrow(() -> new RuntimeException("SellerNegotiate not found with ID: " + snId));

        // Fetch the Inquiry entity associated with the QId
        Inquiry inquiry = inquiryRepository.findByQId(sellerNegotiate.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sellerNegotiate.getQId()));


        // Check if a seller has already been selected for the inquiry
        if (inquiry.getSellerUId() != null) {
            throw new RuntimeException("Seller with ID: " + inquiry.getSellerUId()
                    + " has already been selected for this inquiry. "
                    + "Seller ID: " + sellerNegotiate.getSellerUId() + " cannot be selected.");
        }

        // Update the SellerNegotiate entity to "Selected Seller"
        sellerNegotiate.setStatus("Selected Seller");
        sellerNegotiateRepository.save(sellerNegotiate);

        // Update the Inquiry entity with the selected seller details
        inquiry.setSellerUId(sellerNegotiate.getSellerUId());
        inquiry.setSentDate(LocalDate.now());
        inquiry.setSentTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));

        // Set the final price in the Inquiry based on availability of Admin's final price
        if (sellerNegotiate.getAdminFinalPrice() != null) {
            inquiry.setSellerFinalPrice(sellerNegotiate.getAdminFinalPrice());
        } else {
            inquiry.setSellerFinalPrice(sellerNegotiate.getSellerNegotiatePrice());
        }

        // Create and save the OrderStatus entity
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sellerNegotiate.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(19L)); // Assuming 19L corresponds to the required status
        orderStatusRepository.save(orderStatus);

        // Update the Inquiry with the new order status
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(19L));
        inquiryRepository.save(inquiry);

        return "Seller with ID: " + sellerNegotiate.getSellerUId() + " is selected for the Query.";
    }


    @Override
    public String adminRejectSeller(Long snId) {
        SellerNegotiate sellerNegotiate = sellerNegotiateRepository.findById(snId).orElseThrow(null);
        sellerNegotiate.setStatus("Admin Rejected for Query");
        sellerNegotiateRepository.save(sellerNegotiate);
        return "Seller with ID : " + sellerNegotiate.getSellerUId() + " is rejected for the Query";
    }

    @Override
    public String adminQuoteToBuyer(String qId, InquiryDTO inquiryDTO) {
        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        BuyerNegotiate buyerNegotiate = new BuyerNegotiate();
        buyerNegotiate.setQId(qId);
        buyerNegotiate.setBuyerUId(inquiry.getBuyerId());
        buyerNegotiate.setAdminInitialPrice(inquiryDTO.getAdminInitialPrice());
        buyerNegotiate.setComment(inquiryDTO.getComment());
        buyerNegotiate.setPaymentTerm(inquiryDTO.getPaymentTerm());
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
        negotiate.setStatus("Buyer negotiated the price");
        buyerNegotiateRepository.save(negotiate);

        return "You negotiated with the amount of " + amount;
    }

    @Override
    public String adminFinalPriceToBuyer(String qId, Double amount) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setAdminFinalPrice(amount);
        negotiate.setAfpDate(LocalDate.now());
        negotiate.setAfpTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        negotiate.setStatus("Admin Sent the Final Price");
        buyerNegotiateRepository.save(negotiate);
        return "You sent the final price to buyer";
    }

    @Override
    public String buyerAcceptQuery(String qId, String buyerUId) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setStatus("Buyer has accepted the query.");
        buyerNegotiateRepository.save(negotiate);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(7L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(7L));
        if(negotiate.getAdminFinalPrice() != null) {
            inquiry.setBuyerFinalPrice(negotiate.getAdminFinalPrice());
        } else {
            inquiry.setBuyerFinalPrice(negotiate.getAdminInitialPrice());
        }
        inquiryRepository.save(inquiry);

        return "You accepted the Query";
    }

    @Override
    public String buyerRejectedQuery(String qId, String buyerUId) {
        BuyerNegotiate negotiate = buyerNegotiateRepository.findByQId(qId);
        negotiate.setStatus("Buyer has rejected the query.");
        buyerNegotiateRepository.save(negotiate);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(6L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(6L));
        inquiryRepository.save(inquiry);

        return "You rejected the Query";
    }


//    @Override
//    public void sellerOrderSample(String qId, SampleOrderDTO sampleOrderDTO) throws Exception {
//        SampleOrder sampleOrder = new SampleOrder();
//        OrderStatus orderStatus = new OrderStatus();
////        Inquiry inquiry = inquiryRepository.findByQId(qId);
//        Inquiry inquiry = inquiryQIdCache.get(qId);
//        if(inquiry == null){
//            throw new Exception("Inquiry not found with qId: " + qId);
//        }
//        LocalDate currentDate = LocalDate.now();
//
//        Long count = sampleOrderRepository.countSampleOrderByCurrentDate(currentDate);
//        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String nextInquiryNumber = String.format("%03d", count + 1);
//        String soId = "SO" + formattedDate + nextInquiryNumber;
//
//        sampleOrder.setSoId(soId);
//        sampleOrder.setQId(qId);
//        sampleOrder.setBuyerUId(inquiry.getBuyerId());
//        sampleOrder.setSellerUId(inquiry.getSellerUId());
//        sampleOrder.setProductId(inquiry.getProductId());
//
////        sampleOrder.setStockLocation(sampleOrderDTO.getStockLocation());
//        sampleOrderRepository.save(sampleOrder);
//
//        orderStatus.setOId(qId);
//        orderStatus.setStatus(statusRepository.findSMeaningBySId(5L));
//        orderStatus.setDescription(sampleOrderDTO.getDescription());
//        orderStatusRepository.save(orderStatus);
//
//        inquiry.setOsId(orderStatus.getOsId());
//        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(5L));
//        inquiryRepository.save(inquiry);
//
//    }

    @Override
    public String buyerRequestSample(String qId, String buyerUId, SampleOrderDTO sampleOrderDTO) throws Exception {

        // Check if a SampleOrder already exists for the given qId and buyerUId
        boolean exists = sampleOrderRepository.existsByQIdAndBuyerUId(qId, buyerUId);
        if (exists) {
            throw new RuntimeException("Buyer already Requested for sample for this Inquiry.");
        }

        SampleOrder sampleOrder = new SampleOrder();
        OrderStatus orderStatus = new OrderStatus();

        Inquiry inquiry = inquiryRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + qId));



        LocalDate currentDate = LocalDate.now();

        Long count = sampleOrderRepository.countSampleOrderByCurrentDate(currentDate);
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nextInquiryNumber = String.format("%03d", count + 1);
        String soId = "SO" + formattedDate + nextInquiryNumber;

        sampleOrder.setSoId(soId);
        sampleOrder.setQId(qId);
        sampleOrder.setBuyerUId(inquiry.getBuyerId());
//        sampleOrder.setSellerUId(inquiry.getSellerUId()); //excluded because; without admin send SO to seller, seller will get notified of the SO.

        sampleOrder.setProductId(inquiry.getProductId());
        sampleOrder.setProductFormId(inquiry.getProductFormId());
        sampleOrder.setProductVarietyId(inquiry.getProductVarietyId());

        sampleOrder.setBuyerQuantity(sampleOrderDTO.getBuyerQuantity());
        sampleOrder.setBuyerUnit(sampleOrderDTO.getBuyerUnit());
        sampleOrder.setBuyerAddressId(sampleOrderDTO.getBuyerAddressId());
        sampleOrder.setBuyerAmount(sampleOrderDTO.getBuyerAmount());
        sampleOrder.setBuyerRequestDate(LocalDate.now());
        sampleOrder.setBuyerRequestTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Buyer Requested for Sample");
        sampleOrderRepository.save(sampleOrder);

        orderStatus.setOId(qId);
        orderStatus.setStatus(statusRepository.findSMeaningBySId(5L));
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(5L));
        inquiryRepository.save(inquiry);

        return "You requested for sample.";
    }

    @Override
    public List<SampleOrderDTO> buyerGetsAllSampleOrders(String buyerUId) {
        return sampleOrderRepository.findAllByBuyerUId(buyerUId).stream()
                .map(sampleOrder -> {
                    SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
                    sampleOrderDTO.setSoId(sampleOrder.getSoId());
                    sampleOrderDTO.setQId(sampleOrder.getQId());
                    sampleOrderDTO.setBuyerUId(sampleOrder.getBuyerUId());
                    String buyerName = sellerBuyerRepository.findById(sampleOrder.getBuyerUId())
                            .orElseThrow(() -> new RuntimeException("Buyer no found with Id : " + sampleOrder.getBuyerUId()))
                                    .getFullName();
                    sampleOrderDTO.setBuyerName(buyerName);

                    Product product = productRepository.findById(sampleOrder.getProductId())
                                    .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

                    sampleOrderDTO.setProductId(sampleOrder.getProductId());
                    sampleOrderDTO.setProductName(product.getProductName());
                    sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
                    sampleOrderDTO.setProductFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
                    sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
                    sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

                    sampleOrderDTO.setBuyerQuantity(sampleOrder.getBuyerQuantity());
                    sampleOrderDTO.setBuyerUnit(sampleOrder.getBuyerUnit());
                    sampleOrderDTO.setBuyerAddressId(sampleOrder.getBuyerAddressId());
                    sampleOrderDTO.setBuyerAmount(sampleOrder.getBuyerAmount());

                    sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
                    sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());



                    sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());

                return sampleOrderDTO;
                })
        .toList();
    }

    @Override
    public SampleOrderDTO buyerGetsSampleOrderById(String soId, String buyerUId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));
        if(! sampleOrder.getBuyerUId().equals(buyerUId)){
            throw new RuntimeException("Access denied! This SampleOrder does not belong to the buyer with ID: " + buyerUId);
        }
        SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
        sampleOrderDTO.setSoId(sampleOrder.getSoId());
        sampleOrderDTO.setQId(sampleOrder.getQId());
        sampleOrderDTO.setBuyerUId(sampleOrder.getBuyerUId());
        String buyerName = sellerBuyerRepository.findById(buyerUId)
                .orElseThrow(() -> new RuntimeException("Buyer no found with Id : " + sampleOrder.getBuyerUId()))
                .getFullName();
        sampleOrderDTO.setBuyerName(buyerName);

        Product product = productRepository.findById(sampleOrder.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + sampleOrder.getProductId()));
        sampleOrderDTO.setProductId(sampleOrder.getProductId());
        sampleOrderDTO.setProductName(product.getProductName());
        sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
        sampleOrderDTO.setProductFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
        sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
        sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

        sampleOrderDTO.setBuyerQuantity(sampleOrder.getBuyerQuantity());
        sampleOrderDTO.setBuyerUnit(sampleOrder.getBuyerUnit());
        sampleOrderDTO.setBuyerAddressId(sampleOrder.getBuyerAddressId());
        sampleOrderDTO.setBuyerAmount(sampleOrder.getBuyerAmount());
        sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
        sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());

        sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
        sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());
        sampleOrderDTO.setAdminEDDToSeller(sampleOrder.getAdminEDDToSeller());
//
//        sampleOrderDTO.setSellerRespondDate(sampleOrder.getSellerRespondDate());
//        sampleOrderDTO.setSellerRespondTime(sampleOrder.getSellerRespondTime());

        sampleOrderDTO.setSellerPackagingDate(sampleOrder.getSellerPackagingDate());
        sampleOrderDTO.setSellerPackagingTime(sampleOrder.getSellerPackagingTime());

        sampleOrderDTO.setSellerDispatchDate(sampleOrder.getSellerDispatchDate());
        sampleOrderDTO.setSellerDispatchTime(sampleOrder.getSellerDispatchTime());

        sampleOrderDTO.setAdminReceiveDate(sampleOrder.getAdminReceiveDate());
        sampleOrderDTO.setAdminReceiveTime(sampleOrder.getAdminReceiveTime());

        sampleOrderDTO.setAdminProcessingDate(sampleOrder.getAdminProcessingDate());
        sampleOrderDTO.setAdminProcessingTime(sampleOrder.getAdminProcessingTime());

        sampleOrderDTO.setAdminDispatchDate(sampleOrder.getAdminDispatchDate());
        sampleOrderDTO.setAdminDispatchTime(sampleOrder.getAdminDispatchTime());

        sampleOrderDTO.setBuyerReceiveDate(sampleOrder.getBuyerReceiveDate());
        sampleOrderDTO.setBuyerReceiveTime(sampleOrder.getBuyerReceiveTime());

        sampleOrderDTO.setBuyerApproveDate(sampleOrder.getBuyerApproveDate());
        sampleOrderDTO.setBuyerApproveTime(sampleOrder.getBuyerApproveTime());

        sampleOrderDTO.setBuyerRejectDate(sampleOrder.getBuyerRejectDate());
        sampleOrderDTO.setBuyerRejectTime(sampleOrder.getBuyerRejectTime());

        sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());


        return sampleOrderDTO;
    }

    @Override
    public List<SampleOrderDTO> adminGetsAllSampleOrders() {
        return sampleOrderRepository.findAll().stream()
                .map(sampleOrder -> {
                    SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
                    sampleOrderDTO.setSoId(sampleOrder.getSoId());
                    sampleOrderDTO.setQId(sampleOrder.getQId());
                    sampleOrderDTO.setBuyerUId(sampleOrder.getBuyerUId());
                    sampleOrderDTO.setSellerUId(sampleOrder.getSellerUId());
                    String buyerName = sellerBuyerRepository.findById(sampleOrder.getBuyerUId())
                            .orElseThrow(() -> new RuntimeException("Buyer not found with Id : " + sampleOrder.getBuyerUId()))
                            .getFullName();
                    sampleOrderDTO.setBuyerName(buyerName);

                    if (sampleOrder.getSellerUId() != null) {
                        String sellerName = sellerBuyerRepository.findById(sampleOrder.getSellerUId())
                                .orElseThrow(() -> new RuntimeException("Seller not found with Id: " + sampleOrder.getSellerUId()))
                                .getFullName();
                        sampleOrderDTO.setSellerName(sellerName);
                    } else {
                        sampleOrderDTO.setSellerName("N/A"); // Default or fallback value
                    }

                    Product product = productRepository.findById(sampleOrder.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

                    sampleOrderDTO.setProductId(sampleOrder.getProductId());
                    sampleOrderDTO.setProductName(product.getProductName());
                    sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
                    sampleOrderDTO.setProductFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
                    sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
                    sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

                    sampleOrderDTO.setBuyerQuantity(sampleOrder.getBuyerQuantity());
                    sampleOrderDTO.setBuyerAddressId(sampleOrder.getBuyerAddressId());
                    sampleOrderDTO.setBuyerAmount(sampleOrder.getBuyerAmount());
                    sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
                    sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());

                    sampleOrderDTO.setAdminSendQtyToSeller(sampleOrder.getAdminSendQtyToSeller());
                    sampleOrderDTO.setAdminAddressId(sampleOrder.getAdminAddressId());
                    sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
                    sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());

                    sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());

                    return sampleOrderDTO;
                })
        .toList();
    }

    @Override
    public SampleOrderDTO adminGetsSampleOrderById(String soId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
        sampleOrderDTO.setSoId(sampleOrder.getSoId());
        sampleOrderDTO.setQId(sampleOrder.getQId());
        sampleOrderDTO.setBuyerUId(sampleOrder.getBuyerUId());
        sampleOrderDTO.setSellerUId(sampleOrder.getSellerUId());
        String buyerName = sellerBuyerRepository.findById(sampleOrder.getBuyerUId())
                .orElseThrow(() -> new RuntimeException("Buyer not found with Id : " + sampleOrder.getBuyerUId()))
                .getFullName();
        sampleOrderDTO.setBuyerName(buyerName);
        if (sampleOrder.getSellerUId() != null) {
            String sellerName = sellerBuyerRepository.findById(sampleOrder.getSellerUId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with Id: " + sampleOrder.getSellerUId()))
                    .getFullName();
            sampleOrderDTO.setSellerName(sellerName);
        } else {
            sampleOrderDTO.setSellerName("N/A"); // Default or fallback value
        }

        Product product = productRepository.findById(sampleOrder.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

        sampleOrderDTO.setProductId(sampleOrder.getProductId());
        sampleOrderDTO.setProductName(product.getProductName());
        sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
        sampleOrderDTO.setProductFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
        sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
        sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

        sampleOrderDTO.setBuyerQuantity(sampleOrder.getBuyerQuantity());
        sampleOrderDTO.setBuyerUnit(sampleOrder.getBuyerUnit());
        sampleOrderDTO.setBuyerAddressId(sampleOrder.getBuyerAddressId());
        sampleOrderDTO.setBuyerAmount(sampleOrder.getBuyerAmount());
        sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
        sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());

        sampleOrderDTO.setAdminSendQtyToSeller(sampleOrder.getAdminSendQtyToSeller());
        sampleOrderDTO.setAdminUnit(sampleOrder.getAdminUnit());
        sampleOrderDTO.setAdminAddressId(sampleOrder.getAdminAddressId());
        sampleOrderDTO.setAdminEDDToSeller(sampleOrder.getAdminEDDToSeller());
        sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
        sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());
//
//        sampleOrderDTO.setSellerRespondDate(sampleOrder.getSellerRespondDate());
//        sampleOrderDTO.setSellerRespondTime(sampleOrder.getSellerRespondTime());

        sampleOrderDTO.setSellerPackagingDate(sampleOrder.getSellerPackagingDate());
        sampleOrderDTO.setSellerPackagingTime(sampleOrder.getSellerPackagingTime());

        sampleOrderDTO.setSellerDispatchDate(sampleOrder.getSellerDispatchDate());
        sampleOrderDTO.setSellerDispatchTime(sampleOrder.getSellerDispatchTime());

        sampleOrderDTO.setAdminReceiveDate(sampleOrder.getAdminReceiveDate());
        sampleOrderDTO.setAdminReceiveTime(sampleOrder.getAdminReceiveTime());

        sampleOrderDTO.setAdminProcessingDate(sampleOrder.getAdminProcessingDate());
        sampleOrderDTO.setAdminProcessingTime(sampleOrder.getAdminProcessingTime());

        sampleOrderDTO.setAdminDispatchDate(sampleOrder.getAdminDispatchDate());
        sampleOrderDTO.setAdminDispatchTime(sampleOrder.getAdminDispatchTime());

        sampleOrderDTO.setBuyerReceiveDate(sampleOrder.getBuyerReceiveDate());
        sampleOrderDTO.setBuyerReceiveTime(sampleOrder.getBuyerReceiveTime());

        sampleOrderDTO.setBuyerApproveDate(sampleOrder.getBuyerApproveDate());
        sampleOrderDTO.setBuyerApproveTime(sampleOrder.getBuyerApproveTime());

        sampleOrderDTO.setBuyerRejectDate(sampleOrder.getBuyerRejectDate());
        sampleOrderDTO.setBuyerRejectTime(sampleOrder.getBuyerRejectTime());

        sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());

        return sampleOrderDTO;
    }

    @Override
    public SampleOrderDTO adminGetsSampleOrderByQId(String qId) {

        SampleOrder sampleOrder = sampleOrderRepository.findByQId(qId)
                .orElseThrow(() -> new RuntimeException("Sample Order not found with given qId:" + qId));

        SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();

        sampleOrderDTO.setSoId(sampleOrder.getSoId());
        sampleOrderDTO.setQId(sampleOrder.getQId());
        sampleOrderDTO.setBuyerUId(sampleOrder.getBuyerUId());
        sampleOrderDTO.setSellerUId(sampleOrder.getSellerUId());
        String buyerName = sellerBuyerRepository.findById(sampleOrder.getBuyerUId())
                .orElseThrow(() -> new RuntimeException("Buyer not found with Id : " + sampleOrder.getBuyerUId()))
                .getFullName();
        sampleOrderDTO.setBuyerName(buyerName);
        if (sampleOrder.getSellerUId() != null) {
            String sellerName = sellerBuyerRepository.findById(sampleOrder.getSellerUId())
                    .orElseThrow(() -> new RuntimeException("Seller not found with Id: " + sampleOrder.getSellerUId()))
                    .getFullName();
            sampleOrderDTO.setSellerName(sellerName);
        } else {
            sampleOrderDTO.setSellerName("N/A"); // Default or fallback value
        }

        Product product = productRepository.findById(sampleOrder.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

        sampleOrderDTO.setProductId(sampleOrder.getProductId());
        sampleOrderDTO.setProductName(product.getProductName());
        sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
        sampleOrderDTO.setProductFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
        sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
        sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

        sampleOrderDTO.setBuyerQuantity(sampleOrder.getBuyerQuantity());
        sampleOrderDTO.setBuyerUnit(sampleOrder.getBuyerUnit());
        sampleOrderDTO.setBuyerAddressId(sampleOrder.getBuyerAddressId());
        sampleOrderDTO.setBuyerAmount(sampleOrder.getBuyerAmount());
        sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
        sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());

        sampleOrderDTO.setAdminSendQtyToSeller(sampleOrder.getAdminSendQtyToSeller());
        sampleOrderDTO.setAdminUnit(sampleOrder.getAdminUnit());
        sampleOrderDTO.setAdminAddressId(sampleOrder.getAdminAddressId());
        sampleOrderDTO.setAdminEDDToSeller(sampleOrder.getAdminEDDToSeller());
        sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
        sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());

        sampleOrderDTO.setSellerPackagingDate(sampleOrder.getSellerPackagingDate());
        sampleOrderDTO.setSellerPackagingTime(sampleOrder.getSellerPackagingTime());

        sampleOrderDTO.setSellerDispatchDate(sampleOrder.getSellerDispatchDate());
        sampleOrderDTO.setSellerDispatchTime(sampleOrder.getSellerDispatchTime());

        sampleOrderDTO.setAdminReceiveDate(sampleOrder.getAdminReceiveDate());
        sampleOrderDTO.setAdminReceiveTime(sampleOrder.getAdminReceiveTime());

        sampleOrderDTO.setAdminProcessingDate(sampleOrder.getAdminProcessingDate());
        sampleOrderDTO.setAdminProcessingTime(sampleOrder.getAdminProcessingTime());

        sampleOrderDTO.setAdminDispatchDate(sampleOrder.getAdminDispatchDate());
        sampleOrderDTO.setAdminDispatchTime(sampleOrder.getAdminDispatchTime());

        sampleOrderDTO.setBuyerReceiveDate(sampleOrder.getBuyerReceiveDate());
        sampleOrderDTO.setBuyerReceiveTime(sampleOrder.getBuyerReceiveTime());

        sampleOrderDTO.setBuyerApproveDate(sampleOrder.getBuyerApproveDate());
        sampleOrderDTO.setBuyerApproveTime(sampleOrder.getBuyerApproveTime());

        sampleOrderDTO.setBuyerRejectDate(sampleOrder.getBuyerRejectDate());
        sampleOrderDTO.setBuyerRejectTime(sampleOrder.getBuyerRejectTime());

        sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());
        return sampleOrderDTO;
    }

    @Override
    public String adminSendsSampleOrderToSeller(String soId, SampleOrderDTO sampleOrderDTO) throws Exception {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));
        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));

        sampleOrder.setSellerUId(inquiry.getSellerUId());
        sampleOrder.setAdminSendQtyToSeller(sampleOrderDTO.getAdminSendQtyToSeller());
        sampleOrder.setAdminUnit(sampleOrderDTO.getAdminUnit());
        sampleOrder.setAdminAddressId(sampleOrderDTO.getAdminAddressId());
        sampleOrder.setAdminEDDToSeller(sampleOrderDTO.getAdminEDDToSeller());
        sampleOrder.setAdminSendToSellerDate(LocalDate.now());
        sampleOrder.setAdminSendToSellerTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Admin forwarded the sample request to seller.");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(8L));
        orderStatusRepository.save(orderStatus);

        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(8L));
        inquiryRepository.save(inquiry);

        return "You forwarded the sample request to seller.";
    }

    @Override
    public List<SampleOrderDTO> sellerGetsAllSampleOrders(String sellerUId) {
        return sampleOrderRepository.findAllBySellerUId(sellerUId).stream()
                .map(sampleOrder -> {
                    SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
                    sampleOrderDTO.setSoId(sampleOrder.getSoId());
                    sampleOrderDTO.setQId(sampleOrder.getQId());
                    sampleOrderDTO.setSellerUId(sampleOrder.getSellerUId());
                    String sellerName = sellerBuyerRepository.findById(sampleOrder.getSellerUId())
                            .orElseThrow(() -> new RuntimeException("Seller no found with Id : " + sampleOrder.getSellerUId()))
                            .getFullName();
                    sampleOrderDTO.setSellerName(sellerName);

                    Product product = productRepository.findById(sampleOrder.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

                    sampleOrderDTO.setProductId(sampleOrder.getProductId());
                    sampleOrderDTO.setProductName(product.getProductName());
                    sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
                    sampleOrderDTO.setProductFormName(product.getForms().stream()
                            .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
                    sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
                    sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                            .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                            .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

                    sampleOrderDTO.setAdminSendQtyToSeller(sampleOrder.getAdminSendQtyToSeller());
                    sampleOrderDTO.setAdminAddressId(sampleOrder.getAdminAddressId());
                    sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
                    sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());

                    return sampleOrderDTO;
                })
                .toList();
    }

    @Override
    public SampleOrderDTO sellerGetsSampleOrderById(String soId, String sellerUId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));
        if(! sampleOrder.getSellerUId().equals(sellerUId)){
            throw new RuntimeException("Access denied! This SampleOrder does not belong to the buyer with ID: " + sellerUId);
        }
        SampleOrderDTO sampleOrderDTO = new SampleOrderDTO();
        sampleOrderDTO.setSoId(sampleOrder.getSoId());
        sampleOrderDTO.setQId(sampleOrder.getQId());
        sampleOrderDTO.setSellerUId(sampleOrder.getSellerUId());
        String sellerName = sellerBuyerRepository.findById(sampleOrder.getSellerUId())
                .orElseThrow(() -> new RuntimeException("Seller no found with Id : " + sampleOrder.getSellerUId()))
                .getFullName();
        sampleOrderDTO.setSellerName(sellerName);

        Product product = productRepository.findById(sampleOrder.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found for ID: " + sampleOrder.getProductId()));

        sampleOrderDTO.setProductId(sampleOrder.getProductId());
        sampleOrderDTO.setProductName(product.getProductName());
        sampleOrderDTO.setProductFormId(sampleOrder.getProductFormId());
        sampleOrderDTO.setProductFormName(product.getForms().stream()
                .filter(form -> form.getFormId().equals(sampleOrder.getProductFormId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product form not found with ID: " + sampleOrder.getProductFormId())).getFormName());
        sampleOrderDTO.setProductVarietyId(sampleOrder.getProductVarietyId());
        sampleOrderDTO.setVarietyName(product.getVarieties().stream()
                .filter(variety -> variety.getVarietyId().equals(sampleOrder.getProductVarietyId())).findFirst()
                .orElseThrow(() -> new RuntimeException("Product variety not found with ID : " + sampleOrder.getProductVarietyId())).getVarietyName());

        String imageUrl = null;
        if (product.getProductImageUrl() != null) {
            imageUrl = s3Service.getProductImagePresignedUrl(product.getProductImageUrl());
        }
        sampleOrderDTO.setProductImageUrl(imageUrl);

        sampleOrderDTO.setBuyerRequestDate(sampleOrder.getBuyerRequestDate());
        sampleOrderDTO.setBuyerRequestTime(sampleOrder.getBuyerRequestTime());

        sampleOrderDTO.setAdminSendQtyToSeller(sampleOrder.getAdminSendQtyToSeller());
        sampleOrderDTO.setAdminUnit(sampleOrder.getAdminUnit());
        sampleOrderDTO.setAdminAddressId(sampleOrder.getAdminAddressId());
        sampleOrderDTO.setAdminEDDToSeller(sampleOrder.getAdminEDDToSeller());
        sampleOrderDTO.setAdminSendToSellerDate(sampleOrder.getAdminSendToSellerDate());
        sampleOrderDTO.setAdminSendToSellerTime(sampleOrder.getAdminSendToSellerTime());

        sampleOrderDTO.setSellerPackagingDate(sampleOrder.getSellerPackagingDate());
        sampleOrderDTO.setSellerPackagingTime(sampleOrder.getSellerPackagingTime());

        sampleOrderDTO.setSellerDispatchDate(sampleOrder.getSellerDispatchDate());
        sampleOrderDTO.setSellerDispatchTime(sampleOrder.getSellerDispatchTime());

        sampleOrderDTO.setAdminReceiveDate(sampleOrder.getAdminReceiveDate());
        sampleOrderDTO.setAdminReceiveTime(sampleOrder.getAdminReceiveTime());

        sampleOrderDTO.setAdminProcessingDate(sampleOrder.getAdminProcessingDate());
        sampleOrderDTO.setAdminProcessingTime(sampleOrder.getAdminProcessingTime());

        sampleOrderDTO.setAdminDispatchDate(sampleOrder.getAdminDispatchDate());
        sampleOrderDTO.setAdminDispatchTime(sampleOrder.getAdminDispatchTime());

        sampleOrderDTO.setBuyerReceiveDate(sampleOrder.getBuyerReceiveDate());
        sampleOrderDTO.setBuyerReceiveTime(sampleOrder.getBuyerReceiveTime());

        sampleOrderDTO.setBuyerApproveDate(sampleOrder.getBuyerApproveDate());
        sampleOrderDTO.setBuyerApproveTime(sampleOrder.getBuyerApproveTime());

        sampleOrderDTO.setBuyerRejectDate(sampleOrder.getBuyerRejectDate());
        sampleOrderDTO.setBuyerRejectTime(sampleOrder.getBuyerRejectTime());

        sampleOrderDTO.setStatus(sampleOrder.getCurrentStatus());

        return sampleOrderDTO;
    }

//    @Override
//    public String sellerApproveSampleOrder(String soId, String sellerUId) {
//
//        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
//                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));
//
//        sampleOrder.setSellerRespondDate(LocalDate.now());
//        sampleOrder.setSellerRespondTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
//        sampleOrder.setCurrentStatus("Seller Accepted the sample Order");
//        sampleOrderRepository.save(sampleOrder);
//
//        OrderStatus orderStatus = new OrderStatus();
//        orderStatus.setOId(sampleOrder.getQId());
//        orderStatus.setStatus(statusRepository.findSMeaningBySId(9L));
//        orderStatusRepository.save(orderStatus);
//
//        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
//                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
//        inquiry.setOsId(orderStatus.getOsId());
//        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(9L));
//        inquiryRepository.save(inquiry);
//
//        return "You accepted the sample order.";
//    }
//
//    @Override
//    public String sellerDeclineSampleOrder(String soId, String sellerUId) {
//        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
//                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));
//        sampleOrder.setSellerRespondDate(LocalDate.now());
//        sampleOrder.setSellerRespondTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
//        sampleOrder.setCurrentStatus("Seller Declined the sample Order");
//
//        OrderStatus orderStatus = new OrderStatus();
//        orderStatus.setOId(sampleOrder.getQId());
//        orderStatus.setStatus(statusRepository.findSMeaningBySId(10L));
//        orderStatusRepository.save(orderStatus);
//
//        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
//                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
//        inquiry.setOsId(orderStatus.getOsId());
//        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(10L));
//        inquiryRepository.save(inquiry);
//        return "You declined the sample order.";
//    }

    @Override
    public String sellerPackagingSample(String soId, String sellerUId) {

        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setSellerPackagingDate(LocalDate.now());
        sampleOrder.setSellerPackagingTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Seller Packaging the sample Order");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(11L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(11L));
        inquiryRepository.save(inquiry);

        return "You started packaging Sample";
    }

    @Override
        public String sellerDispatchSampleToAdmin(String soId, String sellerUId) {
            SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                    .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

            sampleOrder.setSellerDispatchDate(LocalDate.now());
            sampleOrder.setSellerDispatchTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
            sampleOrder.setCurrentStatus("Seller Dispatched the sample Order");
            sampleOrderRepository.save(sampleOrder);

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setOId(sampleOrder.getQId());
            orderStatus.setStatus(statusRepository.findSMeaningBySId(12L));
            orderStatusRepository.save(orderStatus);

            Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                    .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
            inquiry.setOsId(orderStatus.getOsId());
            inquiry.setOrderStatus(statusRepository.findSMeaningBySId(12L));
            inquiryRepository.save(inquiry);
        return "You dispatched packaging Sample";
    }

    @Override
    public String adminReceivedSample(String soId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setAdminReceiveDate(LocalDate.now());
        sampleOrder.setAdminReceiveTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Admin Received the Sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(13L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(13L));
        inquiryRepository.save(inquiry);
        return "You Received the Sample";
    }

    @Override
    public String adminProcessingSample(String soId) {

        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setAdminProcessingDate(LocalDate.now());
        sampleOrder.setAdminProcessingTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Admin Processed the sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(14L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(14L));
        inquiryRepository.save(inquiry);
        return "You started Processing the Sample";
    }

    @Override
    public String adminDispatchToBuyer(String soId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setAdminDispatchDate(LocalDate.now());
        sampleOrder.setAdminDispatchTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Admin dispatched the sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(15L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(15L));
        inquiryRepository.save(inquiry);
        return "You dispatched the Sample";
    }

    @Override
    public String buyerReceivedSample(String soId, String buyerUId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setBuyerReceiveDate(LocalDate.now());
        sampleOrder.setBuyerReceiveTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Buyer received the sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(16L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(16L));
        inquiryRepository.save(inquiry);
        return "You received the sample";
    }

    @Override
    public String buyerApprovedSample(String soId, String buyerUId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setBuyerApproveDate(LocalDate.now());
        sampleOrder.setBuyerApproveTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Buyer approved the sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(17L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(17L));
        inquiryRepository.save(inquiry);
        return "You approved the sample";
    }

    @Override
    public String buyerRejectedSample(String soId, String buyerUId) {
        SampleOrder sampleOrder = sampleOrderRepository.findById(soId)
                .orElseThrow(() -> new RuntimeException("SampleOrder not found with ID: " + soId));

        sampleOrder.setBuyerRejectDate(LocalDate.now());
        sampleOrder.setBuyerRejectTime(LocalTime.now().truncatedTo(ChronoUnit.SECONDS));
        sampleOrder.setCurrentStatus("Buyer rejected the sample");
        sampleOrderRepository.save(sampleOrder);

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOId(sampleOrder.getQId());
        orderStatus.setStatus(statusRepository.findSMeaningBySId(18L));
        orderStatusRepository.save(orderStatus);

        Inquiry inquiry = inquiryRepository.findByQId(sampleOrder.getQId())
                .orElseThrow(() -> new RuntimeException("Inquiry not found with given Inquiry Id : " + sampleOrder.getQId()));
        inquiry.setOsId(orderStatus.getOsId());
        inquiry.setOrderStatus(statusRepository.findSMeaningBySId(18L));
        inquiryRepository.save(inquiry);
        return "You rejected the sample";
    }

    @Override
    public List<SellerBuyer> buyersHavingCheque() {
        return sellerBuyerRepository.findAll().stream()
                .filter(sellerBuyer ->  sellerBuyer.getCancelledChequeUrl() != null)
                .collect(Collectors.toList());
    }

}














