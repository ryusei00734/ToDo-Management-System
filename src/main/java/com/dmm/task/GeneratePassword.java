package com.dmm.task;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeneratePassword {

	public static void main(String[] args) {
		// ハッシュ化したいパスワードを入力
		String rawPassword = "admin";

		// パスワードをハッシュ化
		String password = getEncodePassword(rawPassword);

		// ハッシュ化された値を表示
		System.out.println(password);
	}

	private static String getEncodePassword(String rawPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(rawPassword);
	}
}