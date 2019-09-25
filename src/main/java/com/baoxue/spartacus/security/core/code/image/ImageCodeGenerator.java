package com.baoxue.spartacus.security.core.code.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.baoxue.spartacus.security.core.code.ValidateCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.baoxue.spartacus.security.core.properties.SecurityProperties;

@Component("imageValidateCodeGenerator")
public class ImageCodeGenerator implements ValidateCodeGenerator {
	
	@Autowired
	private SecurityProperties securityProperties;
	
	private int width;
	private int height;
	private Random random = new Random();
	
	private String text;
	private Color bgColor = new Color(45, 49, 52);
	private String codes = "23456789abcdefghjkmnopqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
	private String[] fontNames = { "宋体", "Times New Roman", "华文楷体", "黑体", "微软雅黑", "楷体_GB2312" };
	
	
	private Font randomFont() {
		int index = random.nextInt(fontNames.length);
		String fontName = fontNames[index];
		int style = random.nextInt(4);
		int size = random.nextInt(5) + 24;
		return new Font(fontName, style, size);
	}

	private void drawLine(BufferedImage image) {
		int num = 3;
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		for (int i = 0; i < num; i++) {
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			int x2 = random.nextInt(width);
			int y2 = random.nextInt(height);
			g2.setStroke(new BasicStroke(1.5F));
			g2.setColor(new Color(48, 212, 227));
			g2.drawLine(x1, y1, x2, y2);
		}
	}

	private BufferedImage createImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.setColor(bgColor);
		g2.fillRect(0, 0, width, height);
		return image;
	}
	
	
	@Override
	public ImageCode generate(ServletWebRequest request) {
		width = ServletRequestUtils.getIntParameter(request.getRequest(), "width", securityProperties.getCode().getImage().getWidth());
		height = ServletRequestUtils.getIntParameter(request.getRequest(), "height", securityProperties.getCode().getImage().getHeight());
		
		BufferedImage image = createImage(width, height);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		StringBuilder sb = new StringBuilder();
		// 画字符
		int n = securityProperties.getCode().getImage().getLength();
		for (int i = 0; i < n; i++) {
			String sText = codes.charAt(random.nextInt(codes.length())) + "";
			sb.append(sText);
			float x = i * 1.0F * width / n + 1.0F * width / n / n + 1.0F * width / n / n /n;
			g2.setFont(randomFont());
			g2.setColor(new Color(48, 212, 227));
			g2.drawString(sText, x, height*0.8F);
		}
		
		drawLine(image);
		text = sb.toString();
		return new ImageCode(image, text, securityProperties.getCode().getImage().getExpireIn());
	}
	
}
