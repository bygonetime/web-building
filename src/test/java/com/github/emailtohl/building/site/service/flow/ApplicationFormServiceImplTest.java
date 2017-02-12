package com.github.emailtohl.building.site.service.flow;

import static com.github.emailtohl.building.initdb.PersistenceData.bar;
import static com.github.emailtohl.building.initdb.PersistenceData.baz;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.emailtohl.building.bootspring.SpringConfigForTest;
import com.github.emailtohl.building.config.RootContextConfiguration;
import com.github.emailtohl.building.site.dao.flow.ApplicationFormRepository;
import com.github.emailtohl.building.site.dao.flow.ApplicationHandleHistoryRepository;
import com.github.emailtohl.building.site.dao.user.UserRepository;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm;
import com.github.emailtohl.building.site.entities.flow.ApplicationHandleHistory;
import com.github.emailtohl.building.site.entities.flow.ApplicationForm.Status;
import com.github.emailtohl.building.site.service.flow.ApplicationFormService;
import com.github.emailtohl.building.stub.SecurityContextManager;
/**
 * 业务类测试
 * @author HeLei
 * @date 2017.02.04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfigForTest.class)
@ActiveProfiles(RootContextConfiguration.PROFILE_DEVELPMENT)
public class ApplicationFormServiceImplTest {
	static final Logger logger = LogManager.getLogger();
	@Inject UserRepository userRepository;
	@Inject ApplicationFormRepository applicationFormRepository;
	@Inject ApplicationHandleHistoryRepository applicationHandleHistoryRepository;
	@Inject ApplicationFormService applicationFormService;
	@Inject SecurityContextManager securityContextManager;
	private final String title = "test";
	private final String description = "test content";
	private Pageable pageable = new PageRequest(0, 20);
	private Long id;
	
	@Before
	public void setUp() throws Exception {
		securityContextManager.setBaz();
		id = applicationFormService.application(title, description);
	}

	@After
	public void tearDown() throws Exception {
		securityContextManager.setEmailtohl();
		if (id != null) {
			/*
			Page<ApplicationHandleHistory> page = applicationHandleHistoryRepository.findByApplicationFormId(id, pageable);
			for (Iterator<ApplicationHandleHistory> i = page.getContent().iterator(); i.hasNext();) {
				ApplicationHandleHistory h = i.next();
				logger.debug(h);
				applicationHandleHistoryRepository.delete(h.getId());
			}
			applicationFormRepository.delete(id);
			*/
			applicationFormService.delete(id);
		}
	}

	@Test
	public void testFindById() {
		securityContextManager.setBar();
		if (id != null) {
			ApplicationForm e = applicationFormService.findById(id);
			assertNotNull(e);
		}
	}
	
	@Test
	public void testFindByNameLike() {
		securityContextManager.setBar();
		Page<ApplicationForm> page = applicationFormService.findByNameLike(title.substring(0, title.length() - 1) + '%', pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindByStatus() {
		securityContextManager.setBar();
		Page<ApplicationForm> page = applicationFormService.findByStatus(Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	public void testFindMyApplicationForm() {
		securityContextManager.setBaz();
		Page<ApplicationForm> page = applicationFormService.findMyApplicationForm(pageable);
		assertTrue(page.getTotalElements() > 0);
	}
	
	@Test
	public void testFindByNameLikeAndStatus() {
		securityContextManager.setBaz();
		Page<ApplicationForm> page = applicationFormService.findByNameAndStatus(title, Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(null, Status.REQUEST, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(title, null, pageable);
		assertTrue(page.getTotalElements() > 0);
		
		page = applicationFormService.findByNameAndStatus(null, null, pageable);
		assertTrue(page.getTotalElements() > 0);
	}

	@Test
	//ApplicationForm#getApplicationHandleHistory()使用懒加载，事务不能在service层关闭，所以在此添加上@Transactional
	@Transactional
	public void testTransit() {
		securityContextManager.setBar();
		String cause = "缘由是：……";
		if (id != null) {
			applicationFormService.transit(id, Status.REJECT, cause);
			ApplicationForm af = applicationFormService.findById(id);
			assertEquals(Status.REJECT, af.getStatus());
			assertEquals(cause, af.getCause());
			ApplicationHandleHistory history = af.getApplicationHandleHistory().iterator().next();
			assertNotNull(history);
			
			Instant now = Instant.now();
			Date start = Date.from(now.minusSeconds(1000));
			Date end = Date.from(now.plusSeconds(100));
			Page<ApplicationHandleHistory> page = applicationFormService.historyFindByCreateDateBetween(start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateGreaterThanEqual(start, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByCreateDateLessThanEqual(end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByHandlerEmailLike(bar.getEmail(), pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.historyFindByStatus(Status.REJECT, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			page = applicationFormService.history(baz.getEmail(), null, title, Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			page = applicationFormService.history(null, bar.getEmail(), title, Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			page = applicationFormService.history("", "", title, Status.REJECT, start, end, pageable);
			assertTrue(page.getTotalElements() > 0);
			
			List<ApplicationHandleHistory> ls = applicationFormService.findHistoryByApplicationFormId(id);
			assertFalse(ls.isEmpty());
			
		} else {
			fail("没有持久化");
		}
	}

}
