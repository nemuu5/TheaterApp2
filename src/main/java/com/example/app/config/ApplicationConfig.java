package com.example.app.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.app.login.AdminAuthFilter;
import com.example.app.login.StudentAuthFilter;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

	// バリデーションメッセージのカスタマイズ
	// Springのフォームバリデーションで表示されるエラーメッセージを
	// 日本語などにカスタマイズするための設定
	@Override
	public Validator getValidator() {
		var validator = new LocalValidatorFactoryBean();
		validator.setValidationMessageSource(messageSource());
		return validator;
	}
	
	// validation.properties または validation_ja.properties などを
	// リソースフォルダに配置すれば、そのファイルの中のメッセージを使えます
    @Bean
    MessageSource messageSource() {
		var messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("validation");
		return messageSource;
	}

    // 認証用フィルタの有効化
    // 管理者用ページ（URLが /admin/material/* や /admin/student/* で始まるもの）にアクセスした時、
    // AdminAuthFilter を通すようにしています。
    // 目的：管理者かどうかをチェックして、不正アクセスをブロックすること。
    @Bean
    FilterRegistrationBean<AdminAuthFilter> adminAuthFilter() {
 		var bean = new FilterRegistrationBean<AdminAuthFilter>(new AdminAuthFilter());
 		bean.addUrlPatterns("/admin/material/*");
 		bean.addUrlPatterns("/admin/student/*");
 		return bean;
 	}

    // Student用フィルタの登録
    // 一般ユーザー（生徒）用のページに対して StudentAuthFilter を適用。
    // たとえば / や /rental/* のURLにアクセスする際に、ログイン済みかどうかをチェックできます
    @Bean
    FilterRegistrationBean<StudentAuthFilter> studentAuthFilter() {
 		var bean = new FilterRegistrationBean<StudentAuthFilter>(new StudentAuthFilter());
 		bean.addUrlPatterns("/");
 		bean.addUrlPatterns("/rental/*");
 		return bean;
 	}

}
