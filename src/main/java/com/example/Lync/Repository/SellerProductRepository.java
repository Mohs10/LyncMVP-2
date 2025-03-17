package com.example.Lync.Repository;

import com.example.Lync.Entity.SellerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SellerProductRepository extends JpaRepository<SellerProduct, String> {
    List<SellerProduct> findBySellerId(String sellerId);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.productId = :productId")
    List<SellerProduct> findByPId(@Param("productId") Long productId);

    @Query("SELECT sp FROM SellerProduct sp JOIN sp.specifications sps " +
            "WHERE sp.productId = :productId AND sp.productFormId = :productFormId AND sp.productVarietyId = :productVarietyId AND sp.certificationName = :certificationName " +
            "AND sps.specificationName IN :specificationNames")
    List<SellerProduct> findBySpecificationAndProductAttributes(List<String> specificationNames, Long productId, Long productFormId, Long productVarietyId, String certificationName);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.productId = :productId AND sp.productFormId = :productFormId AND sp.productVarietyId = :productVarietyId AND sp.certificationName = :certificationName")
    List<SellerProduct> findByProductIdAndProductFormIdAndProductVarietyIdAndCertificationName(@Param("productId") Long productId, @Param("productFormId") Long productFormId, @Param("productVarietyId") Long productVarietyId, @Param("certificationName") String certificationName);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.productId = :productId AND sp.productVarietyId = :productVarietyId AND sp.certificationName = :certificationName")
    List<SellerProduct> findByProductIdAndProductVarietyIdAndCertificationName(@Param("productId") Long productId, @Param("productVarietyId") Long productVarietyId, @Param("certificationName") String certificationName);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.productId = :productId AND sp.productVarietyId = :productVarietyId")
    List<SellerProduct> findByProductIdAndProductVarietyId(@Param("productId") Long productId, @Param("productVarietyId") Long productVarietyId);

    @Query("SELECT sp FROM SellerProduct sp WHERE sp.sellerId = :sellerId AND sp.productId = :productId")
    List<SellerProduct> findBySellerIdAndProductId(@Param("sellerId") String sellerId, @Param("productId") Long productId);


//    @Query(value = "SELECT COUNT(*) > 0 FROM lync.seller_product WHERE product_id = :productId " +
//            "AND product_variety_id = :productVarietyId AND product_form_id = :productFormId " +
//            "AND seller_id = :sellerId", nativeQuery = true)
//    Boolean checkSellerProduct(@Param("productId") Long productId,
//                               @Param("productVarietyId") Long productVarietyId,
//                               @Param("productFormId") Long productFormId,
//                               @Param("sellerId") String sellerId);

    @Procedure(procedureName = "CheckSellerProduct")
    Boolean checkSellerProduct(Long p_product_id, Long p_product_variety_id, Long p_product_form_id, String p_seller_id);
}

