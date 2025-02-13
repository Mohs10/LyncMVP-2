package com.example.Lync.Service;

import com.example.Lync.DTO.PriceRangeProjection;
import com.example.Lync.DTO.SellerBuyerAddressDTO;
import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.Entity.FavouriteCategory;
import com.example.Lync.Entity.FavouriteProduct;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.SellerProduct;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SellerBuyerService {

    SellerBuyer findbyPhoneNumber(String phoneNumber);

    void createSeller(SellerBuyerDTO sellerBuyerDTO) throws IOException;
    void createBuyer(SellerBuyerDTO sellerBuyerDTO) throws IOException;

    public boolean isPhoneNumberInCache(String phoneNumber);
    public boolean isEmailInCache(String email);

    public List<SellerBuyerDTO> allSellerBuyers();
    public  SellerBuyerDTO findSellerBuyerById(String uID);

    public FavouriteProduct addFavouriteProduct(String userId, Long productId);
    public List<FavouriteProduct> getFavouriteProductsByUser(String userId) ;
    public List<FavouriteProduct> getFavouriteProductsByProduct(Long productId) ;
    public Optional<FavouriteProduct> getFavouriteProductById(Long favProductId) ;

    public FavouriteCategory addFavouriteCategory(String userId, Long categoryId) ;

    public List<FavouriteCategory> getFavouriteCategoriesByCategory(Long categoryId) ;

    public Optional<FavouriteCategory> getFavouriteCategoryById(Long favCategoryId) ;
    public List<FavouriteCategory> getFavouriteCategoriesByUser(String userId) ;

    public void becomeBuyer(String userId) ;

    public void becomeSeller(String userId) ;

    void editSellerBuyer(String userId, SellerBuyerDTO sellerBuyerDTO);
    public String uploadProfilePicture(String userId, MultipartFile profilePicture) throws IOException;
    public String uploadCertificate(String userId, MultipartFile certificate) throws IOException ;

        public String uploadCancelledCheque(String userId, MultipartFile cancelledCheque) throws IOException;

    //Seller Products -----------------------------------------------------------------------------
    public SellerProduct addSellerProduct(SellerProductDTO sellerProductDTO) throws Exception;
    public String uploadSellerProductImage1(String sellerProductId, MultipartFile productImage) throws IOException ;
    public String uploadSellerProductImage2(String sellerProductId, MultipartFile productImage) throws IOException ;
    public String uploadSellerProductCertificate(String sellerProductId, MultipartFile productCertificate) throws IOException ;

        public SellerProduct editSellerProduct(SellerProduct existingSellerProduct, SellerProductDTO sellerProductDTO) throws Exception ;
    public String updateInventory(String sellerProductId, Double updatedAvailableAmount) ;

        public List<SellerProduct> getSellerProductsBySeller(String sellerId) ;
    public List<SellerProductDTO> getSellerProductDTOsBySeller(String sellerId) ;

        public Optional<SellerProduct> getSellerProductById(String spId);
    public  SellerProductDTO toDTO(SellerProduct sellerProduct);

    PriceRangeProjection priceRangeByProductId(Long productId);

    List<String> allEmail();

    String enableWaiveSampleFree(String userId, Boolean enable);

    String disableWaiveSampleFree(String userId, Boolean disable);

    String addAddress(String userId, SellerBuyerAddressDTO sellerBuyerAddressDTO);

    List<SellerBuyerAddressDTO> userGetsAddresses(String userId);

    SellerBuyerAddressDTO userGetAddressById(String userId, Long uaId);

    public SellerBuyerDTO convertToSellerBuyerDTO(SellerBuyer sellerBuyer);


    public String changeStatusForSellerBuyer(String userId, Boolean isActive);

    public Boolean checkIfProductExists(Long productId, Long productVarietyId, Long productFormId, String sellerId) ;
    }
