package com.ecom.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.FileService;
import com.ecom.service.ProductService;
import com.ecom.utils.BucketType;
import com.ecom.utils.CommonUtil;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private ProductRepository productRepository;

	private static final long serialVersionUID = 1L;
	
	@Override
	@CachePut(value = "product", key = "#result.id")
	@CacheEvict(value = { "products", "activeProducts", "categories" }, allEntries = true)
	public Product saveProduct(Product product) {
	    return productRepository.save(product);
	}
	
	
	@Override
	@Cacheable("products")
	public List<Product> getAllProducts() {

	    System.out.println("Fetching All Products From MySQL...");

	    return productRepository.findAll();

	}

	@Override
	public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findAll(pageable);
	}

	@Override
	@CacheEvict(value = { "product", "products", "activeProducts", "categories" },
	        key = "#id",
	        allEntries = true)
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false;
	}

	@Override
	@Cacheable(value = "product", key = "#id")
	public Product getProductById(Integer id) {

	    System.out.println("Fetching Product From MySQL...");

	    return productRepository.findById(id).orElse(null);

	}

	@Override
	@CachePut(value = "product", key = "#product.id")
	@CacheEvict(value = { "products", "activeProducts", "categories" }, allEntries = true)
	public Product updateProduct(Product product, MultipartFile image) {
	    Product dbProduct = getProductById(product.getId());

	    // 1. Update all the text details
	    dbProduct.setTitle(product.getTitle());
	    dbProduct.setDescription(product.getDescription());
	    dbProduct.setCategory(product.getCategory());
	    dbProduct.setPrice(product.getPrice());
	    dbProduct.setStock(product.getStock());
	    dbProduct.setIsActive(product.getIsActive());
	    dbProduct.setDiscount(product.getDiscount());

	    // 2. ONLY update the image if the user uploaded a new one
	    if (image != null && !image.isEmpty()) {
	        String imageUrl = commonUtil.getImageUrl(image, BucketType.PRODUCT.getId());
	        dbProduct.setImage(imageUrl);
	    }
	    // If image is empty, we do nothing. The old S3 link stays in dbProduct!

	    // 3. Calculate discount
	    Double disocunt = product.getPrice() * (product.getDiscount() / 100.0);
	    Double discountPrice = product.getPrice() - disocunt;
	    dbProduct.setDiscountPrice(discountPrice);

	    Product updateProduct = productRepository.save(dbProduct);

	    if (!ObjectUtils.isEmpty(updateProduct)) {
	        if (image != null && !image.isEmpty()) { // Check again before S3 upload
	            try {
	                fileService.uploadFilesS3(image, BucketType.PRODUCT.getId());
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return updateProduct; // Return the updated object
	    }
	    return null;
	}
	@Override
	@Cacheable(value = "activeProducts", key = "#category")
	public List<Product> getAllActiveProducts(String category) {
		List<Product> products = null;
		if (ObjectUtils.isEmpty(category)) {
			products = productRepository.findByIsActiveTrue();
		} else {
			products = productRepository.findByCategory(category);
		}

		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
	}

	@Override
	public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
	}

	@Override
	public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Product> pageProduct = null;

		if (ObjectUtils.isEmpty(category)) {
			pageProduct = productRepository.findByIsActiveTrue(pageable);
		} else {
			pageProduct = productRepository.findByCategory(pageable, category);
		}
		return pageProduct;
	}

	@Override
	public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {

		Page<Product> pageProduct = null;
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		pageProduct = productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,
				ch, pageable);

//		if (ObjectUtils.isEmpty(category)) {
//			pageProduct = productRepository.findByIsActiveTrue(pageable);
//		} else {
//			pageProduct = productRepository.findByCategory(pageable, category);
//		}
		return pageProduct;
	}

}