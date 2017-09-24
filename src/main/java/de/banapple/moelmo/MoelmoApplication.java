package de.banapple.moelmo;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MoelmoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoelmoApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(ApplicationContext context) {

		return args -> {

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
			System.out.println("mat = " + mat.dump());
			
		};
	}
}
