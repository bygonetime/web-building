package com.github.emailtohl.building.bootspring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.emailtohl.building.config.RootContextConfiguration;

/**
 * 加载main环境下的spring配置
 * 
 * @author HeLei
 * @date 2017.02.04
 */
public class Spring {
	private static AnnotationConfigApplicationContext context;
	
	private Spring() {
	}
	
	public synchronized static AnnotationConfigApplicationContext getApplicationContext() {
		if (context == null) {
			context = new AnnotationConfigApplicationContext();
			context.getEnvironment().setActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT);
			context.register(SpringConfigForTest.class);
			context.refresh();
		}
		return context;
	}
	
}
