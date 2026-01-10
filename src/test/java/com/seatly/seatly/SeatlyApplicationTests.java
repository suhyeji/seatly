package com.seatly.seatly;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeatlyApplicationTests {

	@Autowired
	private StringEncryptor encryptor;

	@Test
	void contextLoads() {
	}

	@Test
	void encrypt() {
		String plain = "yejiyoon813";
		String encrypted = encryptor.encrypt(plain);

		System.out.println("ENC(" + encrypted + ")");
	}

}
