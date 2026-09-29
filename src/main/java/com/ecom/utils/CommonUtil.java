package com.ecom.utils;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;


@Component
public class CommonUtil {

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	@Lazy
	private UserService userService;
	
//    @Autowired
//	private AmazonS3 amazonS3;
//	
//	@Value("${aws.s3.bucket.category}")
//	private String categoryBucket;
//	
//	@Value("${aws.s3.bucket.product}")
//	private String productBucket;
//	
//	@Value("${aws.s3.bucket.profile}")
//	private String profileBucket;
//	

	public Boolean sendMail(String url, String reciepentEmail) throws UnsupportedEncodingException, MessagingException {

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom("auramart.support@gmail.com", "AuraMart");
		helper.setTo(reciepentEmail);

		String content = "<p>Hello,</p>" + "<p>You have requested to reset your password "
				+ "of your auramart account.</p>"
				+ "<p>Click the link below to change your password:</p>" + "<p><a href=\"" + url
				+ "\">Change my password</a></p>";
		helper.setSubject("Password Reset");
		helper.setText(content, true);
		mailSender.send(message);
		return true;
	}

	public static String generateUrl(HttpServletRequest request) {

		// http://localhost:8080/forgot-password
		String siteUrl = request.getRequestURL().toString();

		return siteUrl.replace(request.getServletPath(), "");
	}
	
	String msg=null;;
	
	public Boolean sendMailForProductOrder(ProductOrder order,String status) throws Exception
	{
		
		msg = "<html>"
			    + "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>"
			    + "  <h2 style='color: #2c3e50;'>Hello [[name]],</h2>"
			    + "  <p>Thank you for shopping with <strong>AuraMart</strong>! Your order status is now: <span style='color: #e67e22;'><strong>[[orderStatus]]</strong></span>.</p>"
			    + "  <hr style='border: 0; border-top: 1px solid #eee;' />"
			    + "  <p><strong>Order Details:</strong></p>"
			    + "  <ul style='list-style: none; padding: 0;'>"
			    + "    <li><strong>Product:</strong> [[productName]]</li>"
			    + "    <li><strong>Category:</strong> [[category]]</li>"
			    + "    <li><strong>Quantity:</strong> [[quantity]]</li>"
			    + "    <li><strong>Price:</strong> ₹[[price]]</li>"
			    + "    <li><strong>Payment:</strong> [[paymentType]]</li>"
			    + "  </ul>"
			    + "  <p>Your order is expected to be delivered within <strong>5 working days</strong>.</p>"
			    + "  <br />"
			    + "  <p>Best regards,<br /><strong>Team AuraMart</strong></p>"
			    + "</body>"
			    + "</html>";	
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("auramart.support@gmail.com", "AuraMart");
		helper.setTo(order.getOrderAddress().getEmail());

		msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
		msg=msg.replace("[[orderStatus]]",status);
		msg=msg.replace("[[productName]]", order.getProduct().getTitle());
		msg=msg.replace("[[category]]", order.getProduct().getCategory());
		msg=msg.replace("[[quantity]]", order.getQuantity().toString());
		msg=msg.replace("[[price]]", order.getPrice().toString());
		msg=msg.replace("[[paymentType]]", order.getPaymentType());
		
		helper.setSubject("Product Order Status");
		helper.setText(msg, true);
		mailSender.send(message);
		return true;
	}
	
	public UserDtls getLoggedInUserDetails(Principal p) {
		String email = p.getName();
		UserDtls userDtls = userService.getUserByEmail(email);
		return userDtls;
	}
	
	public String getImageUrl(MultipartFile file, Integer bucketType) {

	    if (file == null || file.isEmpty()) {
	        return "/img/default.jpg";
	    }

	    String folder;

	    if (bucketType == BucketType.CATEGORY.getId()) {
	        folder = "category_img";
	    } else if (bucketType == BucketType.PRODUCT.getId()) {
	        folder = "product_img";
	    } else if (bucketType == BucketType.PROFILE.getId()) {
	        folder = "profile_img";
	    } else {
	        folder = "other";
	    }

	    return "/img/" + folder + "/" + file.getOriginalFilename();
	}

}