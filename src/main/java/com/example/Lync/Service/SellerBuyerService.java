package com.example.Lync.Service;

import com.example.Lync.DTO.SellerBuyerDTO;
import com.example.Lync.DTO.SellerProductDTO;
import com.example.Lync.Entity.FavouriteCategory;
import com.example.Lync.Entity.FavouriteProduct;
import com.example.Lync.Entity.SellerBuyer;
import com.example.Lync.Entity.SellerProduct;

import java.util.List;
import java.util.Optional;

public interface SellerBuyerService {

    SellerBuyer findbyPhoneNumber(String phoneNumber);

    void createSeller(SellerBuyerDTO sellerBuyerDTO);
    void createBuyer(SellerBuyerDTO sellerBuyerDTO);

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

    //Seller Products -----------------------------------------------------------------------------
    public SellerProduct addSellerProduct(SellerProductDTO sellerProductDTO) throws Exception;
    public List<SellerProduct> getSellerProductsBySeller(String sellerId) ;
    public Optional<SellerProduct> getSellerProductById(String spId);

    List<String> allEmail();
    }
