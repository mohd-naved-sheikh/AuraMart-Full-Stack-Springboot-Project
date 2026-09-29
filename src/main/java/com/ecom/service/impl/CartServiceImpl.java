package com.ecom.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserRepository;
import com.ecom.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProductRepository productRepository;

	@Override
	public Cart saveCart(Integer productId, Integer userId) {

	    UserDtls userDtls = userRepository.findById(userId).get();
	    Product product = productRepository.findById(productId).get();

	    
	    if (product.getStock() <= 0) {
	        return null;
	    }

	    Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

	    Cart cart = null;

	    if (ObjectUtils.isEmpty(cartStatus)) {

	        cart = new Cart();
	        cart.setProduct(product);
	        cart.setUser(userDtls);
	        cart.setQuantity(1);
	        cart.setTotalPrice(product.getDiscountPrice());

	    } else {

	        cart = cartStatus;

	       
	        if (cart.getQuantity() >= product.getStock()) {
	            return null;
	        }

	        cart.setQuantity(cart.getQuantity() + 1);
	        cart.setTotalPrice(
	                cart.getQuantity() * cart.getProduct().getDiscountPrice()
	        );
	    }

	    return cartRepository.save(cart);
	}
	@Override
	public List<Cart> getCartsByUser(Integer userId) {
		List<Cart> carts = cartRepository.findByUserId(userId);

		Double totalOrderPrice = 0.0;
		List<Cart> updateCarts = new ArrayList<>();
		for (Cart c : carts) {
			Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());
			c.setTotalPrice(totalPrice);
			totalOrderPrice = totalOrderPrice + totalPrice;
			c.setTotalOrderPrice(totalOrderPrice);
			updateCarts.add(c);
		}

		return updateCarts;
	}

	@Override
	public Integer getCountCart(Integer userId) {
		Integer countByUserId = cartRepository.countByUserId(userId);
		return countByUserId;
	}

	@Override
	public void updateQuantity(String sy, Integer cid) {

	    Cart cart = cartRepository.findById(cid).get();

	    int updateQuantity;

	    if (sy.equalsIgnoreCase("de")) {

	        updateQuantity = cart.getQuantity() - 1;

	        if (updateQuantity <= 0) {

	            cartRepository.delete(cart);

	        } else {

	            cart.setQuantity(updateQuantity);
	            cart.setTotalPrice(
	                    updateQuantity * cart.getProduct().getDiscountPrice()
	            );

	            cartRepository.save(cart);
	        }

	    } else {

	        updateQuantity = cart.getQuantity() + 1;

	        // STOCK CHECK
	        if (updateQuantity > cart.getProduct().getStock()) {
	            return;
	        }

	        cart.setQuantity(updateQuantity);
	        cart.setTotalPrice(
	                updateQuantity * cart.getProduct().getDiscountPrice()
	        );

	        cartRepository.save(cart);
	    }
	}	
	
	@Override
	public void clearCartByUser(Integer userId) {
		cartRepository.deleteByUserId(userId);
	}

}