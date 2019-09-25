package com.baoxue.spartacus.security.core.code.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import com.baoxue.spartacus.security.core.code.ValidateCode;


/**
 * @author zhailiang
 *
 */
public class ImageCode extends ValidateCode {
	
	private static final long serialVersionUID = 1L;
	
	private BufferedImage image; 
	
	public ImageCode(BufferedImage image, String code, int expireIn){
		super(code, expireIn);
		this.image = image;
	}
	
	public ImageCode(BufferedImage image, String code, LocalDateTime expireTime){
		super(code, expireTime);
		this.image = image;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

}
